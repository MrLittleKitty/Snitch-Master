package com.gmail.nuclearcat1337.snitch_master.handlers;

import com.gmail.nuclearcat1337.snitch_master.Settings;
import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;
import com.gmail.nuclearcat1337.snitch_master.api.IAlertRecipient;
import com.gmail.nuclearcat1337.snitch_master.api.SnitchAlert;
import com.gmail.nuclearcat1337.snitch_master.util.QuietTimeConfig;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;

/**
 * Created by Mr_Little_Kitty on 12/30/2016.
 */
public class QuietTimeHandler implements IAlertRecipient {
	private static final byte B_PLAYER_NAME = 1;
	private static final byte B_ACTIVITY_TEXT = 2;
	private static final byte B_SNITCH_NAME = 3;
	private static final byte B_LOCATION = 4;
	private static final byte B_SNITCH_NAME_AND_LOCATION = 5;

	private static final byte B_STRING_SUBSTITUTION = 6;
	private static final byte B_HOVER_EVENT = 7;

	private static final byte B_INSERT_SPACE = 8;

	public static final String QUIET_TIME_CONFIG_KEY = "quiet-time-config";
	private final Settings settings;

	private static final Style aqua = new Style().setColor(TextFormatting.AQUA);
	private int index;
	private byte[] instructions;
	private String[] literals;

	public QuietTimeHandler(Settings settings) {
		this.settings = settings;
	}

	@Override
	public void receiveSnitchAlert(SnitchAlert alert) {
		index = 0;
		QuietTimeConfig quietTimeConfig = (QuietTimeConfig) settings.getValue(QUIET_TIME_CONFIG_KEY);
		instructions = quietTimeConfig.instructions;
		literals = quietTimeConfig.literals;

		ITextComponent currentComponent = null;
		StringBuilder builder = new StringBuilder("* ");

		for (index = 0; index < instructions.length; index++) {
			byte instruction = instructions[index];
			//If its the hover instruction then make sure there at least another 2 instructions
			if (instruction == B_HOVER_EVENT && index + 2 < instructions.length) {
				ITextComponent hoverComponent = parseHover(alert);
				if (hoverComponent != null) {
					if (currentComponent == null) {
						currentComponent = new TextComponentString(builder.toString()).setStyle(aqua.createShallowCopy());
						builder = new StringBuilder();
					}
					else if (builder.length() > 0) {
						currentComponent.appendSibling(new TextComponentString(builder.toString()).setStyle(aqua.createShallowCopy()));
						builder = new StringBuilder();
					}
					currentComponent.appendSibling(hoverComponent);
				} else {
					SnitchMaster.SendMessageToPlayer("Your quiet time config has an invalid hover instruction. Please load a working config.");
					return;
				}
			} else if (instruction == B_STRING_SUBSTITUTION && index + 1 < instructions.length) {
				index++;
				String sub = getStringSubstitution(instructions[index]);
				if (sub != null) {
					builder.append(sub);
				} else {
					SnitchMaster.SendMessageToPlayer("Your quiet time config has an invalid string sub. Please load a working config.");
					return;
				}
			} else if (instruction == B_INSERT_SPACE) {
				builder.append(" ");
			} else {
				String possibleText = getTextForCurrentInstruction(alert);
				if (possibleText != null) {
					builder.append(possibleText);
				} else {
					SnitchMaster.SendMessageToPlayer("Your quiet time config has an invalid instruction. Please load a working config.");
					return;
				}
			}
		}

		if (builder.length() > 0) {
			ITextComponent comp = new TextComponentString(builder.toString()).setStyle(aqua.createShallowCopy());
			if (currentComponent == null) {
				currentComponent = comp;
			} else {
				currentComponent.appendSibling(comp);
			}
		}

		if (currentComponent != null) {
			SnitchMaster.logger.info("Original Message: " + alert.getRawMessage().getUnformattedText());
			alert.setRawMessage(currentComponent);
		}
	}

	//PRECONDITION: Index is on the HOVER INSTRUCTION
	//POSTCONDITION: Index is on the instruction AFTER the last hover part --WAIT MAYBE NOT
	private ITextComponent parseHover(SnitchAlert alert) {
		try {
			//If we are parsing a hover then the current index is for the hover instruction
			//That means that we have 2 parts: 1-TEXT YOU SEE IN CHAT, 2-TEXT THAT YOU SEE WHEN YOU HOVER
			//Either of those 2 could be a string substitution or a variable instruction

			String text;
			//Move index to the first instruction part (text to show in chat)
			index++;
			String showText = getTextForCurrentInstruction(alert);
			//Move index to second instruction part (text shown on hover)
			index++;
			String hoverText = getTextForCurrentInstruction(alert);
			index++;

			HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponentString(hoverText));
			ITextComponent hoverComponent = new TextComponentString(showText).setStyle(new Style().setColor(TextFormatting.AQUA).setHoverEvent(hoverEvent));

			index--;//????
			return hoverComponent;
		} catch (Exception e) {
			return null;
		}
	}

	private String getTextForCurrentInstruction(SnitchAlert alert) {
		if (index >= instructions.length) {
			return null;
		}
		byte inst1 = instructions[index];
		if (inst1 == B_STRING_SUBSTITUTION) {
			index++;
			if (index >= instructions.length) {
				return null;
			}
			return getStringSubstitution(instructions[index]);
		} else {
			return GetString(alert, inst1);
		}
	}

	private String getStringSubstitution(byte index) {
		if (index < 0 || index >= literals.length) {
			return null;
		}
		return literals[index];
	}

	private static String GetString(SnitchAlert alert, int instruction) {
		if (instruction == B_PLAYER_NAME) {
			return alert.getPlayerName();
		}
		if (instruction == B_ACTIVITY_TEXT) {
			return alert.getActivity().getActivityText();
		}
		if (instruction == B_SNITCH_NAME) {
			return alert.getSnitchName();
		}
		if (instruction == B_LOCATION) {
			return alert.getLocation().toString();
		}
		if (instruction == B_SNITCH_NAME_AND_LOCATION) {
			return alert.getSnitchName() + System.lineSeparator() + alert.getLocation().toString();
		}
		return null;
	}
}
