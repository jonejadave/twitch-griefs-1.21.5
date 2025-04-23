package net.soren.custom.config;

import com.mojang.datafixers.util.Pair;
import net.soren.custom.twitchGriefMain;

public class ModConfigs {
    public static SimpleConfig CONFIG;
    private static ModConfigProvider configs;

    public static String MINECRAFT_USERNAME;
    public static String TWITCH_OAUTH_KEY;

    public static void registerConfigs() {
        configs = new ModConfigProvider();
        createConfigs();

        CONFIG = SimpleConfig.of(twitchGriefMain.MOD_ID + "config").provider(configs).request();

        assignConfigs();
    }

    private static void createConfigs() {
        configs.addKeyValuePair(new Pair<>("twitch.oauth.key", "[your-twitch-oauth-here]"), "");
    }


    private static void assignConfigs() {
        MINECRAFT_USERNAME = CONFIG.getOrDefault("user.minecraft.username", "Steve");
        TWITCH_OAUTH_KEY = CONFIG.getOrDefault("twitch.oauth.key", "oauth:default");

        System.out.println("Loaded Twitch config for user: " + MINECRAFT_USERNAME);
    }

}