package carnage.cAbilityStones.listeners;

import carnage.cAbilityStones.CAbilityStones;
import carnage.cAbilityStones.models.StoneType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Listens for player interactions with ability stones and triggers their abilities.
 */
public class StoneInteractListener implements Listener {
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("&([0-9a-fA-Fk-oK-O])");

    private final CAbilityStones plugin;

    public StoneInteractListener(CAbilityStones plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles player interaction events to activate ability stones on right-click.
     *
     * @param event the player interact event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isRightClick(event.getAction())) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        if (!plugin.getStoneManager().isAbilityStone(item)) {
            return;
        }

        StoneType stoneType = plugin.getStoneManager().getStoneType(item);
        event.setCancelled(true);
        handleStoneActivation(player, stoneType);
    }

    /**
     * Checks if the action is a right-click.
     *
     * @param action the action to check
     * @return true if the action is a right-click
     */
    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }

    /**
     * Handles the activation of an ability stone, including cooldown checks.
     *
     * @param player the player using the stone
     * @param stoneType the type of stone
     */
    private void handleStoneActivation(Player player, StoneType stoneType) {
        if (plugin.getCooldownManager().hasCooldown(player.getUniqueId(), stoneType)) {
            long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), stoneType);
            double seconds = remaining / 1000.0;
            String message = String.format("Ability on cooldown! %.1fs remaining", seconds);
            sendMessage(player, translateLegacyColorCodes("&c" + message));
            return;
        }

        boolean success = plugin.getAbilityManager().activateAbility(player, stoneType);
        if (success) {
            long cooldown = plugin.getAbilityManager().getAbility(stoneType).getCooldown();
            plugin.getCooldownManager().setCooldown(player.getUniqueId(), stoneType, cooldown);
        }
    }

    /**
     * Translates legacy color codes (&x) to Adventure Component with NamedTextColor.
     *
     * @param text the text with legacy color codes
     * @return the formatted Component
     */
    private Component translateLegacyColorCodes(String text) {
        if (text == null) {
            return Component.empty();
        }

        StringBuilder builder = new StringBuilder();
        Matcher matcher = COLOR_CODE_PATTERN.matcher(text);
        int lastEnd = 0;
        NamedTextColor currentColor = NamedTextColor.WHITE;
        Component result = Component.empty();

        while (matcher.find()) {
            builder.append(text, lastEnd, matcher.start());
            if (!builder.isEmpty()) {
                result = result.append(Component.text(builder.toString(), currentColor));
                builder.setLength(0);
            }

            char code = matcher.group(1).toLowerCase().charAt(0);
            currentColor = getColorFromCode(code);
            lastEnd = matcher.end();
        }

        builder.append(text.substring(lastEnd));
        if (!builder.isEmpty()) {
            result = result.append(Component.text(builder.toString(), currentColor));
        }

        return result;
    }

    /**
     * Maps legacy color codes to NamedTextColor.
     *
     * @param code the color code character
     * @return the corresponding NamedTextColor
     */
    private NamedTextColor getColorFromCode(char code) {
        return switch (code) {
            case '0' -> NamedTextColor.BLACK;
            case '1' -> NamedTextColor.DARK_BLUE;
            case '2' -> NamedTextColor.DARK_GREEN;
            case '3' -> NamedTextColor.DARK_AQUA;
            case '4' -> NamedTextColor.DARK_RED;
            case '5' -> NamedTextColor.DARK_PURPLE;
            case '6' -> NamedTextColor.GOLD;
            case '7' -> NamedTextColor.GRAY;
            case '8' -> NamedTextColor.DARK_GRAY;
            case '9' -> NamedTextColor.BLUE;
            case 'a' -> NamedTextColor.GREEN;
            case 'b' -> NamedTextColor.AQUA;
            case 'c' -> NamedTextColor.RED;
            case 'd' -> NamedTextColor.LIGHT_PURPLE;
            case 'e' -> NamedTextColor.YELLOW;
            case 'f' -> NamedTextColor.WHITE;
            default -> NamedTextColor.WHITE;
        };
    }

    /**
     * Sends a message to the player.
     *
     * @param player the player to receive the message
     * @param message the message to send
     */
    private void sendMessage(Player player, Component message) {
        player.sendMessage(message);
    }
}