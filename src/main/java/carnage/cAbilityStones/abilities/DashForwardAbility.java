package carnage.cAbilityStones.abilities;

import carnage.cAbilityStones.CAbilityStones;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class DashForwardAbility implements Ability {
    private static final double DASH_MULTIPLIER = 2.0;
    private static final double Y_OFFSET = 0.3;
    private static final int SPEED_DURATION = 40;
    private static final int SPEED_AMPLIFIER = 1;
    private static final int PARTICLE_TICKS = 40;
    private static final long DEFAULT_COOLDOWN = 8_000L;

    private final CAbilityStones plugin;

    public DashForwardAbility(CAbilityStones plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean activate(Player player) {
        applyDash(player);
        applySpeedEffect(player);
        startParticleEffect(player);
        player.sendMessage(Component.text("Dash Forward activated!", NamedTextColor.WHITE));
        return true;
    }

    private void applyDash(Player player) {
        Vector direction = player.getLocation().getDirection().normalize();
        direction.setY(Y_OFFSET);
        player.setVelocity(direction.multiply(DASH_MULTIPLIER));
    }

    private void applySpeedEffect(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, SPEED_DURATION, SPEED_AMPLIFIER));
    }

    private void startParticleEffect(Player player) {
        double[] angle = {0};
        int[] ticks = {0};

        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (ticks[0] >= PARTICLE_TICKS) {
                task.cancel();
                return;
            }

            Location loc = player.getLocation().clone().add(0, 1, 0);
            for (int i = 0; i < 3; i++) {
                double offsetAngle = angle[0] + (i * 120);
                double rad = Math.toRadians(offsetAngle);
                Location slashLoc = loc.clone().add(Math.cos(rad) * 0.8, 0, Math.sin(rad) * 0.8);
                player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, slashLoc, 1, 0, 0, 0, 0);
                player.getWorld().spawnParticle(Particle.CRIT, slashLoc, 2, 0.1, 0.1, 0.1, 0);
            }

            angle[0] += 30;
            ticks[0]++;
        }, 0, 1);
    }

    @Override
    public String getName() {
        return plugin.getConfig().getString("stones.air.ability_name", "Dash Forward");
    }

    @Override
    public long getCooldown() {
        return plugin.getConfig().getLong("stones.air.cooldown", DEFAULT_COOLDOWN / 1000) * 1000;
    }
}