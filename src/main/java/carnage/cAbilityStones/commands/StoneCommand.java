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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handles the /stone command for giving ability stones and reloading configuration.
 */
public class StoneCommand implements CommandExecutor, TabCompleter {
    private static final String PERMISSION_GIVE = "abilitystones.give";
    private static final String PERMISSION_RELOAD = "abilitystones.reload";
    
    // Pre-computed stone type names for tab completion
    private static final List<String> STONE_TYPE_NAMES = Arrays.stream(StoneType.values())
            .map(type -> type.name().toLowerCase(Locale.ROOT))
            .collect(Collectors.toList());
    
    private static final List<String> SUBCOMMANDS = Arrays.asList("give", "reload");

    private final CAbilityStones plugin;

    public StoneCommand(CAbilityStones plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, 
                           @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            displayHelp(sender);
            return true;
        }

        String subcommand = args[0].toLowerCase(Locale.ROOT);
        
        switch (subcommand) {
            case "reload":
                return handleReload(sender);
            case "give":
                return handleGiveCommand(sender, args);
            default:
                sendMessage(sender, Component.text("Unknown subcommand! Use /stone for help", NamedTextColor.RED));
                return true;
        }
    }

    /**
     * Displays the help message for the /stone command.
     *
     * @param sender the command sender
     */
    private void displayHelp(@NotNull CommandSender sender) {
        sender.sendMessage(Component.text("=== Ability Stones ===", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/stone give <type> [player] - Give a stone", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("/stone reload - Reload config", NamedTextColor.YELLOW));
        sender.sendMessage(Component.text("Types: " + String.join(", ", STONE_TYPE_NAMES), NamedTextColor.YELLOW));
    }

    /**
     * Handles the reload subcommand.
     *
     * @param sender the command sender
     * @return true if the command was handled successfully
     */
    private boolean handleReload(@NotNull CommandSender sender) {
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
    private boolean handleGiveCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission(PERMISSION_GIVE)) {
            sendMessage(sender, Component.text("No permission!", NamedTextColor.RED));
            return true;
        }

        if (args.length < 2) {
            sendMessage(sender, Component.text("Usage: /stone give <type> [player]", NamedTextColor.RED));
            return true;
        }

        StoneType type = parseStoneType(args[1]);
        if (type == null) {
            sendMessage(sender, Component.text(
                "Invalid stone type! Use: " + String.join(", ", STONE_TYPE_NAMES), 
                NamedTextColor.RED
            ));
            return true;
        }

        Player target = resolveTarget(sender, args);
        if (target == null) {
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
    @Nullable
    private Player resolveTarget(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length >= 3) {
            Player target = plugin.getServer().getPlayer(args[2]);
            if (target == null) {
                sendMessage(sender, Component.text("Player not found!", NamedTextColor.RED));
            }
            return target;
        }

        if (sender instanceof Player) {
            return (Player) sender;
        }
        
        sendMessage(sender, Component.text("You must specify a player!", NamedTextColor.RED));
        return null;
    }

    /**
     * Parses the stone type from the command argument.
     *
     * @param typeStr the string representation of the stone type
     * @return the StoneType, or null if invalid
     */
    @Nullable
    private StoneType parseStoneType(@NotNull String typeStr) {
        try {
            return StoneType.valueOf(typeStr.toUpperCase(Locale.ROOT));
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
    private void giveStone(@NotNull Player target, @NotNull StoneType type, @NotNull CommandSender sender) {
        ItemStack stone = plugin.getStoneManager().createStone(type);
        target.getInventory().addItem(stone);
        
        String typeName = type.name().toLowerCase(Locale.ROOT);
        sendMessage(target, Component.text("You received a " + typeName + " Stone!", NamedTextColor.GREEN));
        
        if (!target.equals(sender)) {
            sendMessage(sender, Component.text(
                "Gave " + target.getName() + " a " + typeName + " Stone!", 
                NamedTextColor.GREEN
            ));
        }
    }

    /**
     * Sends a message to the command sender.
     *
     * @param sender the command sender
     * @param message the message to send
     */
    private void sendMessage(@NotNull CommandSender sender, @NotNull Component message) {
        sender.sendMessage(message);
    }

    @Override
    @NotNull
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, 
                                     @NotNull String alias, @NotNull String[] args) {
        // No permission check - let players see available commands
        
        if (args.length == 1) {
            return filterCompletions(SUBCOMMANDS, args[0]);
        }
        
        if (args[0].equalsIgnoreCase("give")) {
            if (args.length == 2) {
                return filterCompletions(STONE_TYPE_NAMES, args[1]);
            }
            if (args.length == 3) {
                return filterCompletions(
                    plugin.getServer().getOnlinePlayers().stream()
                        .map(Player::getName)
                        .collect(Collectors.toList()),
                    args[2]
                );
            }
        }

        return Collections.emptyList();
    }

    /**
     * Filters completions based on the current argument.
     *
     * @param options the available options
     * @param arg the current argument being typed
     * @return filtered list of completions
     */
    @NotNull
    private List<String> filterCompletions(@NotNull List<String> options, @NotNull String arg) {
        String lowerArg = arg.toLowerCase(Locale.ROOT);
        return options.stream()
                .filter(option -> option.toLowerCase(Locale.ROOT).startsWith(lowerArg))
                .collect(Collectors.toList());
    }
}
