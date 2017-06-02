package com.gmail.nuclearcat1337.snitch_master.handlers;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.api.IAlertRecipient;
import com.gmail.nuclearcat1337.snitch_master.api.SnitchAlert;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchTags;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import com.gmail.nuclearcat1337.snitch_master.util.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mr_Little_Kitty on 6/30/2016.
 * Handles the parsing of Snitched from the /jalist command.
 */
public class ChatSnitchParser
{
    private static final Pattern jaListPattern = Pattern.compile("\\s*World: (\\S*)\\sLocation: \\[([-\\d]+) ([-\\d]+) ([-\\d]+)\\]\\sHours to cull: ([-\\d]*)\\sGroup: (\\S*)\\sName: (\\S*)\\s*", Pattern.MULTILINE);
    private static final Pattern snitchAlertPattern = Pattern.compile("\\s*\\*\\s*([^\\s]*)\\s\\b(entered snitch at|logged out in snitch at|logged in to snitch at)\\b\\s*([^\\s]*)\\s\\[([^\\s]*)\\s([-\\d]*)\\s([-\\d]*)\\s([-\\d]*)\\]");

    private static final String[] resetSequences = {"Unknown command", " is empty", "You do not own any snitches nearby!"};
    private static final String tpsMessage = "TPS from last 1m, 5m, 15m:";

    private final SnitchMaster snitchMaster;
    private final SnitchManager manager;
    private final List<IAlertRecipient> alertRecipients;

    private int jaListIndex = 1;
    private int maxJaListIndex = -1;

    private boolean updatingSnitchList = false;
    private HashSet<Snitch> snitchesCopy;
    private HashSet<Snitch> loadedSnitches;

    private double waitTime = 4;
    private int tickTimeout = 20;
    private long nextUpdate = System.currentTimeMillis();

