package com.gmail.nuclearcat1337.snitch_master.util;

import com.gmail.nuclearcat1337.snitch_master.SnitchMaster;

import java.util.Arrays;

/**
 * Created by Mr_Little_Kitty on 2/22/2017.
 */
public class QuietTimeConfig {
	//TL;DR: Don't even bother trying to read these and understand what they represent. Its not your destiny.
	public static final QuietTimeConfig NORMAL = new QuietTimeConfig(new byte[]{1, 8, 2, 8, 3, 8, 4}, new String[]{});
	public static final QuietTimeConfig HIDE_COORDS = new QuietTimeConfig(new byte[]{1, 8, 2, 8, 3, 8, 7, 6, 0, 4}, new String[]{"[world X X X]"});
	public static final QuietTimeConfig HIDE_COORDS_AND_NAME = new QuietTimeConfig(new byte[]{1, 8, 2, 8, 7, 6, 0, 3, 8, 7, 6, 1, 4}, new String[]{"Hidden", "[world X X X]"});
	public static final QuietTimeConfig GJUM_SPECIAL = new QuietTimeConfig(new byte[]{1, 8, 2, 8, 7, 3, 4}, new String[]{});

	public byte[] instructions;
	public String[] literals;

	public QuietTimeConfig(byte[] instructions, String[] literals) {
		this.instructions = instructions;
		this.literals = literals;
	}

	@Override
	public String toString() {
		return Arrays.toString(instructions) + ":" + ToString(literals);
	}

	public static QuietTimeConfig GetDefaultQuietTimeConfig() {
		return NORMAL;
	}

	public static QuietTimeConfig FromString(String value) {
		try {
			String[] parts = value.split(":");
			if (parts.length < 1) {
				return GetDefaultQuietTimeConfig();
			}
			byte[] bytes = ParseByteArray(parts[0]);
			String[] literals = parts.length > 1 ? ParseStringArray(parts[1]) : new String[0];
			return new QuietTimeConfig(bytes, literals);
		} catch (Exception e) {
			SnitchMaster.instance.logger.info("");
			return GetDefaultQuietTimeConfig();
		}
	}

	private static String ToString(String[] literals) {
		StringBuilder builder = new StringBuilder();
		for (String str : literals) {
			builder.append(str.replace(":", "").replace(";", "")).append(';');
		}
		return builder.toString();
	}

	private static String[] ParseStringArray(String str) {
		return str.split(";");
	}

	private static byte[] ParseByteArray(String str) {
		str = str.replace("[", "").replace("]", "").replace(",", "");
		String[] b = str.split(" ");
		byte[] bytes = new byte[b.length];
		for (int i = 0; i < b.length; i++) {
			bytes[i] = Byte.parseByte(b[i]);
		}
		return bytes;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		QuietTimeConfig that = (QuietTimeConfig) o;

		if (!Arrays.equals(instructions, that.instructions)) {
			return false;
		}
		// Probably incorrect - comparing Object[] arrays with Arrays.equals
		return Arrays.equals(literals, that.literals);
	}

	@Override
	public int hashCode() {
		int result = Arrays.hashCode(instructions);
		result = 31 * result + Arrays.hashCode(literals);
		return result;
	}
}
