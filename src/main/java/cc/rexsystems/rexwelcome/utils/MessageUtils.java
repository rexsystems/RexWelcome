package cc.rexsystems.rexwelcome.utils;

import cc.rexsystems.rexwelcome.RexWelcome;
import cc.rexsystems.rexwelcome.placeholder.PlaceholderHook;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Central message formatting: prefix, internal placeholders, PlaceholderAPI, MiniMessage.
 */
public final class MessageUtils {

    private MessageUtils() {
    }

    public static Component toComponent(RexWelcome plugin, String text, CommandSender sender) {
        Player player = sender instanceof Player ? (Player) sender : null;
        int totalPlayers = plugin.getPlayerDataManager().getTotalPlayers();
        return toComponent(plugin, text, player, totalPlayers);
    }

    public static Component toComponent(RexWelcome plugin, String text, Player player, int totalPlayers) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }

        String processed = text.replace("%prefix%", resolvePrefix(plugin, player, totalPlayers));
        processed = replacePlaceholders(processed, player, totalPlayers);
        processed = applyPlaceholderApi(plugin, player, processed);
        return ColorUtils.toComponent(processed);
    }

    public static String toPlain(RexWelcome plugin, String text, Player player, int totalPlayers) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        String processed = text.replace("%prefix%", resolvePrefix(plugin, player, totalPlayers));
        processed = replacePlaceholders(processed, player, totalPlayers);
        return applyPlaceholderApi(plugin, player, processed);
    }

    private static String resolvePrefix(RexWelcome plugin, Player player, int totalPlayers) {
        String prefix = plugin.getConfigManager().getPrefix();
        prefix = replacePlaceholders(prefix, player, totalPlayers);
        return applyPlaceholderApi(plugin, player, prefix);
    }

    private static String replacePlaceholders(String text, Player player, int totalPlayers) {
        if (text == null) {
            return null;
        }

        if (player != null) {
            text = ColorUtils.replacePlaceholders(text, player, totalPlayers);
        } else {
            text = text.replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
            text = text.replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers()));
            text = text.replace("%total_players%", String.valueOf(totalPlayers));
            text = text.replace("%joincount%", String.valueOf(totalPlayers));
        }

        return text;
    }

    private static String applyPlaceholderApi(RexWelcome plugin, Player player, String text) {
        if (!plugin.isPlaceholderAPIEnabled()) {
            return text;
        }
        return PlaceholderHook.apply(player, text);
    }
}
