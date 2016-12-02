package com.gmail.nuclearcat1337.snitch_master.handlers;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.api.IAlertRecipient;
import com.gmail.nuclearcat1337.snitch_master.api.SnitchAlert;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.util.IOHandler;
import com.gmail.nuclearcat1337.snitch_master.util.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
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
    private static final Pattern snitchAlertPattern = Pattern.compile("\\s*\\*\\s*([^\\s]*)\\s\\b(?:entered snitch at|logged out in snitch at|logged in to snitch at)\\b\\s*([^\\s]*)\\s\\[([^\\s]*)\\s([-\\d]*)\\s([-\\d]*)\\s([-\\d]*)\\]");

    private static final String[] resetSequences = {"Unknown command"," is empty", "You do not own any snitches nearby!"};
    private static final String tpsMessage = "TPS from last 1m, 5m, 15m:";

    private final SnitchMaster snitchMaster;
    private final List<IAlertRecipient> IAlertRecipients;

    private int jaListIndex = 1;
    private boolean updatingSnitchList = false;

    private double waitTime = 4;
    private int tickTimeout = 20;
    private long nextUpdate = System.currentTimeMillis();

    public ChatSnitchParser(SnitchMaster api)
    {
        this.snitchMaster = api;
        IAlertRecipients = new ArrayList<>();
    }

    /**
     * Adds a recipient that would like to receive Snitch alerts.
     */
    public void addAlertRecipient(IAlertRecipient recipient)
    {
        this.IAlertRecipients.add(recipient);
    }

    @SubscribeEvent
    public void chatParser(ClientChatReceivedEvent event)
    {
        ITextComponent msg = event.getMessage();
        if(event != null && msg != null)
        {
            String msgText = msg.getUnformattedText();
            if(msgText != null)
            {
                SnitchMaster.logger.warning("Temp message " + msgText);
                //Check if its the tps message (this is quick)
                if(msgText.contains(tpsMessage))
                    parseTPS(msgText);
                else if(updatingSnitchList)
                {
                    if (containsAny(msgText, resetSequences)) //Check if this is any of the reset messages (this is kind of quick)
                        resetUpdatingSnitchList(true);
                    else
                    {
                        //Check if this matches a snitch entry from the jalist command (this is less quick than above)
                        if (!tryParseJalistMsg(msg))
                        {
                            //Check if this matches the snitch alert message (slowest of all of these)
                            Matcher matcher = snitchAlertPattern.matcher(msgText);
                            if (matcher.matches())
                            {
                                if (!IAlertRecipients.isEmpty())  //Sometimes an optimization because it avoids building the altert object
                                {
                                    SnitchAlert alert = buildSnitchAlert(matcher, msg);
                                    for (IAlertRecipient recipient : IAlertRecipients)
                                    {
                                        recipient.receiveSnitchAlert(alert);
                                    }
                                    event.setMessage(alert.getRawMessage());
                                }
                            }
                        }
                    }
                }
            }
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
        try {
            ITextComponent snitchListComponent = msg.getSiblings().get(0);
            snitchRows = snitchListComponent.getSiblings();

            if (snitchRows.isEmpty())
                return false;

//            if (!snitchListComponent.getUnformattedComponentText().startsWith("§f Snitch List for "))
//                return false;
        } catch (IndexOutOfBoundsException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }

        for (ITextComponent row : snitchRows) {
            String hoverText = "";
            try {
                //For some reason row.getStyle() is never null
                HoverEvent event =  row.getStyle().getHoverEvent();
                if(event != null)
                {
                    hoverText = event.getValue().getUnformattedComponentText();

                    Matcher matcher = jaListPattern.matcher(hoverText);
                    if (!matcher.matches())
                        continue;
                    Snitch snitch = parseSnitchFromJaList(matcher);
                    snitchMaster.submitSnitch(snitch);
                }
            } catch (IllegalStateException e) {
                Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new TextComponentString("No match on /jalist hover text " + hoverText));
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
        if(cullTimeString == null || cullTimeString.isEmpty())
            cullTime = Double.NaN;
        else
            cullTime = Double.parseDouble(cullTimeString);

        String ctGroup = matcher.group(6);
        String name = matcher.group(7);

        return new Snitch(new Location(x,y,z,worldName),"jalist",cullTime,ctGroup,name);
    }

    @SubscribeEvent
    public void tickEvent(TickEvent.ClientTickEvent event)
    {
        if(updatingSnitchList)
        {
            if (System.currentTimeMillis() > nextUpdate)
            {
                //If they disconnect while updating is running we dont want the game to crash
                if(Minecraft.getMinecraft().thePlayer != null)
                {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/jalist " + jaListIndex);
                    jaListIndex++;
                    nextUpdate = System.currentTimeMillis() + (long) (waitTime * 1000);
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

        Minecraft.getMinecraft().thePlayer.sendChatMessage("/tps");
        nextUpdate = System.currentTimeMillis() + 2000;
        updatingSnitchList = true;

        Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new TextComponentString("The current world is: "+snitchMaster.getCurrentWorld()));
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

            for (int i=0; i<3; i++) // fix for TPS 20 -- *20.0 is rendered, yikes.
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

        Minecraft.getMinecraft().thePlayer.addChatMessage(new TextComponentString("Timeout between commands is " + waitTime + " seconds."));
    }

    /**
     * Resets the updating of Snitches from the /jalist command.
     * If an update is currently in progress, it is stopped.
     */
    public void resetUpdatingSnitchList(boolean save)
    {
        jaListIndex = 1;

        if(save)
        {
            //TODO----Make it save all the snitch lists (asynchronously) after its done updating from the /jalist command
            //if(updatingSnitchList)
            //snitchMaster.saveTheCorrectSnitchListOrSomething
            IOHandler.asyncSaveSnitches(snitchMaster.getSnitches());
            IOHandler.asyncSaveSnitchLists(snitchMaster.getSnitchLists());
        }

        updatingSnitchList = false;
    }

    private static SnitchAlert buildSnitchAlert(Matcher matcher, ITextComponent message)
    {
        String playerName = matcher.group(1);
        String snitchName = matcher.group(2);
        String worldName = matcher.group(3);
        int x = Integer.parseInt(matcher.group(4));
        int y = Integer.parseInt(matcher.group(5));
        int z = Integer.parseInt(matcher.group(6));
        return new SnitchAlert(playerName,x,y,z,worldName,message);
    }

    private static boolean containsAny(String message, String[] tokens)
    {
        for(String token : tokens)
        {
            if(message.contains(token))
                return true;
        }
        return false;
    }
}
