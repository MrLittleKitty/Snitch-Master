package com.gmail.nuclearcat1337.snitch_master;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Settings {
	private static final String modSettingsFile = SnitchMaster.modDataFolder + "/Settings.txt";

	public enum ChatSpamState {
		ON, OFF, PAGENUMBERS;
	}

	public static final String CHAT_SPAM_KEY = "chat-spam";
	public static final String RENDER_TEXT_KEY = "render-text";
	public static final String MANUAL_MODE_KEY = "manual-mode";

	private final File file;
	private final ValueParser parser;

	private final HashMap<String, Object> values;

	public Settings(ValueParser parser) {
		this.parser = parser;
		values = new HashMap<>();

		file = new File(modSettingsFile);
		if (file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void setValueIfNotSet(String key, Object value) {
		if (!values.containsKey(key)) {
			values.put(key, value);
		}
	}

	public boolean hasValue(String key) {
		return values.containsKey(key);
	}

	public void setValue(String key, Object value) {
		values.put(key, value);
	}

	public Object getValue(String key) {
		return values.get(key);
	}

	public void loadSettings() {
		if (file.exists()) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = null;
				while ((line = reader.readLine()) != null) {
					String[] tokens = line.split("=");
					Object value = parser.parse(tokens[0], tokens[1]);
					values.put(tokens[0], value);
				}
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveSettings() {
		BufferedWriter writer = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			writer = new BufferedWriter(new FileWriter(file));
			for (Map.Entry<String, Object> entry : values.entrySet()) {
				writer.write(entry.getKey());
				writer.write('=');
				writer.write(entry.getValue().toString());
				writer.write(System.lineSeparator());
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public interface ValueParser {
		Object parse(String key, String value);
	}
}
