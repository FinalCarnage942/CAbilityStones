package carnage.cAbilityStones.abilities;

import carnage.cAbilityStones.CAbilityStones;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Implements the Dash Forward ability, propelling the player forward with speed and particle effects.
 */
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
        sendMessage(player, Component.text("Dash Forward activated!", NamedTextColor.WHITE));
        return true;
    }

    /**
     * Applies the dash movement to the player.
     *
     * @param player the player to dash
     */
    private void applyDash(Player player) {
        Vector direction = player.getLocation().getDirection().normalize();
        direction.setY(Y_OFFSET);
        player.setVelocity(direction.multiply(DASH_MULTIPLIER));
    }

    /**
     * Applies a speed potion effect to the player.
     *
     * @param player the player to receive the effect
     */
    private void applySpeedEffect(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, SPEED_DURATION, SPEED_AMPLIFIER));
    }

    /**
     * Starts the particle effect for the dash ability.
     *
     * @param player the player to show particles for
     */
    private void startParticleEffect(Player player) {
        new BukkitRunnable() {
            int ticks = 0;
            double angle = 0;

            @Override
            public void run() {
                if (ticks >= PARTICLE_TICKS) {
                    cancel();
                    return;
                }

                Location loc = player.getLocation().clone().add(0, 1, 0);
                spawnDashParticles(loc, angle);
                angle += 30;
                ticks++;
            }

            private void spawnDashParticles(Location loc, double angle) {
                for (int i = 0; i < 3; i++) {
                    double offsetAngle = angle + (i * 120);
                    double radians = Math.toRadians(offsetAngle);
                    double x = Math.cos(radians) * 0.8;
                    double z = Math.sin(radians) * 0.8;

                    Location slashLoc = loc.clone().add(x, 0, z);
                    player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, slashLoc, 1, 0, 0, 0, 0);
                    player.getWorld().spawnParticle(Particle.CRIT, slashLoc, 2, 0.1, 0.1, 0.1, 0);
                }
            }
        }.runTaskTimer(plugin, 0, 1);
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

    @Override
    public String getName() {
        return plugin.getConfig().getString("stones.air.ability_name", "Dash Forward");
    }

    @Override
    public long getCooldown() {
        return plugin.getConfig().getLong("stones.air.cooldown", DEFAULT_COOLDOWN / 1000) * 1000;
    }
}