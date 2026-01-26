package cc.rexsystems.rexwelcome.data;

import cc.rexsystems.rexwelcome.RexWelcome;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerDataManager {

    private final RexWelcome plugin;
    private File dataFile;
    private FileConfiguration dataConfig;
    private final LinkedHashMap<UUID, PlayerData> playerData; // Maintains insertion order

    public PlayerDataManager(RexWelcome plugin) {
        this.plugin = plugin;
        this.playerData = new LinkedHashMap<>();
        load();
    }

    private void load() {
        dataFile = new File(plugin.getDataFolder(), "playerdata.yml");

        if (!dataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create playerdata.yml: " + e.getMessage());
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        // Load player data
        ConfigurationSection playersSection = dataConfig.getConfigurationSection("players");
        if (playersSection != null) {
            for (String uuidString : playersSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidString);
                    int joinNumber = playersSection.getInt(uuidString + ".join-number");
                    String playerName = playersSection.getString(uuidString + ".name", "Unknown");
                    long firstJoin = playersSection.getLong(uuidString + ".first-join", System.currentTimeMillis());

                    playerData.put(uuid, new PlayerData(joinNumber, playerName, firstJoin));
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in playerdata.yml: " + uuidString);
                }
            }
        }

        plugin.getLogger().info("Loaded " + playerData.size() + " known players from playerdata.yml");
    }

    public void reload() {
        playerData.clear();
        load();
    }

    public void save() {
        if (dataConfig == null || dataFile == null)
            return;

        // Clear existing data
        dataConfig.set("players", null);

        // Save player data
        for (Map.Entry<UUID, PlayerData> entry : playerData.entrySet()) {
            String uuidString = entry.getKey().toString();
            PlayerData data = entry.getValue();

            dataConfig.set("players." + uuidString + ".join-number", data.joinNumber);
            dataConfig.set("players." + uuidString + ".name", data.name);
            dataConfig.set("players." + uuidString + ".first-join", data.firstJoin);
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save playerdata.yml: " + e.getMessage());
        }
    }

    /**
     * Check if a player has joined before
     */
    public boolean hasPlayerJoinedBefore(UUID uuid) {
        return playerData.containsKey(uuid);
    }

    /**
     * Mark a player as having joined and assign them a join number
     */
    public void markPlayerAsJoined(UUID uuid, String playerName) {
        if (!playerData.containsKey(uuid)) {
            int joinNumber = playerData.size() + 1;
            playerData.put(uuid, new PlayerData(joinNumber, playerName, System.currentTimeMillis()));
            save();
        }
    }

    /**
     * Get total number of unique players
     */
    public int getTotalPlayers() {
        return playerData.size();
    }

    /**
     * Get a player's join number (1st, 2nd, 3rd, etc.)
     */
    public int getPlayerNumber(UUID uuid) {
        PlayerData data = playerData.get(uuid);
        return data != null ? data.joinNumber : 0;
    }

    /**
     * Get player name by their join number
     */
    public String getPlayerByNumber(int number) {
        for (Map.Entry<UUID, PlayerData> entry : playerData.entrySet()) {
            if (entry.getValue().joinNumber == number) {
                return entry.getValue().name;
            }
        }
        return null;
    }

    /**
     * Get UUID by join number
     */
    public UUID getPlayerUUIDByNumber(int number) {
        for (Map.Entry<UUID, PlayerData> entry : playerData.entrySet()) {
            if (entry.getValue().joinNumber == number) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Internal class to store player data
     */
    private static class PlayerData {
        final int joinNumber;
        final String name;
        final long firstJoin;

        PlayerData(int joinNumber, String name, long firstJoin) {
            this.joinNumber = joinNumber;
            this.name = name;
            this.firstJoin = firstJoin;
        }
    }
}
