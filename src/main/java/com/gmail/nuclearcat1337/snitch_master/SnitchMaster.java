package com.gmail.nuclearcat1337.snitch_master;

import com.gmail.nuclearcat1337.snitch_master.handlers.*;
import com.gmail.nuclearcat1337.snitch_master.journeymap.JourneyMapInterface;
import com.gmail.nuclearcat1337.snitch_master.locatableobjectlist.LocatableObjectList;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchLists;
import com.gmail.nuclearcat1337.snitch_master.util.IOHandler;
import com.gmail.nuclearcat1337.snitch_master.util.ValueParser;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by Mr_Little_Kitty on 6/25/2016.
 * The main class for the SnitchMaster mod.
 */
@Mod(modid = SnitchMaster.MODID, name = SnitchMaster.MODNAME, version = SnitchMaster.MODVERSION, guiFactory = "com.gmail.nuclearcat1337.snitch_master.gui.ConfigGuiFactory")
public class SnitchMaster
{
    public static final String MODID = "snitchmaster";
    public static final String MODNAME = "Snitch Master";
    public static final String MODVERSION = "1.0.9";

    private static final Minecraft mc = Minecraft.getMinecraft();
    /**
     * The Logger instance to use to print to the console
     */
    public static final Logger logger = Logger.getLogger(MODID);
    public static final boolean CULL_TIME_ENABLED = true;

    /**
     * The JourneyMap interface for communicating with the JourneyMap plugin
     */
    public static JourneyMapInterface jmInterface = null;

    /**
     * The static instance of this SnitchMaster class
     */
    public static SnitchMaster instance;

    private Settings settings;

    private ChatSnitchParser chatSnitchParser;
    private WorldInfoListener worldInfoListener;

    private SnitchLists snitchLists;
    private LocatableObjectList<Snitch> snitches;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        IOHandler.prepareFiles();

        this.snitches = new LocatableObjectList<>();
        this.snitchLists = new SnitchLists(this);

        snitchLists.addDefaultSnitchLists();

        worldInfoListener = new WorldInfoListener(this);
        MinecraftForge.EVENT_BUS.register(worldInfoListener);
        FMLCommonHandler.instance().bus().register(worldInfoListener);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        initializeSettings();

        chatSnitchParser = new ChatSnitchParser(this);

        MinecraftForge.EVENT_BUS.register(chatSnitchParser);
        MinecraftForge.EVENT_BUS.register(new SnitchRenderer(this));
        MinecraftForge.EVENT_BUS.register(new SnitchListeners(this));

        FMLCommonHandler.instance().bus().register(new KeyHandler(this));

        chatSnitchParser.addAlertRecipient(new QuietTimeHandler(getSettings()));

        try
        {
            IOHandler.loadSnitchLists(this);
            IOHandler.loadSnitches(this);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        instance = this;
    }

    private void initializeSettings()
    {
        settings = new Settings(IOHandler.getSettingsFile(),new ObjectParser());
        settings.loadSettings();

        settings.setValueIfNotSet(Settings.QUIET_TIME_KEY, Boolean.FALSE);
        settings.setValueIfNotSet(Settings.CHAT_SPAM_KEY, Settings.ChatSpamState.ON);
        settings.setValueIfNotSet(Settings.RENDER_TEXT_KEY, Boolean.TRUE);

        settings.saveSettings();
    }

    public Settings getSettings()
    {
        return settings;
    }

    /**
     * Returns the instance of the ChatSnitchParser for dealing with chat Snitch loading.
     */
    public ChatSnitchParser getChatSnitchParser()
    {
        return chatSnitchParser;
    }

    /**
     * Returns the name of the current world or "single player" if in single player.
     */
    public String getCurrentWorld()
    {
        return worldInfoListener.getWorldID();
    }

    /**
     * Gets the SnitchLists object that manages SnitchLists.
     * @return
     */
    public SnitchLists getSnitchLists()
    {
        return snitchLists;
    }

    public SnitchList getSnitchListByName(String name)
    {
        for(SnitchList list : getSnitchLists())
        {
            if(list.getListName().equals(name))
                return list;
        }
        return null;
    }

    /**
     * Returns a LocateableObjectList of all the currently loaded Snitches
     */
    public LocatableObjectList<Snitch> getSnitches()
    {
        return snitches;
    }

    /**
     * Submits a Snitch for processing and adding to the Snitch collection.
     * The Snitch is added to all SnitchLists, JourneyMap, (if applicable) and then saved to a file.
     */
    public void submitSnitch(Snitch snitch)
    {
        //Check to see if there is already a snitch at this location
        Snitch contains = snitches.get(snitch.getLocation());

        //Check if the snitch that was submitted already exists
        if(contains != null)
        {
            //If it does then change the cull time and group
            contains.setCullTime(snitch.getCullTime());
            contains.setGroupName(snitch.getGroupName());
            contains.setSnitchName(snitch.getSnitchName());
            //Clear the attached snitch lists because we are going to requalify the snitch because some attributes changed
            contains.clearAttachedSnitchLists();
        }
        else
        {
            //Just some reference rearranging
            contains = snitch;
            //add the snitch to the collection
            snitches.add(contains);
        }

        //Go through all the snitch lists to see if this snitch should be in them
        for(SnitchList list : snitchLists)
        {
            //If it should then attach the snitch list to the snitch
            if(list.getQualifier().isQualified(contains))
                contains.attachSnitchList(list);
        }

        //send it to journey map if that is enabled
        if(jmInterface != null)
            jmInterface.displaySnitch(contains);
    }

    /**
     * Refreshes the render priorities of all currently loaded Snitches.
     */
    public void refreshSnitchListPriorities()
    {
        for(Snitch snitch : getSnitches())
            snitch.sortSnitchLists();
    }

    private static class ObjectParser implements ValueParser
    {
//        settings.setValueIfNotSet("quiet-time", Boolean.FALSE.toString());
//        settings.setValueIfNotSet("chat-spam", 1);

        @Override
        public Object parse(String key, String value)
        {
            if(key.equalsIgnoreCase(Settings.QUIET_TIME_KEY) || key.equalsIgnoreCase(Settings.RENDER_TEXT_KEY))
                return Boolean.parseBoolean(value);
            else if(key.equalsIgnoreCase(Settings.CHAT_SPAM_KEY))
                return Settings.ChatSpamState.valueOf(value);
            else
                return value;
        }
    }
}
