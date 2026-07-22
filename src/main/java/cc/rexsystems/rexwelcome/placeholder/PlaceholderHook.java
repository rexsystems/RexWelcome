package cc.rexsystems.rexwelcome.placeholder;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;

/**
 * Isolated wrapper around PlaceholderAPI.
 * <p>
 * This class references PlaceholderAPI classes directly. Because the JVM only
 * loads/links a class the first time it is used, this class must ONLY be
 * touched when PlaceholderAPI is confirmed to be installed (see
 * {@code RexWelcome#isPlaceholderAPIEnabled()}). Keeping the reference isolated
 * here prevents a {@link NoClassDefFoundError} on servers without PAPI.
 */
public final class PlaceholderHook {

    private PlaceholderHook() {
    }

    /**
     * Resolve any PlaceholderAPI placeholders in the given text.
     *
     * @param player the player context (may be null for server placeholders)
     * @param text   the text to process
     * @return the text with PAPI placeholders resolved
     */
    public static String apply(OfflinePlayer player, String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