    public ChatSnitchParser(SnitchMaster api)
    {
        this.snitchMaster = api;
        this.manager = snitchMaster.getManager();
        alertRecipients = new ArrayList<>();
        snitchesCopy = null;
        loadedSnitches = null;

        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Adds a recipient that would like to receive Snitch alerts.
     */
    public void addAlertRecipient(IAlertRecipient recipient)
    {
        this.alertRecipients.add(recipient);
    }

    @SubscribeEvent
    public void chatParser(ClientChatReceivedEvent event)
    {
        ITextComponent msg = event.getMessage();
        if (msg == null)
            return;

        String msgText = msg.getUnformattedText();
        if (msgText == null)
            return;

        //Check if its the tps message (this is quick)
        if (msgText.contains(tpsMessage))
        {
            parseTPS(msgText);
            return;
        }

        //Start of the chat message for creating a snitch block from /ctf or /ctr
        if (msgText.contains("You've created"))
        {
            if (tryParsePlaceMessage(msg))
            {
                //Save the snitches now that we loaded a new one from chat
                manager.saveSnitches();

                SnitchMaster.SendMessageToPlayer("Saved snitch from chat message");
                return;
            }
        }
        else if(msgText.contains("Changed snitch name to"))
        {
            if(tryParseNameChangeMessage(msg))
            {
                manager.saveSnitches();
                return;
            }
        }

        //Only check for reset sequences or /jalist messages if we are updating
        if (updatingSnitchList)
        {
            if (containsAny(msgText, resetSequences)) //Check if this is any of the reset messages (this is kind of quick)
            {
                resetUpdatingSnitchList(true);
                SnitchMaster.SendMessageToPlayer("Finished full snitch update");
                return;
            }

            //Check if this matches a snitch entry from the /jalist command (this is less quick than above)
            if (tryParseJalistMsg(msg))
            {
                //If they have it set to not spam the chat then cancel the chat message
                Settings.ChatSpamState state = (Settings.ChatSpamState) snitchMaster.getSettings().getValue(Settings.CHAT_SPAM_KEY);
                if (state == Settings.ChatSpamState.OFF || state == Settings.ChatSpamState.PAGENUMBERS)
                    event.setCanceled(true);
                return;
            }
        }

        //Check if this matches the snitch alert message (slowest of all of these)
        Matcher matcher = snitchAlertPattern.matcher(msgText);
        if (!matcher.matches())
            return; // this was the last kind of message we check

        //Build the snitch alert and send it to all the recipients
        SnitchAlert alert = buildSnitchAlert(matcher, msg);

        for (IAlertRecipient recipient : alertRecipients)
        {
            recipient.receiveSnitchAlert(alert);
        }

        //Set the alert's message to whatever the final message is from the alert "event"
        event.setMessage(alert.getRawMessage());
    }

    private boolean tryParseNameChangeMessage(ITextComponent msg)
    {
        List<ITextComponent> siblings = msg.getSiblings();
        if (siblings.size() <= 0)
            return false;

        ITextComponent hoverComponent = siblings.get(0);

        HoverEvent hover = hoverComponent.getStyle().getHoverEvent();
        if (hover != null)
        {
            String text = hover.getValue().getUnformattedComponentText();
            try
            {
                String[] args = text.split("\n");
                String[] worldArgs = args[0].split(" ");
                String[] locationArgs = args[1].split(":")[1].split(" ");
                String newName = args[6].trim();

                int x, y, z;
                x = Integer.parseInt(locationArgs[1].substring(1));
                y = Integer.parseInt(locationArgs[2]);
                z = Integer.parseInt(locationArgs[3].substring(0, locationArgs[3].length() - 1));
                String world = worldArgs.length > 1 ? worldArgs[1] : snitchMaster.getCurrentWorld();

                Location loc = new Location(x, y, z, world);
                Snitch snitch = manager.getSnitches().get(loc);

                if(snitch != null)
                {
                    manager.setSnitchName(snitch,newName);
                    return true;
                }
            }
            catch (Exception e)
            {
                return false;
            }
        }
        return false;
    }

    private boolean tryParsePlaceMessage(ITextComponent msg)
    {
        List<ITextComponent> siblings = msg.getSiblings();
        if (siblings.size() <= 0)
            return false;

        ITextComponent hoverComponent = siblings.get(0);

        HoverEvent hover = hoverComponent.getStyle().getHoverEvent();
        if (hover != null)
        {
            String text = hover.getValue().getUnformattedComponentText();
            Snitch snitch = parseSnitchFromChat(text);
            if (snitch != null)
            {
                manager.submitSnitch(snitch);
                return true;
            }
        }

        return false;
    }

    private Snitch parseSnitchFromChat(String text)
    {
        try
        {
            String[] args = text.split("\n");
            String[] worldArgs = args[0].split(" ");
            String[] locationArgs = args[1].split(":")[1].split(" ");
            String[] groupArgs = args[2].split(" ");

            int x, y, z;
            x = Integer.parseInt(locationArgs[1].substring(1));
            y = Integer.parseInt(locationArgs[2]);
            z = Integer.parseInt(locationArgs[3].substring(0, locationArgs[3].length() - 1));
            String world = worldArgs.length > 1 ? worldArgs[1] : snitchMaster.getCurrentWorld();

            Location loc = new Location(x, y, z, world);
            String group = groupArgs.length > 1 ? groupArgs[1] : Snitch.DEFAULT_NAME;

            return new Snitch(loc, SnitchTags.FROM_TEXT, SnitchMaster.CULL_TIME_ENABLED ? Snitch.MAX_CULL_TIME : Double.NaN, group, Snitch.DEFAULT_NAME);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Attempt parsing a chat message as a /jalist message, fails quickly returning false,
     * or submits all contained snitches for processing.
     *
     * @param msg
     * @return true if it was parsed as /jalist message, false if it is a different message
     */
    private boolean tryParseJalistMsg(ITextComponent msg)
    {
        List<ITextComponent> snitchRows;
        try
        {
            ITextComponent snitchListComponent = msg.getSiblings().get(0);
            snitchRows = snitchListComponent.getSiblings();

            if (snitchRows.isEmpty())
                return false;

        }
        catch (IndexOutOfBoundsException e)
        {
            return false;
        }
        catch (NullPointerException e)
        {
            return false;
        }

        for (ITextComponent row : snitchRows)
        {
            String hoverText = "";
            try
            {
                //For some reason row.getStyle() is never null
                HoverEvent event = row.getStyle().getHoverEvent();
                if (event != null)
                {
                    hoverText = event.getValue().getUnformattedComponentText();

                    Matcher matcher = jaListPattern.matcher(hoverText);
                    if (!matcher.matches())
                        continue;
                    Snitch snitch = parseSnitchFromJaList(matcher);

                    if(loadedSnitches != null)
                        loadedSnitches.add(snitch);

                    manager.submitSnitch(snitch);
                }
            }
            catch (IllegalStateException e)
            {
                SnitchMaster.SendMessageToPlayer("No match on /jalist hover text " + hoverText);
                e.printStackTrace();
            }
        }

        return true;
    }

    /**
     * Parses a {@link Snitch} from the hover text of a /jalist output row.
     *
     * @param matcher the {@link Matcher} object, on which `.matches()` or similar has to be called already
     */
    private Snitch parseSnitchFromJaList(Matcher matcher)
    {
        String worldName = matcher.group(1);
        int x = Integer.parseInt(matcher.group(2));
        int y = Integer.parseInt(matcher.group(3));
        int z = Integer.parseInt(matcher.group(4));
        double cullTime;

        String cullTimeString = matcher.group(5);
        if (cullTimeString == null || cullTimeString.isEmpty())
            cullTime = Double.NaN;
        else
            cullTime = Double.parseDouble(cullTimeString);

        String ctGroup = matcher.group(6);
        String name = matcher.group(7);

        return new Snitch(new Location(x, y, z, worldName), SnitchTags.FROM_JALIST, cullTime, ctGroup, name);
    }

    @SubscribeEvent
    public void tickEvent(TickEvent.ClientTickEvent event)
    {
        if (updatingSnitchList)
        {
            if (System.currentTimeMillis() > nextUpdate)
            {
                //If they disconnect while updating is running we dont want the game to crash
                if (Minecraft.getMinecraft().thePlayer != null)
                {
                    if (maxJaListIndex != -1 && jaListIndex - 1 >= maxJaListIndex)
                    {
                        resetUpdatingSnitchList(true);
                        SnitchMaster.SendMessageToPlayer("Finished targeted snitch update");
                        return;
                    }

                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/jalist " + jaListIndex);
                    jaListIndex++;
                    nextUpdate = System.currentTimeMillis() + (long) (waitTime * 1000);

                    if (((Settings.ChatSpamState) snitchMaster.getSettings().getValue(Settings.CHAT_SPAM_KEY)) == Settings.ChatSpamState.PAGENUMBERS)
                        SnitchMaster.SendMessageToPlayer("Parsed snitches from /jalist " + (jaListIndex - 1));
                }
                else
                    resetUpdatingSnitchList(true);
            }
        }
    }

    /**
     * Returns true if SnitchMaster is currently updating from the /jalist command.
     */
    public boolean isUpdatingSnitchList()
    {
        return updatingSnitchList;
    }

    /**
     * Begins updating Snitches from the /jalist command.
     */
    public void updateSnitchList()
    {
        resetUpdatingSnitchList(false);

        snitchesCopy = new HashSet<>();
        loadedSnitches = new HashSet<>();

        //Go through all the snitches that we have saved
        for(Snitch snitch : manager.getSnitches())
        {
            //If the snitch isn't already marked as gone and is marked as jalist
            if(!snitch.isTagged(SnitchTags.IS_GONE) && snitch.isTagged(SnitchTags.FROM_JALIST))
                snitchesCopy.add(snitch); //Then we add it to the copy list
        }

        Minecraft.getMinecraft().thePlayer.sendChatMessage("/tps");
        nextUpdate = System.currentTimeMillis() + 2000;
        updatingSnitchList = true;
    }

    public void updateSnitchList(int startIndex, int stopIndex)
    {
        resetUpdatingSnitchList(false);

        jaListIndex = startIndex;
        maxJaListIndex = stopIndex;

        Minecraft.getMinecraft().thePlayer.sendChatMessage("/tps");
        nextUpdate = System.currentTimeMillis() + 2000;
        updatingSnitchList = true;

        //SnitchMaster.SendMessageToPlayer("The current world is: "+snitchMaster.getCurrentWorld());
    }

    private void parseTPS(String message)
    {
        message = message.substring(message.indexOf(':') + 1);
        String[] tokens = message.split(", +"); // " 11.13, 11.25, 11.32"
        if (tokens.length > 2)
        {
            double a = 20.0;
            double b = 20.0;
            double c = 20.0;

            for (int i = 0; i < 3; i++) // fix for TPS 20 -- *20.0 is rendered, yikes.
                tokens[i] = tokens[1].replace('*', ' ');

            a = Double.parseDouble(tokens[0]);
            b = Double.parseDouble(tokens[1]);
            c = Double.parseDouble(tokens[2]);

            if (a < b && a < c)
                waitTime = tickTimeout / a;
            else if (b < a && b < c)
                waitTime = tickTimeout / b;
            else
                waitTime = tickTimeout / c;
        }
        else
            waitTime = 4.0;

        SnitchMaster.SendMessageToPlayer("Timeout between commands is " + waitTime + " seconds.");
    }

    /**
     * Resets the updating of Snitches from the /jalist command.
     * If an update is currently in progress, it is stopped.
     */
    public void resetUpdatingSnitchList(boolean save)
    {
        if(updatingSnitchList && snitchesCopy != null)
        {
            //Remove all the snitches that we loaded from jalist from the copy
            snitchesCopy.removeAll(loadedSnitches);

            for(Snitch snitch : snitchesCopy)
                manager.addTag(snitch,SnitchTags.IS_GONE);

            SnitchMaster.SendMessageToPlayer(snitchesCopy.size()+" snitches were missing since the last full update.");

            snitchesCopy.clear();
            loadedSnitches.clear();

            snitchesCopy = null;
            loadedSnitches = null;
        }

        jaListIndex = 1;
        maxJaListIndex = -1;
        updatingSnitchList = false;

        if (save)
            manager.saveSnitches();
    }

    private static SnitchAlert buildSnitchAlert(Matcher matcher, ITextComponent message)
    {
        String playerName = matcher.group(1);
        String activity = matcher.group(2);
        String snitchName = matcher.group(3);
        String worldName = matcher.group(4);
        int x = Integer.parseInt(matcher.group(5));
        int y = Integer.parseInt(matcher.group(6));
        int z = Integer.parseInt(matcher.group(7));
        return new SnitchAlert(playerName, x, y, z, activity, snitchName, worldName, message);
    }

    private static boolean containsAny(String message, String[] tokens)
    {
        for (String token : tokens)
        {
            if (message.contains(token))
                return true;
        }
        return false;
    }
}
