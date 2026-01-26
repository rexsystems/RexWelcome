package cc.rexsystems.rexwelcome.commands;

import cc.rexsystems.rexwelcome.RexWelcome;
import cc.rexsystems.rexwelcome.config.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RexWelcomeCommand implements CommandExecutor, TabCompleter {

    private final RexWelcome plugin;
    private final ConfigManager configManager;

    public RexWelcomeCommand(RexWelcome plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender, label);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                handleReload(sender);
                break;
            case "help":
                sendHelp(sender, label);
                break;
            case "info":
                sendInfo(sender);
                break;
            default:
                sender.sendMessage(configManager.getMessage("command-not-found"));
                break;
        }

        return true;
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("rexwelcome.reload")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return;
        }

        plugin.reload();
        sender.sendMessage(configManager.getMessage("reload-success"));
    }

    private void sendHelp(CommandSender sender, String label) {
        if (!sender.hasPermission("rexwelcome.help")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return;
        }

        sender.sendMessage("");
        sender.sendMessage("§7§m                    §r §6§lRexWelcome §7§m                    ");
        sender.sendMessage("");
        sender.sendMessage("  §e/" + label + " reload §7- Reload the configuration");
        sender.sendMessage("  §e/" + label + " info §7- Show plugin information");
        sender.sendMessage("  §e/" + label + " help §7- Show this help message");
        sender.sendMessage("");
        sender.sendMessage("§7§m                                                          ");
    }

    private void sendInfo(CommandSender sender) {
        if (!sender.hasPermission("rexwelcome.info")) {
            sender.sendMessage(configManager.getMessage("no-permission"));
            return;
        }

        sender.sendMessage("");
        sender.sendMessage("§7§m                    §r §6§lRexWelcome §7§m                    ");
        sender.sendMessage("");
        sender.sendMessage("  §7Version: §f" + plugin.getDescription().getVersion());
        sender.sendMessage("  §7Author: §fRexSystems");
        sender.sendMessage("  §7Website: §fhttps://rexsystems.cc");
        sender.sendMessage("");
        sender.sendMessage("  §7Total unique players: §e" + plugin.getPlayerDataManager().getTotalPlayers());

        sender.sendMessage("");
        sender.sendMessage("§7§m                                                          ");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("reload");
            completions.add("info");
            completions.add("help");

            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
