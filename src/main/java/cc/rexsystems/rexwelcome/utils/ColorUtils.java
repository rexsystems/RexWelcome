package cc.rexsystems.rexwelcome.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {

    // Patterns for different color formats
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final Pattern HEX_PATTERN_PLAIN = Pattern.compile("#([A-Fa-f0-9]{6})");
    private static final Pattern MINIMESSAGE_COLOR = Pattern.compile("<color:([^>]+)>");
    private static final Pattern MINIMESSAGE_HEX = Pattern.compile("<#([A-Fa-f0-9]{6})>");
    private static final Pattern MINIMESSAGE_GRADIENT = Pattern.compile("<gradient:([^>]+)>");
    private static final Pattern MINIMESSAGE_RAINBOW = Pattern.compile("<rainbow>");
    private static final Pattern MINIMESSAGE_TAGS = Pattern.compile("<(/?)([a-zA-Z_]+)(?::[^>]+)?>");

    /**
     * Colorize a string with support for all color formats:
     * - Legacy: &a, &l, &o, etc.
     * - Hex: &#RRGGBB and #RRGGBB
     * - MiniMessage: <color:#RRGGBB>, <gradient:...>, <rainbow>, etc.
     */
    public static String colorize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // Process MiniMessage formats first
        text = processMiniMessage(text);

        // Process hex colors &#RRGGBB
        text = processHexColors(text);

        // Process plain hex colors #RRGGBB
        text = processPlainHexColors(text);

        // Process legacy color codes
        text = ChatColor.translateAlternateColorCodes('&', text);

        return text;
    }

    /**
     * Colorize with placeholder support
     */
    public static String colorize(String text, Player player) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // Replace placeholders
        text = replacePlaceholders(text, player);

        return colorize(text);
    }

    /**
     * Replace placeholders in text
     */
    public static String replacePlaceholders(String text, Player player) {
        if (text == null)
            return null;

        text = text.replace("%player%", player.getName());
        text = text.replace("%displayname%", player.getDisplayName());
        text = text.replace("%online%", String.valueOf(Bukkit.getOnlinePlayers().size()));
        text = text.replace("%max_players%", String.valueOf(Bukkit.getMaxPlayers()));

        return text;
    }

    /**
     * Replace placeholders with custom values
     */
    public static String replacePlaceholders(String text, Player player, int totalPlayers) {
        text = replacePlaceholders(text, player);
        text = text.replace("%total_players%", String.valueOf(totalPlayers));
        text = text.replace("%joincount%", String.valueOf(totalPlayers)); // Alias for %total_players%
        return text;
    }

    /**
     * Process &#RRGGBB format
     */
    private static String processHexColors(String text) {
        Matcher matcher = HEX_PATTERN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);
            String replacement = hexToMinecraftColor(hex);
            matcher.appendReplacement(buffer, replacement);
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }

    /**
     * Process #RRGGBB format (plain hex)
     */
    private static String processPlainHexColors(String text) {
        Matcher matcher = HEX_PATTERN_PLAIN.matcher(text);
        StringBuffer buffer = new StringBuffer();

        while (matcher.find()) {
            String hex = matcher.group(1);
            String replacement = hexToMinecraftColor(hex);
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(buffer);

        return buffer.toString();
    }

    /**
     * Process basic MiniMessage formats
     */
    private static String processMiniMessage(String text) {
        // Process <color:#RRGGBB> and <color:name>
        Matcher colorMatcher = MINIMESSAGE_COLOR.matcher(text);
        StringBuffer buffer = new StringBuffer();
        while (colorMatcher.find()) {
            String colorValue = colorMatcher.group(1);
            String replacement;
            if (colorValue.startsWith("#")) {
                replacement = hexToMinecraftColor(colorValue.substring(1));
            } else {
                replacement = namedColorToCode(colorValue);
            }
            colorMatcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        colorMatcher.appendTail(buffer);
        text = buffer.toString();

        // Process <#RRGGBB>
        Matcher hexMatcher = MINIMESSAGE_HEX.matcher(text);
        buffer = new StringBuffer();
        while (hexMatcher.find()) {
            String hex = hexMatcher.group(1);
            String replacement = hexToMinecraftColor(hex);
            hexMatcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        hexMatcher.appendTail(buffer);
        text = buffer.toString();

        // Process gradients (simplified - just use first color)
        Matcher gradientMatcher = MINIMESSAGE_GRADIENT.matcher(text);
        buffer = new StringBuffer();
        while (gradientMatcher.find()) {
            String colors = gradientMatcher.group(1);
            String[] colorArray = colors.split(":");
            if (colorArray.length > 0) {
                String firstColor = colorArray[0];
                String replacement;
                if (firstColor.startsWith("#")) {
                    replacement = hexToMinecraftColor(firstColor.substring(1));
                } else {
                    replacement = namedColorToCode(firstColor);
                }
                gradientMatcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
            }
        }
        gradientMatcher.appendTail(buffer);
        text = buffer.toString();

        // Process rainbow (use red as fallback)
        text = MINIMESSAGE_RAINBOW.matcher(text).replaceAll(ChatColor.RED.toString());

        // Process other MiniMessage tags
        Matcher tagMatcher = MINIMESSAGE_TAGS.matcher(text);
        buffer = new StringBuffer();
        while (tagMatcher.find()) {
            String closing = tagMatcher.group(1);
            String tagName = tagMatcher.group(2).toLowerCase();
            String replacement = "";

            if (!closing.isEmpty()) {
                replacement = ChatColor.RESET.toString();
            } else {
                switch (tagName) {
                    case "bold":
                        replacement = ChatColor.BOLD.toString();
                        break;
                    case "italic":
                        replacement = ChatColor.ITALIC.toString();
                        break;
                    case "underlined":
                        replacement = ChatColor.UNDERLINE.toString();
                        break;
                    case "strikethrough":
                        replacement = ChatColor.STRIKETHROUGH.toString();
                        break;
                    case "obfuscated":
                        replacement = ChatColor.MAGIC.toString();
                        break;
                    case "reset":
                        replacement = ChatColor.RESET.toString();
                        break;
                    default:
                        replacement = "";
                }
            }
            tagMatcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement));
        }
        tagMatcher.appendTail(buffer);
        text = buffer.toString();

        return text;
    }

    /**
     * Convert hex color to Minecraft color code format
     */
    private static String hexToMinecraftColor(String hex) {
        try {
            return ChatColor.of("#" + hex).toString();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Convert named color to Minecraft color code
     */
    private static String namedColorToCode(String name) {
        switch (name.toLowerCase()) {
            case "black":
                return ChatColor.BLACK.toString();
            case "dark_blue":
                return ChatColor.DARK_BLUE.toString();
            case "dark_green":
                return ChatColor.DARK_GREEN.toString();
            case "dark_aqua":
                return ChatColor.DARK_AQUA.toString();
            case "dark_red":
                return ChatColor.DARK_RED.toString();
            case "dark_purple":
                return ChatColor.DARK_PURPLE.toString();
            case "gold":
                return ChatColor.GOLD.toString();
            case "gray":
                return ChatColor.GRAY.toString();
            case "dark_gray":
                return ChatColor.DARK_GRAY.toString();
            case "blue":
                return ChatColor.BLUE.toString();
            case "green":
                return ChatColor.GREEN.toString();
            case "aqua":
                return ChatColor.AQUA.toString();
            case "red":
                return ChatColor.RED.toString();
            case "light_purple":
                return ChatColor.LIGHT_PURPLE.toString();
            case "yellow":
                return ChatColor.YELLOW.toString();
            case "white":
                return ChatColor.WHITE.toString();
            default:
                return ChatColor.WHITE.toString();
        }
    }

    /**
     * Convert legacy color codes (&a, &#hex) to MiniMessage format (<green>,
     * <#hex>)
     */
    public static String legacyToMiniMessage(String text) {
        if (text == null)
            return null;

        // 1. Handle Hex Colors: &#RRGGBB -> <#RRGGBB>
        text = text.replaceAll("&#([0-9a-fA-F]{6})", "<#$1>");
        // Handle Plain Hex: #RRGGBB -> <#RRGGBB> (if not preceded by <)
        text = text.replaceAll("(?<!<)#([0-9a-fA-F]{6})", "<#$1>");

        // 2. Handle Legacy Colors
        StringBuilder sb = new StringBuilder();
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '&' || c == '§') {
                if (i + 1 < chars.length) {
                    char code = chars[i + 1];
                    String replacement = getMiniMessageTag(code);
                    if (replacement != null) {
                        sb.append(replacement);
                        i++; // Skip code char
                        continue;
                    }
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }

    private static String getMiniMessageTag(char code) {
        switch (Character.toLowerCase(code)) {
            case '0':
                return "<black>";
            case '1':
                return "<dark_blue>";
            case '2':
                return "<dark_green>";
            case '3':
                return "<dark_aqua>";
            case '4':
                return "<dark_red>";
            case '5':
                return "<dark_purple>";
            case '6':
                return "<gold>";
            case '7':
                return "<gray>";
            case '8':
                return "<dark_gray>";
            case '9':
                return "<blue>";
            case 'a':
                return "<green>";
            case 'b':
                return "<aqua>";
            case 'c':
                return "<red>";
            case 'd':
                return "<light_purple>";
            case 'e':
                return "<yellow>";
            case 'f':
                return "<white>";
            case 'k':
                return "<obfuscated>";
            case 'l':
                return "<bold>";
            case 'm':
                return "<strikethrough>";
            case 'n':
                return "<underlined>";
            case 'o':
                return "<italic>";
            case 'r':
                return "<reset>";
            default:
                return null;
        }
    }

    /**
     * Strip all color codes from a string
     */
    public static String stripColors(String text) {
        if (text == null)
            return null;
        return ChatColor.stripColor(colorize(text));
    }
}
