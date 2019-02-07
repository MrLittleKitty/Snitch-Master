package com.gmail.nuclearcat1337.snitch_master;

import com.gmail.nuclearcat1337.snitch_master.util.ChatSpamState;
import com.gmail.nuclearcat1337.snitch_master.util.Pair;
import com.gmail.nuclearcat1337.snitch_master.util.QuietTimeConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Mr_Little_Kitty on 7/9/2016.
 */
public class Settings {
    public static final int SNITCH_RADIUS = 11;
    private static final String SETTINGS_FILE = SnitchMaster.modDataFolder + "/Settings.txt";

    private boolean globalRender = true;
    private boolean renderText = true;
    private boolean manualMode = false;
    private ChatSpamState chatSpamState = ChatSpamState.ON;
    private QuietTimeConfig quietTimeConfig = QuietTimeConfig.NORMAL;

    private List<Pair<String, Boolean>> snitchColumns = new ArrayList<>();
    private List<Pair<String, Boolean>> snitchListColumns = new ArrayList<>();

    public Settings() {
    }

    public void setGlobalRender(boolean globalRender) {
        this.globalRender = globalRender;
    }

    public void setRenderText(boolean renderText) {
        this.renderText = renderText;
    }

    public void setManualMode(boolean manualMode) {
        this.manualMode = manualMode;
    }

    public void setChatSpamState(ChatSpamState chatSpamState) {
        this.chatSpamState = chatSpamState;
    }

    public void setQuietTimeConfig(QuietTimeConfig quietTimeConfig) {
        this.quietTimeConfig = quietTimeConfig;
    }

    public void setSnitchColumns(List<Pair<String, Boolean>> snitchColumns) {
        this.snitchColumns = snitchColumns;
    }

    public void setSnitchListColumns(List<Pair<String, Boolean>> snitchListColumns) {
        this.snitchListColumns = snitchListColumns;
    }

    public ChatSpamState getChatSpamState() {
        return this.chatSpamState;
    }

    public boolean getRenderText() {
        return this.renderText;
    }

    public boolean getManualMode() {
        return this.manualMode;
    }

    public boolean getGlobalRender() {
        return this.globalRender;
    }

    public QuietTimeConfig getQuietTimeConfig() {
        return this.quietTimeConfig;
    }

    public List<Pair<String, Boolean>> getSnitchColumns() {
        return snitchColumns;
    }

    public List<Pair<String, Boolean>> getSnitchListColumns() {
        return snitchListColumns;
    }

    public void loadSettings() {
        final File settingsFile = new File(SETTINGS_FILE);
        if (settingsFile.exists()) {
            final Gson gson = new Gson();
            try (FileReader reader = new FileReader(settingsFile)) {
                SerializedSettings settings = gson.fromJson(reader, SerializedSettings.class);
                fromSerializedSettings(settings);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveSettings() {
        final SerializedSettings settings = toSerializedSettings();
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String serialized = gson.toJson(settings);

        final File settingsFile = new File(SETTINGS_FILE);
        final File directory = settingsFile.getParentFile();
        if (!directory.exists() || !directory.isDirectory()) {
            directory.mkdir();
        }
        try {
            FileUtils.write(settingsFile, serialized, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void fromSerializedSettings(final SerializedSettings settings) {
        if (settings != null) {
            if (settings.globalRender != null) {
                this.globalRender = settings.globalRender;
            }
            if (settings.renderText != null) {
                this.renderText = settings.renderText;
            }
            if (settings.manualMode != null) {
                this.manualMode = settings.manualMode;
            }
            if (settings.chatSpamState != null) {
                this.chatSpamState = settings.chatSpamState;
            }
            if (settings.quietTimeConfig != null && settings.quietTimeConfig.literals != null &&
                    settings.quietTimeConfig.instructions != null) {
                this.quietTimeConfig = settings.quietTimeConfig;
            }
            if (settings.snitchColumns != null && settings.snitchColumnsRender != null) {
                this.snitchColumns = loadList(settings.snitchColumns, settings.snitchColumnsRender);
            }
            if (settings.snitchListColumns != null && settings.snitchListColumnsRender != null) {
                this.snitchListColumns = loadList(settings.snitchListColumns, settings.snitchListColumnsRender);
            }
        }
    }

    private SerializedSettings toSerializedSettings() {
        final SerializedSettings settings = new SerializedSettings();
        settings.globalRender = globalRender;
        settings.renderText = renderText;
        settings.manualMode = manualMode;
        settings.chatSpamState = chatSpamState;
        settings.quietTimeConfig = quietTimeConfig;

        settings.snitchColumns = new String[snitchColumns.size()];
        settings.snitchColumnsRender = new Boolean[snitchColumns.size()];
        fillSaveArrays(snitchColumns, settings.snitchColumns, settings.snitchColumnsRender);

        settings.snitchListColumns = new String[snitchListColumns.size()];
        settings.snitchListColumnsRender = new Boolean[snitchListColumns.size()];
        fillSaveArrays(snitchListColumns, settings.snitchListColumns, settings.snitchListColumnsRender);

        return settings;
    }

    private List<Pair<String, Boolean>> loadList(final String[] names, final Boolean[] render) {
        final List<Pair<String, Boolean>> list = new ArrayList<>();
        for (int i = 0; i < names.length && i < render.length; i++) {
            list.add(new Pair<String, Boolean>(names[i], render[i]));
        }
        return list;
    }

    private void fillSaveArrays(final List<Pair<String, Boolean>> list, final String[] names, final Boolean[] render) {
        for (int i = 0; i < list.size(); i++) {
            final Pair<String, Boolean> pair = list.get(i);
            names[i] = pair.getOne();
            render[i] = pair.getTwo();
        }
    }

    private static class SerializedSettings {
        private Boolean globalRender;
        private Boolean renderText;
        private Boolean manualMode;
        private ChatSpamState chatSpamState;
        private QuietTimeConfig quietTimeConfig;

        private String[] snitchColumns;
        private Boolean[] snitchColumnsRender;

        private String[] snitchListColumns;
        private Boolean[] snitchListColumnsRender;
    }
}
