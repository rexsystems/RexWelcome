package cc.rexsystems.rexwelcome;

import cc.rexsystems.rexwelcome.commands.RexWelcomeCommand;
import cc.rexsystems.rexwelcome.config.ConfigManager;
import cc.rexsystems.rexwelcome.data.PlayerDataManager;
import cc.rexsystems.rexwelcome.listeners.PlayerJoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class RexWelcome extends JavaPlugin {

    private static RexWelcome instance;
    private ConfigManager configManager;
    private PlayerDataManager playerDataManager;

    @Override
    public void onEnable() {
        instance = this;

        // Initialize managers
        configManager = new ConfigManager(this);
        playerDataManager = new PlayerDataManager(this);

        // Register listeners
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);

        // Register commands
        RexWelcomeCommand command = new RexWelcomeCommand(this);
        getCommand("rexwelcome").setExecutor(command);
        getCommand("rexwelcome").setTabCompleter(command);

        // Register PlaceholderAPI expansion if available
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new cc.rexsystems.rexwelcome.placeholder.RexWelcomePlaceholder(this).register();
            getLogger().info("PlaceholderAPI hook registered!");
        }

        getLogger().info("RexWelcome has been enabled!");
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.save();
        }
        getLogger().info("RexWelcome has been disabled!");
    }

    public void reload() {
        configManager.reload();
        playerDataManager.reload();
    }

    public static RexWelcome getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

}
