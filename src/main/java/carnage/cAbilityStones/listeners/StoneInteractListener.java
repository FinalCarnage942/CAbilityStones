package carnage.cAbilityStones.listeners;

import carnage.cAbilityStones.CAbilityStones;
import carnage.cAbilityStones.models.StoneType;
import carnage.cAbilityStones.utils.ColorTranslator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class StoneInteractListener implements Listener {
    private final CAbilityStones plugin;

    public StoneInteractListener(CAbilityStones plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

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

    private boolean isRightClick(Action action) {
        return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
    }

    private void handleStoneActivation(Player player, StoneType stoneType) {
        long remaining = plugin.getCooldownManager().getRemainingCooldown(player.getUniqueId(), stoneType);
        if (remaining > 0) {
            double seconds = remaining / 1000.0;
            String message = String.format("Ability on cooldown! %.1fs remaining", seconds);
            player.sendMessage(ColorTranslator.translate("&c" + message));
            return;
        }

        boolean success = plugin.getAbilityManager().activateAbility(player, stoneType);
        if (success) {
            long cooldown = plugin.getAbilityManager().getAbility(stoneType).getCooldown();
            plugin.getCooldownManager().setCooldown(player.getUniqueId(), stoneType, cooldown);
        }
    }

}