package carnage.cAbilityStones.abilities;

import carnage.cAbilityStones.CAbilityStones;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Implements the Heal Wave ability, healing the player and nearby allies with particle effects.
 */
public class HealWaveAbility implements Ability {
    private static final double HEAL_AMOUNT = 4.0;
    private static final double RANGE = 5.0;
    private static final int REGEN_DURATION = 100;
    private static final int REGEN_AMPLIFIER = 1;
    private static final int PARTICLE_TICKS = 30;
    private static final double MAX_RADIUS = 6.0;
    private static final long DEFAULT_COOLDOWN = 12_000L;

    private final CAbilityStones plugin;

    public HealWaveAbility(CAbilityStones plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean activate(Player player) {
        healPlayer(player);
        healNearbyAllies(player);
        startParticleEffect(player);
        sendMessage(player, Component.text("Heal Wave activated!", NamedTextColor.AQUA));
        return true;
    }

    /**
     * Heals the activating player.
     *
     * @param player the player to heal
     */
    private void healPlayer(Player player) {
        double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
        double newHealth = Math.min(player.getHealth() + HEAL_AMOUNT, maxHealth);
        player.setHealth(newHealth);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, REGEN_DURATION, REGEN_AMPLIFIER));
    }

    /**
     * Heals nearby allied players.
     *
     * @param player the activating player
     */
    private void healNearbyAllies(Player player) {
        player.getNearbyEntities(RANGE, RANGE, RANGE).stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .forEach(ally -> {
                    double allyMaxHealth = ally.getAttribute(Attribute.MAX_HEALTH).getValue();
                    double allyNewHealth = Math.min(ally.getHealth() + HEAL_AMOUNT, allyMaxHealth);
                    ally.setHealth(allyNewHealth);
                    ally.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, REGEN_DURATION, REGEN_AMPLIFIER));
                    sendMessage(ally, Component.text(player.getName() + " healed you!", NamedTextColor.AQUA));
                });
    }

    /**
     * Starts the particle effect for the heal wave.
     *
     * @param player the player to show particles for
     */
    private void startParticleEffect(Player player) {
        new BukkitRunnable() {
            double radius = 0;
            int ticks = 0;

            @Override
            public void run() {
                if (radius > MAX_RADIUS || ticks >= PARTICLE_TICKS) {
                    cancel();
                    return;
                }

                Location center = player.getLocation().clone().add(0, 0.1, 0);
                spawnHealParticles(center, radius);
                radius += 0.2;
                ticks++;
            }

            private void spawnHealParticles(Location center, double radius) {
                for (double angle = 0; angle < 360; angle += 5) {
                    double radians = Math.toRadians(angle);
                    double x = center.getX() + Math.cos(radians) * radius;
                    double z = center.getZ() + Math.sin(radians) * radius;
                    Location particleLoc = new Location(center.getWorld(), x, center.getY(), z);

                    Particle.DustOptions blueOptions = new Particle.DustOptions(Color.fromRGB(0, 191, 255), 1.5f);
                    player.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0, 0, 0, 0, blueOptions);

                    Particle.DustOptions cyanOptions = new Particle.DustOptions(Color.fromRGB(0, 255, 255), 1.2f);
                    player.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0, 0.05, 0, 0, cyanOptions);

                    player.getWorld().spawnParticle(Particle.BUBBLE, particleLoc, 1, 0, 0.1, 0, 0);
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
        return plugin.getConfig().getString("stones.water.ability_name", "Heal Wave");
    }

    @Override
    public long getCooldown() {
        return plugin.getConfig().getLong("stones.water.cooldown", DEFAULT_COOLDOWN / 1000) * 1000;
    }
}