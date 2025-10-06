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

/**
 * Handles the /stone command for giving ability stones and reloading configuration.
 */
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

        sendMessage(sender, Component.text("Unknown subcommand! Use /stone for help", NamedTextColor.RED));
        return true;
    }

    /**
     * Displays the help message for the /stone command.
     *
     * @param sender the command sender
     */
    private void displayHelp(CommandSender sender) {
        sendMessage(sender, Component.text("=== Ability Stones ===", NamedTextColor.GOLD));
        sendMessage(sender, Component.text("/stone give <type> [player] - Give a stone", NamedTextColor.YELLOW));
        sendMessage(sender, Component.text("/stone reload - Reload config", NamedTextColor.YELLOW));
        sendMessage(sender, Component.text("Types: fire, water, earth, air, lightning, darkness", NamedTextColor.YELLOW));
    }

    /**
     * Handles the reload subcommand.
     *
     * @param sender the command sender
     * @return true if the command was handled successfully
     */
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission(PERMISSION_RELOAD)) {
            sendMessage(sender, Component.text("No permission!", NamedTextColor.RED));
            return true;
        }

        plugin.reloadConfig();
        sendMessage(sender, Component.text("Config reloaded!", NamedTextColor.GREEN));
        return true;
    }

    /**
     * Handles the give subcommand to provide a stone to a player.
     *
     * @param sender the command sender
     * @param args the command arguments
     * @return true if the command was handled successfully
     */
    private boolean handleGiveCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission(PERMISSION_GIVE)) {
            sendMessage(sender, Component.text("No permission!", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sendMessage(sender, Component.text("Usage: /stone give <type> [player]", NamedTextColor.RED));
            return true;
        }

        Player target = resolveTarget(sender, args);
        if (target == null) {
            return true;
        }

        StoneType type = parseStoneType(args[1]);
        if (type == null) {
            sendMessage(sender, Component.text("Invalid stone type! Use: fire, water, earth, air, lightning, darkness", NamedTextColor.RED));
            return true;
        }

        giveStone(target, type, sender);
        return true;
    }

    /**
     * Resolves the target player for the give command.
     *
     * @param sender the command sender
     * @param args the command arguments
     * @return the target player, or null if invalid
     */
    private Player resolveTarget(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            Player target = plugin.getServer().getPlayer(args[2]);
            if (target == null) {
                sendMessage(sender, Component.text("Player not found!", NamedTextColor.RED));
                return null;
            }
            return target;
        }

        if (!(sender instanceof Player)) {
            sendMessage(sender, Component.text("You must specify a player!", NamedTextColor.RED));
            return null;
        }
        return (Player) sender;
    }

    /**
     * Parses the stone type from the command argument.
     *
     * @param typeStr the string representation of the stone type
     * @return the StoneType, or null if invalid
     */
    private StoneType parseStoneType(String typeStr) {
        try {
            return StoneType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Gives a stone to the target player and sends feedback.
     *
     * @param target the player receiving the stone
     * @param type the type of stone
     * @param sender the command sender
     */
    private void giveStone(Player target, StoneType type, CommandSender sender) {
        ItemStack stone = plugin.getStoneManager().createStone(type);
        target.getInventory().addItem(stone);
        sendMessage(target, Component.text("You received a " + type.name() + " Stone!", NamedTextColor.GREEN));
        if (!target.equals(sender)) {
            sendMessage(sender, Component.text("Gave " + target.getName() + " a " + type.name() + " Stone!", NamedTextColor.GREEN));
        }
    }

    /**
     * Sends a message to the command sender.
     *
     * @param sender the command sender
     * @param message the message to send
     */
    private void sendMessage(CommandSender sender, Component message) {
        sender.sendMessage(message);
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