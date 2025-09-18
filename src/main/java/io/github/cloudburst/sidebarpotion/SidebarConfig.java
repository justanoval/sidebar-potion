package io.github.cloudburst.sidebarpotion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SidebarConfig {

    public enum DurationFormat {
        @SerializedName("")
        NONE,
        @SerializedName("t")
        TICKS,
        @SerializedName("s")
        SECONDS,
        @SerializedName("s.")
        SECONDS_MILLISECONDS,
        @SerializedName("mm:ss")
        MINUTES_SECONDS,
        @SerializedName("mm:ss.")
        MINUTES_SECONDS_MILLISECONDS
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = new File("./config/" + SidebarPotionMod.MOD_ID + ".json");

    public boolean showEffectName = true;
    public boolean colorizeText = true;
    public boolean hideDefaultGui = true;
    public boolean showPotency = true;
    public boolean showIcon = true;
    public DurationFormat durationFormat = DurationFormat.MINUTES_SECONDS;
    public String title = "";
    public boolean forceCompatIcons = false;

    public static SidebarConfig load() {
        if (!FILE.exists()) {
            SidebarConfig config = new SidebarConfig();
            config.save();
            return config;
        }
        try (FileReader reader = new FileReader(FILE)) {
            return GSON.fromJson(reader, SidebarConfig.class);
        } catch (IOException e) {
            SidebarPotionMod.LOGGER.error("Failed to load config file!", e);
            return new SidebarConfig();
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            SidebarPotionMod.LOGGER.error("Failed to save config file!", e);
        }
    }
}
