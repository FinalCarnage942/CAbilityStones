package carnage.cAbilityStones.abilities;

import carnage.cAbilityStones.CAbilityStones;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ChainLightningAbility implements Ability {
    private static final double RANGE = 6.0;
    private static final long DEFAULT_COOLDOWN = 12_000L;
    private static final double DAMAGE = 6.0;

    private final CAbilityStones plugin;

    public ChainLightningAbility(CAbilityStones plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean activate(Player player) {
        List<LivingEntity> targets = findNearbyEnemies(player);
        if (targets.isEmpty()) {
            player.sendMessage(Component.text("No enemies nearby!", NamedTextColor.RED));
            return false;
        }

        applyLightningEffect(player, targets);
        player.sendMessage(Component.text("Chain Lightning struck " + targets.size() + " enemies!", NamedTextColor.YELLOW));
        return true;
    }

    private List<LivingEntity> findNearbyEnemies(Player player) {
        return player.getNearbyEntities(RANGE, RANGE, RANGE).stream()
                .filter(entity -> entity instanceof LivingEntity)
                .filter(entity -> !(entity instanceof Player && entity.equals(player)))
                .map(entity -> (LivingEntity) entity)
                .sorted(Comparator.comparingDouble(e -> e.getLocation().distanceSquared(player.getLocation())))
                .limit(3)
                .collect(Collectors.toList());
    }

    private void applyLightningEffect(Player player, List<LivingEntity> targets) {
        for (LivingEntity enemy : targets) {
            enemy.getWorld().strikeLightningEffect(enemy.getLocation());
            enemy.damage(DAMAGE, player);
        }
    }

    @Override
    public String getName() {
        return plugin.getConfig().getString("stones.lightning.ability_name", "Chain Lightning");
    }

    @Override
    public long getCooldown() {
        return plugin.getConfig().getLong("stones.lightning.cooldown", DEFAULT_COOLDOWN / 1000) * 1000;
    }
}