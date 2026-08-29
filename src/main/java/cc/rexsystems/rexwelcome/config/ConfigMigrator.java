package cc.rexsystems.rexwelcome.config;

import cc.rexsystems.rexwelcome.RexWelcome;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Applies incremental config.yml migrations based on {@code config-version}.
 */
public final class ConfigMigrator {

    public static final int CURRENT_VERSION = 2;

    private final RexWelcome plugin;

    public ConfigMigrator(RexWelcome plugin) {
        this.plugin = plugin;
    }

    /**
     * @return number of version steps applied (0 if already up to date)
     */
    public int migrate(FileConfiguration config) {
        int version = config.getInt("config-version", 0);
        if (version > CURRENT_VERSION) {
            plugin.getLogger().warning(
                    "config-version " + version + " is newer than this plugin supports (" + CURRENT_VERSION + ").");
            return 0;
        }
        if (version >= CURRENT_VERSION) {
            return 0;
        }

        int fromVersion = version;
        while (version < CURRENT_VERSION) {
            switch (version + 1) {
                case 1 -> migrateToV1(config);
                case 2 -> migrateToV2(config);
                default -> {
                }
            }
            version++;
        }

        config.set("config-version", CURRENT_VERSION);
        backupConfigSafely();
        plugin.saveConfig();
        plugin.getLogger().info("Config migrated from v" + fromVersion + " to v" + CURRENT_VERSION + ".");
        return CURRENT_VERSION - fromVersion;
    }

    /** Configurable delayed welcome messages for async PlaceholderAPI expansions. */
    private void migrateToV1(FileConfiguration config) {
        ensureDelayedWelcome(config);
    }

    /** Ensure delayed-welcome exists for configs that reached v1 without it. */
    private void migrateToV2(FileConfiguration config) {
        ensureDelayedWelcome(config);
    }

    private void ensureDelayedWelcome(FileConfiguration config) {
        if (!config.isSet("welcome-messages.delayed-welcome.enabled")) {
            config.set("welcome-messages.delayed-welcome.enabled", false);
        }
        if (!config.isSet("welcome-messages.delayed-welcome.delay-ms")) {
            config.set("welcome-messages.delayed-welcome.delay-ms", 1000);
        }
    }

    private void backupConfigSafely() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                return;
            }
            File file = new File(dataFolder, "config.yml");
            if (!file.exists()) {
                return;
            }
            String ts = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            Path backup = new File(dataFolder, "config.yml.bak." + ts).toPath();
            Files.copy(file.toPath(), backup, StandardCopyOption.REPLACE_EXISTING);
        } catch (Throwable ignored) {
        }
    }
}
