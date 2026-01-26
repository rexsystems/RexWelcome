package cc.rexsystems.rexwelcome.listeners;

import cc.rexsystems.rexwelcome.RexWelcome;
import cc.rexsystems.rexwelcome.config.ConfigManager;
import cc.rexsystems.rexwelcome.data.PlayerDataManager;
import cc.rexsystems.rexwelcome.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;
import java.util.List;

public class PlayerJoinListener implements Listener {

    private final RexWelcome plugin;
    private final ConfigManager configManager;
    private final PlayerDataManager playerDataManager;

    public PlayerJoinListener(RexWelcome plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.playerDataManager = plugin.getPlayerDataManager();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Check if this is a first-time join
        boolean isFirstJoin = !playerDataManager.hasPlayerJoinedBefore(player.getUniqueId());

        if (isFirstJoin) {
            handleFirstJoin(player);
        } else {
            handleReturningPlayer(player);
        }

        // Play sound
        if (configManager.isSoundEnabled()) {
            playJoinSound(player);
        }
    }

    private void handleFirstJoin(Player player) {
        if (!configManager.isFirstJoinEnabled())
            return;

        // Mark player as joined
        playerDataManager.markPlayerAsJoined(player.getUniqueId(), player.getName());
        int totalPlayers = playerDataManager.getTotalPlayers();

        // Clear chat if enabled
        if (configManager.isClearChatEnabled()) {
            clearChat(player);
        }

        // Send title
        if (configManager.isTitleEnabled()) {
            ConfigManager.TitleMessage titleMessage = configManager.getRandomFirstJoinTitle();
            sendTitle(player, titleMessage, totalPlayers);
        }

        // Send messages to player
        List<String> messages = configManager.getFirstJoinMessages();
        sendMessages(player, messages, totalPlayers);

        // Broadcast
        if (configManager.isBroadcastEnabled()) {
            String broadcastMsg = configManager.getBroadcastMessage();
            broadcastMsg = broadcastMsg.replace("%prefix%", configManager.getPrefix());
            broadcastMsg = ColorUtils.replacePlaceholders(broadcastMsg, player, totalPlayers);
            broadcastMsg = ColorUtils.replacePlaceholders(broadcastMsg, player, totalPlayers).replace("%head%", "");
            broadcastMsg = ColorUtils.colorize(broadcastMsg);
            Bukkit.broadcastMessage(broadcastMsg);
        }

        // Execute first join commands
        List<String> commands = configManager.getFirstJoinCommands();
        for (String command : commands) {
            if (command == null || command.trim().isEmpty())
                continue;

            command = command.replace("%player%", player.getName());
            command = command.replace("%joincount%", String.valueOf(totalPlayers));
            command = ColorUtils.replacePlaceholders(command, player, totalPlayers);

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    private void handleReturningPlayer(Player player) {
        if (!configManager.isWelcomeEnabled())
            return;

        int totalPlayers = playerDataManager.getTotalPlayers();

        // Clear chat if enabled
        if (configManager.isClearChatEnabled()) {
            clearChat(player);
        }

        // Send title
        if (configManager.isTitleEnabled()) {
            ConfigManager.TitleMessage titleMessage = configManager.getRandomReturningTitle();
            sendTitle(player, titleMessage, totalPlayers);
        }

        // Send messages to player
        List<String> messages = configManager.getReturningMessages();
        sendMessages(player, messages, totalPlayers);
    }

    private void clearChat(Player player) {
        for (int i = 0; i < 100; i++) {
            player.sendMessage("");
        }
    }

    private void sendTitle(Player player, ConfigManager.TitleMessage titleMessage, int totalPlayers) {
        // Construct Title Component
        Component title = createComponent(titleMessage.getTitle(), player, totalPlayers);
        Component subtitle = createComponent(titleMessage.getSubtitle(), player, totalPlayers);

        Title.Times times = Title.Times.times(
                Duration.ofMillis(configManager.getTitleFadeIn() * 50L), // Ticks to millis (1 tick = 50ms)
                Duration.ofMillis(configManager.getTitleStay() * 50L),
                Duration.ofMillis(configManager.getTitleFadeOut() * 50L));

        Title adventureTitle = Title.title(title, subtitle, times);
        player.showTitle(adventureTitle);
    }

    private void sendMessages(Player player, List<String> messages, int totalPlayers) {
        for (String message : messages) {
            Component component = createComponent(message, player, totalPlayers);
            player.sendMessage(component);
        }
    }

    private Component createComponent(String text, Player player, int totalPlayers) {
        // 1. Replace Prefix
        String processed = text.replace("%prefix%", configManager.getPrefix());

        // 2. Replace Placeholders (Player, Online, etc.)
        processed = ColorUtils.replacePlaceholders(processed, player, totalPlayers);

        // 3. Colorize Legacy (& -> §) including Hex
        processed = ColorUtils.colorize(processed);

        // 4. Return Component (Standard Legacy Deserialization)
        // This handles standard color codes (§a, §x...) natively supported by Adventure
        return LegacyComponentSerializer.legacySection().deserialize(processed);
    }

    private void playJoinSound(Player player) {
        try {
            Sound sound = Sound.valueOf(configManager.getJoinSound());
            player.playSound(
                    player.getLocation(),
                    sound,
                    configManager.getSoundVolume(),
                    configManager.getSoundPitch());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid sound: " + configManager.getJoinSound());
        }
    }
}
