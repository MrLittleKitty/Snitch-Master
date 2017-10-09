package com.gmail.nuclearcat1337.snitch_master.handlers;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.Settings.ChatSpamState;
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

import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatSnitchParser {
	private static final Pattern snitchAlertPattern = Pattern.compile(
		"\\s*\\*\\s*([^\\s]*)\\s\\b(entered snitch at|logged out in snitch at|logged in to snitch at)\\b\\s*([^\\s]*)\\s\\[([^\\s]*)\\s([-\\d]*)\\s([-\\d]*)\\s([-\\d]*)\\]");
	private static final Pattern snitchCreateMessageStartRegex = Pattern.compile(
		"(?i)^\\s*You've created .*");
	private static final Pattern snitchInfoMessageStartRegex = Pattern.compile(
		"(?i)^\\s*(?: \\* )?(?:(?:Unnamed )?Entry snitch|Page [0-9]* is empty for|Log for(?:unnamed)? snitch) .*");
	private static final Pattern snitchBreakMessageStartRegex = Pattern.compile(
		"(?i)^\\s*You've broken .*");
	private static final Pattern snitchNameChangeMessageStartRegex = Pattern.compile(
		"(?i)^\\s*Changed snitch name to .*");
	private static final Pattern snitchNotificationMessageStartRegex = Pattern.compile(
		"(?i)^\\s* \\* .+? snitch at .*");
	private static final Pattern tpsMessageStartRegex = Pattern.compile(
		"(?i)^\\s*TPS from last 1m, 5m, 15m: .*");
	private static final Pattern snitchMessageHoverTextRegex = Pattern.compile(
		"^(?i)\\s*Location:\\s*\\[(\\S+?) (-?[0-9]+) (-?[0-9]+) (-?[0-9]+)\\]\\s*Group:\\s*(\\S+?)\\s*Type:\\s*(Entry|Logging)\\s*(?:(?:Hours to cull|Cull):\\s*([0-9]+\\.[0-9]+)h?)?\\s*(?:Previous name:\\s*(\\S+?))?\\s*(?:Name:\\s*(\\S+?))?\\s*", Pattern.MULTILINE);

	private static final String[] resetSequences = {"Unknown command", " is empty", "You do not own any snitches nearby!"};

	private final SnitchMaster snitchMaster;
	private final SnitchManager manager;
	private final List<IAlertRecipient> alertRecipients;

	private int jaListIndex = 1;
	private int maxJaListIndex = -1;

	private boolean updatingSnitchList = false;
	private HashSet<Snitch> snitchesCopy;
	private HashSet<Snitch> loadedSnitches;

	private Double avgTps1MinPrev = null;
	private Double avgTps1Min = null;
	private Double avgTps5Min = null;
	private Double avgTps15Min = null;
	private int tickTimeoutSec = 20;
	private Long nextTpsRunTime = null;
	private Long nextCommandRunTime = System.currentTimeMillis();

	public ChatSnitchParser(SnitchMaster api) {
		this.snitchMaster = api;
		this.manager = snitchMaster.getManager();
		alertRecipients = new ArrayList<>();
		snitchesCopy = null;
		loadedSnitches = null;

		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addAlertRecipient(IAlertRecipient recipient) {
		this.alertRecipients.add(recipient);
	}

	@SubscribeEvent
	public void parseChat(ClientChatReceivedEvent event) {
		ITextComponent message = event.getMessage();
		if (message == null) {
			return;
		}
		String messageText = message.getUnformattedText();
		if (messageText == null) {
			return;
		}
		messageText = stripMinecraftFormattingCodes(messageText);

		if (tryParseTpsMessage(message, messageText)) {
			return;
		}
		if (tryParseSnitchNotificationMessage(message, messageText)) {
			return;
		}
		if (tryParseSnitchInfoMessage(message, messageText)) {
			return;
		}
		if (tryParseSnitchCreateMessage(message, messageText)) {
			return;
		}
		if (tryParseSnitchBreakMessage(message, messageText)) {
			return;
		}
		if (tryParseSnitchNameChangeMessage(message, messageText)) {
			return;
		}

		if (updatingSnitchList) {
			if (containsAny(messageText, resetSequences)) {
				resetUpdatingSnitchList(/* save = */ true, /* cancelled = */ false);
				SnitchMaster.SendMessageToPlayer("Finished full snitch update");
				return;
			}

			if (tryParseJalistMsg(message)) {
				Settings.ChatSpamState state = (Settings.ChatSpamState) snitchMaster.getSettings().getValue(Settings.CHAT_SPAM_KEY);
				if (state == Settings.ChatSpamState.OFF || state == Settings.ChatSpamState.PAGENUMBERS)
					event.setCanceled(true);
				return;
			}
		}

		Matcher matcher = snitchAlertPattern.matcher(messageText);
		if (!matcher.matches()) {
			return;
		}
		SnitchAlert alert = buildSnitchAlert(matcher, message);
		for (IAlertRecipient recipient : alertRecipients) {
			recipient.receiveSnitchAlert(alert);
		}
		event.setMessage(alert.getRawMessage());
	}

	private boolean tryParseTpsMessage(ITextComponent message, String messageText) {
		Matcher matcher = tpsMessageStartRegex.matcher(messageText);
		if (!matcher.matches()) {
			return false;
		}

		messageText = messageText.substring(messageText.indexOf(':') + 1);
		// "11.13, 11.25, 11.32"
		String[] tokens = messageText.split(", +");
		if (tokens.length <= 2) {
			avgTps1MinPrev = avgTps1Min;
			avgTps1Min = null;
			avgTps5Min = null;
			avgTps15Min = null;
			updateNextTpsRunTime();
			return true;
		}

		// Fix for TPS 20 - "*20.0" is rendered.
		for (int i = 0; i < 3; i++) {
			tokens[i] = tokens[i].replace("*", "");
		}

		avgTps1MinPrev = avgTps1Min;
		avgTps1Min = Double.parseDouble(tokens[0]);
		avgTps5Min = Double.parseDouble(tokens[1]);
		avgTps15Min = Double.parseDouble(tokens[2]);
		updateNextTpsRunTime();
		return true;
	}

	private boolean tryParseSnitchNotificationMessage(ITextComponent message, String messageText) {
		Matcher matcher = snitchNotificationMessageStartRegex.matcher(messageText);
		if (!matcher.matches()) {
			return false;
		}
		Snitch snitch = snitchFromMessage(message, messageText, "a snitch notification");
		if (snitch == null) {
			return true;
		}
		manager.submitSnitch(snitch);
		manager.saveSnitches();
		return true;
	}

	private Snitch snitchFromMessage(
			ITextComponent message, String messageText, String logMessageType) {
		String hoverText = hoverTextFromMessage(message);
		if (hoverText == null) {
			SnitchMaster.logger.info(
				String.format(
					"[SnitchMaster] While parsing %s message [1]: "
					+ "No hover text. [1]: '%s'",
					logMessageType, messageText));
			return null;
		}
		Snitch snitch = snitchFromSnitchMessageHoverText(hoverText);
		if (snitch == null) {
			SnitchMaster.logger.info(
				String.format(
					"[SnitchMaster] While parsing %s message [1]: "
					+ "Failed to parse hover text [2]. [1]: '%s' [2]: '%s'",
					logMessageType, messageText, hoverText));
			return null;
		}
		return snitch;
	}

	private String hoverTextFromMessage(ITextComponent message) {
		HoverEvent hover;
		List<ITextComponent> siblings = message.getSiblings();
		if (siblings == null || siblings.size() <= 0) {
			hover = message.getStyle().getHoverEvent();
		} else {
			ITextComponent hoverComponent = siblings.get(0);
			hover = hoverComponent.getStyle().getHoverEvent();
		}
		if (hover == null) {
			return null;
		}
		String text = hover.getValue().getUnformattedComponentText();
		if (text.trim().isEmpty()) {
			return null;
		}
		return text;
	}

	private Snitch snitchFromSnitchMessageHoverText(String hoverText) {
		try {
			return _snitchFromSnitchMessageHoverText(hoverText);
		} catch (Exception e) {
			return null;
		}
	}

	private Snitch _snitchFromSnitchMessageHoverText(String hoverText) {
		Matcher matcher = snitchMessageHoverTextRegex.matcher(hoverText);
		if (!matcher.matches()) {
			return null;
		}

		String worldName = matcher.group(1);
		int x = Integer.parseInt(matcher.group(2));
		int y = Integer.parseInt(matcher.group(3));
		int z = Integer.parseInt(matcher.group(4));
		String ctGroup = matcher.group(5);
		String type = matcher.group(6).toLowerCase();
		Double cullTime;
		if (matcher.group(7) == null || matcher.group(7).isEmpty()) {
			cullTime = SnitchMaster.CULL_TIME_ENABLED ? Snitch.MAX_CULL_TIME : Double.NaN;
		} else {
			cullTime = Double.parseDouble(matcher.group(7));
		}
		String previousName = matcher.group(8);
		String name = matcher.group(9);

		return new Snitch(
			new Location(x, y, z, worldName),
			SnitchTags.FROM_JALIST,
			cullTime,
			ctGroup,
			name,
			previousName,
			type);
	}

	private boolean tryParseSnitchInfoMessage(ITextComponent message, String messageText) {
		Matcher matcher = snitchInfoMessageStartRegex.matcher(messageText);
		if (!matcher.matches()) {
			return false;
		}
		Snitch snitch = snitchFromMessage(message, messageText, "a snitch info");
		if (snitch == null) {
			return true;
		}
		manager.submitSnitch(snitch);
		manager.saveSnitches();
		return true;
	}

	private boolean tryParseSnitchCreateMessage(ITextComponent message, String messageText) {
		Matcher matcher = snitchCreateMessageStartRegex.matcher(messageText);
		if (!matcher.matches()) {
			return false;
		}
		Snitch snitch = snitchFromMessage(message, messageText, "a snitch create");
		if (snitch == null) {
			return true;
		}
		manager.submitSnitch(snitch);
		manager.saveSnitches();
		return true;
	}

	private boolean tryParseSnitchBreakMessage(ITextComponent message, String messageText) {
		Matcher matcher = snitchBreakMessageStartRegex.matcher(messageText);
		if (!matcher.matches()) {
			return false;
		}
		Snitch snitch = snitchFromMessage(message, messageText, "a snitch break");
		if (snitch == null) {
			return true;
		}
		manager.getSnitches().remove(snitch.getLocation());
		manager.saveSnitches();
		return true;
	}

	private boolean tryParseSnitchNameChangeMessage(ITextComponent message, String messageText) {
		Matcher matcher = snitchNameChangeMessageStartRegex.matcher(messageText);
		if (!matcher.matches()) {
			return false;
		}
		Snitch snitch = snitchFromMessage(message, messageText, "a snitch name change");
		if (snitch == null) {
			return true;
		}
		Snitch existingSnitch = manager.getSnitches().get(snitch.getLocation());
		if (existingSnitch == null) {
			manager.submitSnitch(snitch);
			manager.saveSnitches();
			return true;
		}
		manager.setSnitchName(existingSnitch, snitch.getName());
		manager.saveSnitches();
		return true;
	}

	private boolean tryParseJalistMsg(ITextComponent msg) {
		String snitchLines = stripMinecraftFormattingCodes(msg.getUnformattedText());
		if (!snitchLines.trim().startsWith("Snitch List for")) {
			return false;
		}
		SnitchMaster.logger.info(
			"[SnitchMaster] Parsing /jalist message with the legacy parser ...");
		if (tryParseJalistMsgLegacy(msg)) {
			return true;
		}
		SnitchMaster.logger.info(
			"[SnitchMaster] The legacy parser failed, trying the JSON parser ...");
		if (tryParseJalistMsgJson(msg)) {
			return true;
		}
		SnitchMaster.logger.info(
			"[SnitchMaster] The JSON parser failed, trying the fallback parser ...");
		if (tryParseJalistMsgFallback(msg)) {
			return true;
		}
		String errMsg = "[SnitchMaster] Error: Failed to parse the /jalist message.";
		SnitchMaster.logger.error(errMsg);
		SnitchMaster.SendMessageToPlayer(errMsg);
		return false;
	}

	private boolean tryParseJalistMsgLegacy(ITextComponent msg) {
		List<ITextComponent> snitchRows;
		try {
			ITextComponent snitchListComponent = msg.getSiblings().get(0);
			snitchRows = snitchListComponent.getSiblings();
			if (snitchRows.isEmpty()) {
				return false;
			}
		} catch (IndexOutOfBoundsException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}

		Pattern jaListPattern = Pattern.compile(
		   "^(?i)\\s*Location:\\s*\\[(\\S+?) (-?[0-9]+) (-?[0-9]+) (-?[0-9]+)\\]\\s*Group:\\s*(\\S+?)\\s*Type:\\s*(Entry|Logging)\\s*(?:Cull:\\s*([0-9]+\\.[0-9]+)h?)?\\s*(?:Previous name:\\s*(\\S+?))?\\s*(?:Name:\\s*(\\S+?))?\\s*", Pattern.MULTILINE);

		for (ITextComponent row : snitchRows) {
			String hoverText = "";
			try {
				// For some reason row.getStyle() is never null.
				HoverEvent event = row.getStyle().getHoverEvent();
				if (event != null) {
					hoverText = event.getValue().getUnformattedComponentText();
					Matcher matcher = jaListPattern.matcher(hoverText);
					if (!matcher.matches()) {
						SnitchMaster.SendMessageToPlayer(
							"[Snitch Master] Error: /jalist hover text regex doesn't match");
						continue;
					}

					Snitch snitch = parseSnitchFromJaListLegacy(matcher);
					if (loadedSnitches != null) {
						loadedSnitches.add(snitch);
					}
					manager.submitSnitch(snitch);
				}
			} catch (IllegalStateException e) {
				SnitchMaster.SendMessageToPlayer("No match on /jalist hover text " + hoverText);
				e.printStackTrace();
			}
		}
		return true;
	}

	private Snitch parseSnitchFromJaListLegacy(Matcher matcher) {
		String worldName = matcher.group(1);
		int x = Integer.parseInt(matcher.group(2));
		int y = Integer.parseInt(matcher.group(3));
		int z = Integer.parseInt(matcher.group(4));
		double cullTime;

		String cullTimeString = matcher.group(7);
		if (cullTimeString == null || cullTimeString.isEmpty()) {
			cullTime = Double.NaN;
		} else {
			cullTime = Double.parseDouble(cullTimeString);
		}

		String ctGroup = matcher.group(5);
		String type = matcher.group(6).toLowerCase();
		String name = matcher.group(9);

		return new Snitch(
			new Location(x, y, z, worldName),
			SnitchTags.FROM_JALIST,
			cullTime,
			ctGroup,
			name,
			null,
			type);
	}

	private boolean tryParseJalistMsgJson(ITextComponent msg) {
		String json = ITextComponent.Serializer.componentToJson(msg);
		JsonElement jelement = new JsonParser().parse(json);

		JsonObject jobject = jelement.getAsJsonObject();
		if (!jobject.has("extra")) {
			return false;
		}
		JsonArray jarray = jobject.getAsJsonArray("extra");

		List<String> hovers = new ArrayList<String>();
		for (JsonElement line : jarray) {
			jobject = line.getAsJsonObject();
			if (!jobject.has("hoverEvent")) {
				continue;
			}
			jobject = jobject.getAsJsonObject("hoverEvent");

			if (!jobject.has("value")) {
				continue;
			}
			jobject = jobject.getAsJsonObject("value");

			if (!jobject.has("text")) {
				continue;
			}
			String text = jobject.get("text").getAsString().replaceAll("\\\\n", " ");
			hovers.add(text);
		}
		SnitchMaster.logger.info(
			String.format(
				"[SnitchMaster] JSON parser: Hover snitch line count in message: %d/10.",
				hovers.size()));
		if (hovers.size() == 0) {
			return false;
		}

		Snitch snitch;
		for (String hover : hovers) {
			snitch = snitchFromSnitchMessageHoverText(hover);
			if (snitch == null) {
				SnitchMaster.logger.error(
					String.format(
						"[SnitchMaster] JSON parser: Error: Failed to parse snitch from hover '%s'.", hover));
				return false;
			}
			if (loadedSnitches != null) {
				loadedSnitches.add(snitch);
			}
			manager.submitSnitch(snitch);
		}
		return true;
	}

	private boolean tryParseJalistMsgFallback(ITextComponent msg) {
		String snitchLines = stripMinecraftFormattingCodes(msg.getUnformattedText());
		if (!snitchLines.trim().startsWith("Snitch List for")) {
			return false;
		}

		Pattern worldPattern = Pattern.compile("^\\s*Snitch List for (\\S+?)\\s*$");
		// [-293 10 1099]     651.83   SN Bunker
		Pattern snitchPattern = Pattern.compile(
			"^(?i)\\s*\\[(-?[0-9]+) (-?[0-9]+) (-?[0-9]+)\\]\\s*([0-9]+\\.[0-9]+)h?\\s+(\\S+?)(?:\\s|$)\\s*(\\S*?)\\s*$");

		String world = "world";
		for (String line : snitchLines.split("\n")) {
			Matcher matcher = snitchPattern.matcher(line);
			if (!matcher.matches()) {
				matcher = worldPattern.matcher(line);
				if (matcher.matches()) {
					world = matcher.group(1);
				}
				continue;
			}
			Snitch snitch = parseSnitchFromJaListFallback(matcher, world);
			if (loadedSnitches != null) {
				loadedSnitches.add(snitch);
			}
			manager.submitSnitch(snitch);
		}
		return true;
	}

	public static String stripMinecraftFormattingCodes(String str) {
		return str.replaceAll("(?i)\\u00A7[a-z0-9]", "");
	}

	private Snitch parseSnitchFromJaListFallback(Matcher matcher, String world) {
		int x = Integer.parseInt(matcher.group(1));
		int y = Integer.parseInt(matcher.group(2));
		int z = Integer.parseInt(matcher.group(3));

		String cullTimeString = matcher.group(4);
		double cullTime;
		if (cullTimeString == null || cullTimeString.isEmpty()) {
			cullTime = Double.NaN;
		} else {
			cullTime = Double.parseDouble(cullTimeString);
		}

		String ctGroup = matcher.group(5);
		String type = null;
		String name = matcher.group(6);

		return new Snitch(
			new Location(x, y, z, world),
			SnitchTags.FROM_JALIST,
			cullTime,
			ctGroup,
			name,
			null,
			type);
	}

	@SubscribeEvent
	public void tickEvent(TickEvent.ClientTickEvent event) {
		if (!updatingSnitchList) {
			return;
		}
		// Player disconnected while the update was running.
		if (Minecraft.getMinecraft().player == null) {
			resetUpdatingSnitchList(/* save = */ true, /* cancelled = */ true);
			return;
		}

		long timeNow = System.currentTimeMillis();
		if (timeNow <= nextCommandRunTime) {
			return;
		}

		// '/tps' has priority over all other commands.
		if (nextTpsRunTime != null && timeNow > nextTpsRunTime) {
			Minecraft.getMinecraft().player.sendChatMessage("/tps");
			updateNextCommandRunTime();
			updateNextTpsRunTime();
			return;
		}

		if (maxJaListIndex != -1 && jaListIndex - 1 >= maxJaListIndex) {
			resetUpdatingSnitchList(/* save = */ true, /* cancelled = */ false);
			SnitchMaster.SendMessageToPlayer("Finished targeted snitch update");
		} else {
			Minecraft.getMinecraft().player.sendChatMessage("/jalistlong " + jaListIndex);
			ChatSpamState chatSpamSetting = (ChatSpamState) snitchMaster.getSettings().getValue(Settings.CHAT_SPAM_KEY);
			if (chatSpamSetting == Settings.ChatSpamState.PAGENUMBERS) {
				SnitchMaster.SendMessageToPlayer("Parsed snitches from /jalist " + jaListIndex);
			}
			jaListIndex++;
			updateNextCommandRunTime();
		}
	}

	private void updateNextCommandRunTime() {
		if (avgTps1Min == null) {
			avgTps1Min = 19.0;
		}
		double delta = 1.0;
		if (avgTps1MinPrev != null) {
			delta = avgTps1MinPrev - avgTps1Min;
		}
		if (delta < 0) {
			delta = 1.0;
		}
		// Give the player some leeway to send their own chat messages.
		double assumedTps = avgTps1Min - delta - 1;
		if (tpsIsDecreasing()) {
			nextCommandRunTime = timeInXSeconds(tickTimeoutSec / (assumedTps - 1));
			return;
		}
		nextCommandRunTime = timeInXSeconds(tickTimeoutSec / assumedTps);
	}

	private void updateNextTpsRunTime() {
		if (!updatingSnitchList) {
			nextTpsRunTime = null;
			avgTps1MinPrev = null;
			avgTps1Min = null;
			avgTps5Min = null;
			avgTps15Min = null;
			return;
		}

		// Run /tps again to be able to see if TPS is decreasing.
		if (avgTps1Min == null || avgTps1MinPrev == null) {
			nextTpsRunTime = timeInXSeconds(2);
			return;
		}

		if (tpsIsDecreasing()) {
			nextTpsRunTime = timeInXSeconds(2);
			return;
		}

		// TPS below 19 typically means the server isn't in ideal shape.
		if (avgTps1Min < 19.0) {
			nextTpsRunTime = timeInXSeconds(30);
			return;
		}

		// TPS above 19, the server is probably stable.
		nextTpsRunTime = timeInXSeconds(60);
	}

	private static long timeInXSeconds(double waitTimeSec) {
		return System.currentTimeMillis() + (long) (waitTimeSec * 1000);
	}

	private boolean tpsIsDecreasing() {
		if (
			avgTps1Min == null
			|| avgTps1MinPrev == null
			|| avgTps1Min < avgTps1MinPrev
			|| avgTps5Min == null
		) {
			return true;
		}
		return avgTps1Min + 0.5 < avgTps5Min;
	}

	public boolean isUpdatingSnitchList() {
		return updatingSnitchList;
	}

	public void updateSnitchList() {
		resetUpdatingSnitchList(/* save = */ false, /* cancelled = */ false);

		snitchesCopy = new HashSet<>();
		loadedSnitches = new HashSet<>();

		for (Snitch snitch : manager.getSnitches()) {
			if (!snitch.isTagged(SnitchTags.IS_GONE) && snitch.isTagged(SnitchTags.FROM_JALIST)) {
				snitchesCopy.add(snitch);
			}
		}

		updatingSnitchList = true;
		Minecraft.getMinecraft().player.sendChatMessage("/tps");
		updateNextCommandRunTime();
	}

	public void updateSnitchList(int startIndex, int stopIndex) {
		resetUpdatingSnitchList(/* save = */ false, /* cancelled = */ false);

		jaListIndex = startIndex;
		maxJaListIndex = stopIndex;

		updatingSnitchList = true;
		Minecraft.getMinecraft().player.sendChatMessage("/tps");
		updateNextCommandRunTime();
	}

	/**
	 * Resets the updating of Snitches from the /jalist command.
	 * If an update is currently in progress, it is stopped.
	 */
	public void resetUpdatingSnitchList(boolean save, boolean cancelled) {
		if (updatingSnitchList && snitchesCopy != null) {
			if (!cancelled) {
				snitchesCopy.removeAll(loadedSnitches);
				for (Snitch snitch : snitchesCopy) {
					manager.addTag(snitch, SnitchTags.IS_GONE);
				}
				SnitchMaster.SendMessageToPlayer(
					snitchesCopy.size() + " snitches were missing since the last full update.");
			}
			snitchesCopy.clear();
			loadedSnitches.clear();
			snitchesCopy = null;
			loadedSnitches = null;
		}

		jaListIndex = 1;
		maxJaListIndex = -1;
		updatingSnitchList = false;
		updateNextTpsRunTime();

		if (save) {
			manager.saveSnitches();
		}
	}

	private static SnitchAlert buildSnitchAlert(Matcher matcher, ITextComponent message) {
		String playerName = matcher.group(1);
		String activity = matcher.group(2);
		String snitchName = matcher.group(3);
		String worldName = matcher.group(4);
		int x = Integer.parseInt(matcher.group(5));
		int y = Integer.parseInt(matcher.group(6));
		int z = Integer.parseInt(matcher.group(7));
		return new SnitchAlert(playerName, x, y, z, activity, snitchName, worldName, message);
	}

	private static boolean containsAny(String message, String[] tokens) {
		for (String token : tokens) {
			if (message.contains(token)) {
				return true;
			}
		}
		return false;
	}
}
