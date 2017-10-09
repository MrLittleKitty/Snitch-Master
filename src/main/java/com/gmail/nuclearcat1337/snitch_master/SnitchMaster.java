package com.gmail.nuclearcat1337.snitch_master;

import com.gmail.nuclearcat1337.snitch_master.handlers.*;
import com.gmail.nuclearcat1337.snitch_master.journeymap.JourneyMapInterface;
import com.gmail.nuclearcat1337.snitch_master.snitches.Snitch;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchList;
import com.gmail.nuclearcat1337.snitch_master.snitches.SnitchManager;
import com.gmail.nuclearcat1337.snitch_master.util.QuietTimeConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

@Mod(modid = SnitchMaster.MODID, name = SnitchMaster.MODNAME, version = SnitchMaster.MODVERSION, guiFactory = "com.gmail.nuclearcat1337.snitch_master.gui.ConfigGuiFactory")
public class SnitchMaster {
	public static final String MODID = "snitchmaster";
	public static final String MODNAME = "Snitch Master";
	public static final String MODVERSION = "1.0.9";
	public static final String modDataFolder = "mods/Snitch-Master";

	private static final Minecraft mc = Minecraft.getMinecraft();
	public static final Logger logger = LogManager.getLogger(MODID);
	public static final boolean CULL_TIME_ENABLED = true;

	public static JourneyMapInterface jmInterface = null;

	/**
	 * The static instance of this SnitchMaster class
	 */
	@Mod.Instance(MODID)
	public static SnitchMaster instance;

	private Settings settings;
	private SnitchManager manager;

	private ChatSnitchParser chatSnitchParser;
	private WorldInfoListener worldInfoListener;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		File file = new File(modDataFolder);
		if (!file.exists()) {
			file.mkdir();
		}

		initializeSettings();

		manager = new SnitchManager(this);

		worldInfoListener = new WorldInfoListener(this);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		chatSnitchParser = new ChatSnitchParser(this);

		new SnitchRenderer(this);
		new SnitchListeners(this);
		new KeyHandler(this);

		chatSnitchParser.addAlertRecipient(new QuietTimeHandler(getSettings()));
	}

	public SnitchManager getManager() {
		return manager;
	}

	private void initializeSettings() {
		settings = new Settings(new ObjectParser());
		settings.loadSettings();

		settings.setValueIfNotSet(QuietTimeHandler.QUIET_TIME_CONFIG_KEY, QuietTimeConfig.GetDefaultQuietTimeConfig());
		settings.setValueIfNotSet(Settings.CHAT_SPAM_KEY, Settings.ChatSpamState.ON);
		settings.setValueIfNotSet(Settings.RENDER_TEXT_KEY, Boolean.TRUE);
		settings.setValueIfNotSet(Settings.MANUAL_MODE_KEY, Boolean.TRUE);
		settings.setValueIfNotSet(SnitchManager.GLOBAL_RENDER_KEY, Boolean.TRUE);

		settings.saveSettings();
	}

	public void fullJourneyMapUpdate() {
		if (jmInterface != null) {
			jmInterface.refresh(manager.getSnitches()); //TODO---Do better
		}
	}

	public void individualJourneyMapUpdate(Snitch snitch) {
		if (jmInterface != null) {
			jmInterface.displaySnitch(snitch); //TODO--Do better
		}
	}

	public void snitchListJourneyMapUpdate(SnitchList list) {
		if (jmInterface != null) {
			List<Snitch> snitches = manager.getSnitchesInList(list);
			for (Snitch snitch : snitches)
				individualJourneyMapUpdate(snitch); //TODO---Do better
		}
	}

	public Settings getSettings() {
		return settings;
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
	public String getCurrentWorld() {
		return worldInfoListener.getWorldID();
	}

	public static void SendMessageToPlayer(String message) {
		if (mc.player != null) {
			mc.player.sendMessage(new TextComponentString("[Snitch Master] " + message));
		}
	}

	private static class ObjectParser implements Settings.ValueParser {
		@Override
		public Object parse(String key, String value) {
			if (key.equalsIgnoreCase(Settings.RENDER_TEXT_KEY)) {
				return Boolean.parseBoolean(value);
			} else if (key.equalsIgnoreCase(Settings.CHAT_SPAM_KEY)) {
				return Settings.ChatSpamState.valueOf(value);
			} else if (key.equalsIgnoreCase(QuietTimeHandler.QUIET_TIME_CONFIG_KEY)) {
				return QuietTimeConfig.FromString(value);
			} else {
				if (value.equalsIgnoreCase(Boolean.FALSE.toString())) {
					return Boolean.FALSE;
				} else if (value.equalsIgnoreCase(Boolean.TRUE.toString())) {
					return Boolean.TRUE;
				} else {
					return value;
				}
			}
		}
	}
}
