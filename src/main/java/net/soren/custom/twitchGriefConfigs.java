package net.soren.custom;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class twitchGriefConfigs {
    private static final Gson GSON = new Gson();
    private static final String CONFIG_FILE = "config/twitch-griefs-config.json";

    public static String minecraftUsername = "[your-minecraft-username]";
    public static String twitchOAuthKey = "oauth:[twitch_key]";

    public static void loadConfig() {
        File file = new File(CONFIG_FILE);

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                twitchGriefConfigs loaded = GSON.fromJson(reader, twitchGriefConfigs.class);
                if (loaded != null) {
                    minecraftUsername = loaded.minecraftUsername;
                    twitchOAuthKey = loaded.twitchOAuthKey;
                    twitchGriefMain.LOGGER.info("Config loaded successfully.");
                }
            } catch (IOException | JsonSyntaxException e) {
                twitchGriefMain.LOGGER.error("Failed to load config! Using defaults.", e);
            }
        } else {
            saveDefaultConfig();
        }
    }

    public static void saveDefaultConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(new twitchGriefConfigs(), writer);
            twitchGriefMain.LOGGER.info("Default config created.");
        } catch (IOException e) {
            twitchGriefMain.LOGGER.error("Failed to save default config!", e);
        }
    }
}
