package cc.rexsystems.rexwelcome.config;

import cc.rexsystems.rexwelcome.RexWelcome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Merges missing defaults from the bundled config.yml into the user's config
 * without overwriting existing values.
 */
public final class ConfigAutoUpdater {

    private final RexWelcome plugin;

    public ConfigAutoUpdater(RexWelcome plugin) {
        this.plugin = plugin;
    }

    public void ensureDefaults() {
        try {
            FileConfiguration userCfg = plugin.getConfig();
            InputStream is = plugin.getResource("config.yml");
            if (is == null) {
                plugin.getLogger().warning("Default config.yml resource not found; skipping auto-update.");
                return;
            }

            YamlConfiguration defaults = YamlConfiguration
                    .loadConfiguration(new InputStreamReader(is, StandardCharsets.UTF_8));

            int missing = mergeMissingKeys(userCfg, defaults);
            if (missing <= 0) {
                return;
            }

            backupConfigSafely();
            plugin.saveConfig();
            plugin.getLogger().info("Config auto-update: added " + missing + " missing key(s).");
        } catch (Throwable t) {
            plugin.getLogger().warning("Config auto-update failed: " + t.getMessage());
        }
    }

    private int mergeMissingKeys(FileConfiguration userCfg, YamlConfiguration defaults) {
        int count = 0;

        for (String key : defaults.getKeys(true)) {
            if (defaults.isConfigurationSection(key)) {
                continue;
            }
            if (userCfg.isSet(key)) {
                continue;
            }

            userCfg.set(key, defaults.get(key));
            count++;
        }

        return count;
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
