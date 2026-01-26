package cc.rexsystems.rexwelcome.config;

import cc.rexsystems.rexwelcome.RexWelcome;
import cc.rexsystems.rexwelcome.utils.ColorUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConfigManager {

    private final RexWelcome plugin;
    private FileConfiguration config;
    private final Random random = new Random();

    // Cached config values
    private String prefix;
    private List<TitleMessage> returningTitles;
    private List<TitleMessage> firstJoinTitles;
    private List<String> returningMessages;
    private List<String> firstJoinMessages;
    private String broadcastMessage;
    private boolean welcomeEnabled;
    private boolean firstJoinEnabled;
    private boolean broadcastEnabled;
    private boolean titleEnabled;
    private boolean clearChatEnabled;
    private boolean soundEnabled;
    private String joinSound;
    private float soundVolume;
    private float soundPitch;
    private int titleFadeIn;
    private int titleStay;
    private int titleFadeOut;

    public ConfigManager(RexWelcome plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        reload();
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        loadConfig();
    }

    private void loadConfig() {
        // Prefix
        prefix = config.getString("messages.prefix", "&7[&#f75634RexWelcome&7] ");

        // Welcome settings
        welcomeEnabled = config.getBoolean("welcome-messages.enabled", true);
        clearChatEnabled = config.getBoolean("welcome-messages.clear-chat", false);
        titleEnabled = config.getBoolean("welcome-messages.title.enabled", true);
        titleFadeIn = config.getInt("welcome-messages.title.fade-in", 10);
        titleStay = config.getInt("welcome-messages.title.stay", 70);
        titleFadeOut = config.getInt("welcome-messages.title.fade-out", 20);

        // Returning player titles
        returningTitles = loadTitles("returning-player.titles");
        if (returningTitles.isEmpty()) {
            // Fallback to old config format
            String title = config.getString("welcome-messages.title.title", "&6WELCOME BACK!");
            String subtitle = config.getString("welcome-messages.title.subtitle", "&fWe missed you!");
            returningTitles.add(new TitleMessage(title, subtitle));
        }

        // First join titles
        firstJoinTitles = loadTitles("first-join.titles");
        if (firstJoinTitles.isEmpty()) {
            firstJoinTitles.add(new TitleMessage("&b✨ WELCOME!", "&fThis is your first time here!"));
        }

        // Returning messages
        returningMessages = config.getStringList("returning-player.messages");
        if (returningMessages.isEmpty()) {
            returningMessages = config.getStringList("welcome-messages.messages");
        }

        // First join messages
        firstJoinMessages = config.getStringList("first-join.messages");

        // First join settings
        firstJoinEnabled = config.getBoolean("first-join.enabled", true);
        broadcastEnabled = config.getBoolean("broadcast.enabled", true);
        broadcastMessage = config.getString("broadcast.message",
                "%prefix%&#f75634%player% &fhas joined for the first time!");

        // Sound settings
        soundEnabled = config.getBoolean("sounds.join.enabled", true);
        joinSound = config.getString("sounds.join.sound", "ENTITY_PLAYER_LEVELUP");
        soundVolume = (float) config.getDouble("sounds.join.volume", 1.0);
        soundPitch = (float) config.getDouble("sounds.join.pitch", 1.0);

        // First join commands
        firstJoinCommands = config.getStringList("first-join.commands");
    }

    private List<String> firstJoinCommands;

    private List<TitleMessage> loadTitles(String path) {
        List<TitleMessage> titles = new ArrayList<>();

        if (config.isList(path)) {
            List<?> titleList = config.getList(path);
            if (titleList != null) {
                for (Object obj : titleList) {
                    if (obj instanceof java.util.Map) {
                        @SuppressWarnings("unchecked")
                        java.util.Map<String, Object> map = (java.util.Map<String, Object>) obj;
                        String title = (String) map.getOrDefault("title", "");
                        String subtitle = (String) map.getOrDefault("subtitle", "");
                        titles.add(new TitleMessage(title, subtitle));
                    }
                }
            }
        }

        return titles;
    }

    // Getters
    public String getPrefix() {
        return prefix;
    }

    public String getMessage(String path) {
        String message = config.getString("messages." + path, "");
        message = message.replace("%prefix%", prefix);
        return ColorUtils.colorize(message);
    }

    public TitleMessage getRandomReturningTitle() {
        if (returningTitles.isEmpty()) {
            return new TitleMessage("&6WELCOME BACK!", "&fGood to see you!");
        }
        return returningTitles.get(random.nextInt(returningTitles.size()));
    }

    public TitleMessage getRandomFirstJoinTitle() {
        if (firstJoinTitles.isEmpty()) {
            return new TitleMessage("&b✨ WELCOME!", "&fThis is your first time!");
        }
        return firstJoinTitles.get(random.nextInt(firstJoinTitles.size()));
    }

    public List<String> getReturningMessages() {
        return returningMessages;
    }

    public List<String> getFirstJoinMessages() {
        return firstJoinMessages;
    }

    public String getBroadcastMessage() {
        return broadcastMessage;
    }

    public boolean isWelcomeEnabled() {
        return welcomeEnabled;
    }

    public boolean isClearChatEnabled() {
        return clearChatEnabled;
    }

    public boolean isFirstJoinEnabled() {
        return firstJoinEnabled;
    }

    public boolean isBroadcastEnabled() {
        return broadcastEnabled;
    }

    public boolean isTitleEnabled() {
        return titleEnabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public String getJoinSound() {
        return joinSound;
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public float getSoundPitch() {
        return soundPitch;
    }

    public int getTitleFadeIn() {
        return titleFadeIn;
    }

    public int getTitleStay() {
        return titleStay;
    }

    public int getTitleFadeOut() {
        return titleFadeOut;
    }

    public List<String> getFirstJoinCommands() {
        return firstJoinCommands != null ? firstJoinCommands : new ArrayList<>();
    }

    // Inner class for title messages
    public static class TitleMessage {
        private final String title;
        private final String subtitle;

        public TitleMessage(String title, String subtitle) {
            this.title = title;
            this.subtitle = subtitle;
        }

        public String getTitle() {
            return title;
        }

        public String getSubtitle() {
            return subtitle;
        }
    }
}
