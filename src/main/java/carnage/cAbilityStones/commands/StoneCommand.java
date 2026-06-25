package carnage.cAbilityStones.commands;

import carnage.cAbilityStones.CAbilityStones;
import carnage.cAbilityStones.models.StoneType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StoneCommand implements CommandExecutor, TabCompleter {
    private static final String PERMISSION_GIVE = "abilitystones.give";
    private static final String PERMISSION_RELOAD = "abilitystones.reload";

    private final CAbilityStones plugin;

    public StoneCommand(CAbilityStones plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            displayHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            return handleReload(sender);
        }

        if (args[0].equalsIgnoreCase("give")) {
            return handleGiveCommand(sender, args);
        }

        sender.sendMessage(Component.text("Unknown subcommand! Use /stone for help", NamedTextColor.RED));
        return true;
    }

    private void displayHelp(CommandSender sender) {
        sender.sendMessage(Component.text("=== Ability Stones ===", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/stone give <type> [player] - Give a stone", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/stone reload - Reload config", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("Types: fire, water, earth, air, lightning, darkness", NamedTextColor.YELLOW));
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission(PERMISSION_RELOAD)) {
            sender.sendMessage(Component.text("No permission!", NamedTextColor.RED));
            return true;
        }

        plugin.reloadConfig();
        sender.sendMessage(Component.text("Config reloaded!", NamedTextColor.GREEN));
        return true;
    }

    private boolean handleGiveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION_GIVE)) {
            sender.sendMessage(Component.text("No permission!", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /stone give <type> [player]", NamedTextColor.RED));
            return true;
        }

        Player target = resolveTarget(sender, args);
        if (target == null) {
            return true;
        }

        StoneType type = parseStoneType(args[1]);
        if (type == null) {
            sender.sendMessage(Component.text("Invalid stone type! Use: fire, water, earth, air, lightning, darkness", NamedTextColor.RED));
            return true;
        }

        giveStone(target, type, sender);
        return true;
    }

    private Player resolveTarget(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            Player target = plugin.getServer().getPlayer(args[2]);
            if (target == null) {
                sender.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
                return null;
            }
            return target;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("You must specify a player!", NamedTextColor.RED));
            return null;
        }
        return (Player) sender;
    }

    private StoneType parseStoneType(String typeStr) {
        try {
            return StoneType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private void giveStone(Player target, StoneType type, CommandSender sender) {
        ItemStack stone = plugin.getStoneManager().createStone(type);
        target.getInventory().addItem(stone);
        target.sendMessage(Component.text("You received a " + type.name() + " Stone!", NamedTextColor.GREEN));
        if (!target.equals(sender)) {
            sender.sendMessage(Component.text("Gave " + target.getName() + " a " + type.name() + " Stone!", NamedTextColor.GREEN));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("give");
            completions.add("reload");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            return Arrays.stream(StoneType.values())
                    .map(type -> type.name().toLowerCase())
                    .collect(Collectors.toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }

        return completions.stream()
                .filter(s -> s.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .collect(Collectors.toList());
    }
}