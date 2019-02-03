package com.gmail.nuclearcat1337.snitch_master;

import com.gmail.nuclearcat1337.snitch_master.assistant.AssistantManager;
import com.gmail.nuclearcat1337.snitch_master.handlers.*;
import com.gmail.nuclearcat1337.snitch_master.journeymap.JourneyMapRelay;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import com.gmail.nuclearcat1337.snitch_master.worldinfo.WorldInfoListener;
import com.gmail.nuclearcat1337.snitch_master.worldinfo.WorldProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

/**
 * Created by Mr_Little_Kitty on 6/25/2016.
 * The main class for the SnitchMaster mod.
 */
@Mod(modid = SnitchMaster.MODID, name = SnitchMaster.MODNAME, version = SnitchMaster.MODVERSION, guiFactory = "com.gmail.nuclearcat1337.snitch_master.gui.ConfigGuiFactory")
public class SnitchMaster implements WorldProvider {
    public static final String MODID = "snitchmaster";
    public static final String MODNAME = "Snitch Master";
    public static final String MODVERSION = "1.2.0";
    public static final String modDataFolder = "mods/Snitch-Master";

    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final Logger logger = LogManager.getLogger(MODID);
    public static final boolean CULL_TIME_ENABLED = true;

    /**
     * The static instance of this SnitchMaster class
     */
    @Mod.Instance(MODID)
    public static SnitchMaster instance;

    private Settings settings;
    private SnitchManager manager;
    private AssistantManager assistantManager;

    private ChatSnitchParser chatSnitchParser;
    private WorldInfoListener worldInfoListener;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File file = new File(modDataFolder);
        if (!file.exists()) {
            file.mkdir();
        }

        settings = new Settings();
        manager = new SnitchManager(JourneyMapRelay.getInstance(), settings);
        assistantManager = new AssistantManager();

        settings.loadSettings();
        settings.saveSettings(); //Save back the settings we just loaded or else save back the default settings

        worldInfoListener = new WorldInfoListener(this);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        chatSnitchParser = new ChatSnitchParser(manager, settings);

        new SnitchRenderer(manager, settings, this);
        new SnitchListeners(manager, settings, this, JourneyMapRelay.getInstance());
        new KeyHandler(this);

        new AssistantRenderer(assistantManager, settings, this);

        chatSnitchParser.addAlertRecipient(new QuietTimeHandler(settings));
    }

    public SnitchManager getManager() {
        return manager;
    }

    public Settings getSettings() {
        return settings;
    }

    public AssistantManager getAssistantManager() {
        return this.assistantManager;
    }

    /**
     * Returns the instance of the ChatSnitchParser for dealing with chat Snitch loading.
     */
    public ChatSnitchParser getChatSnitchParser() {
        return chatSnitchParser;
    }

    /**
     * Returns the name of the current world or "single player" if in single player.
     */
    @Override
    public String getCurrentWorld() {
        return worldInfoListener.getWorldName();
    }

    public static void SendMessageToPlayer(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(new TextComponentString("[Snitch Master] " + message));
        }
    }

}
