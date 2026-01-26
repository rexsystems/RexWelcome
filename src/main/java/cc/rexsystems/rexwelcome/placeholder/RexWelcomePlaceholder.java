package cc.rexsystems.rexwelcome.placeholder;

import cc.rexsystems.rexwelcome.RexWelcome;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class RexWelcomePlaceholder extends PlaceholderExpansion {

    private final RexWelcome plugin;

    public RexWelcomePlaceholder(RexWelcome plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "rexwelcome";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "RexSystems";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true; // Keep expansion registered across reloads
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        // %rexwelcome_playernumber%
        if (params.equalsIgnoreCase("playernumber")) {
            if (player == null)
                return "0";
            int number = plugin.getPlayerDataManager().getPlayerNumber(player.getUniqueId());
            return String.valueOf(number);
        }

        // %rexwelcome_totalplayers%
        if (params.equalsIgnoreCase("totalplayers")) {
            return String.valueOf(plugin.getPlayerDataManager().getTotalPlayers());
        }

        // %rexwelcome_player_<N>% - Get name of Nth player
        if (params.startsWith("player_")) {
            try {
                int number = Integer.parseInt(params.substring(7));
                String playerName = plugin.getPlayerDataManager().getPlayerByNumber(number);
                return playerName != null ? playerName : "Unknown";
            } catch (NumberFormatException e) {
                return "Invalid";
            }
        }

        return null; // Placeholder not found
    }
}
