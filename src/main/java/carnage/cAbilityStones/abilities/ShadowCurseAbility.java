package carnage.cAbilityStones.abilities;

import carnage.cAbilityStones.CAbilityStones;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Implements the Shadow Curse ability, applying debuffs to nearby enemies with particle effects.
 */
public class ShadowCurseAbility implements Ability {
    private static final double RANGE = 4.0;
    private static final int EFFECT_DURATION = 60;
    private static final int EFFECT_AMPLIFIER = 1;
    private static final int PARTICLE_TICKS = 60;
    private static final long DEFAULT_COOLDOWN = 15_000L;

    private final CAbilityStones plugin;

    public ShadowCurseAbility(CAbilityStones plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean activate(Player player) {
        int affected = applyDebuffs(player);
        if (affected == 0) {
            sendMessage(player, Component.text("No enemies nearby!", NamedTextColor.RED));
            return false;
        }

        startParticleEffect(player);
        sendMessage(player, Component.text("Shadow Curse affected " + affected + " enemies!", NamedTextColor.DARK_PURPLE));
        return true;
    }

    /**
     * Applies blindness and slowness debuffs to nearby enemies.
     *
     * @param player the player activating the ability
     * @return the number of affected enemies
     */
    private int applyDebuffs(Player player) {
        int affected = 0;
        for (Entity entity : player.getNearbyEntities(RANGE, RANGE, RANGE)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player && entity.equals(player))) {
                LivingEntity target = (LivingEntity) entity;
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, EFFECT_DURATION, EFFECT_AMPLIFIER));
                target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, EFFECT_DURATION, EFFECT_AMPLIFIER));
                affected++;
            }
        }
        return affected;
    }

    /**
     * Starts the particle effect for the shadow curse.
     *
     * @param player the player to show particles for
     */
    private void startParticleEffect(Player player) {
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= PARTICLE_TICKS) {
                    cancel();
                    return;
                }

                Location center = player.getLocation().clone().add(0, 3, 0);
                spawnCurseParticles(center);
                ticks++;
            }

            private void spawnCurseParticles(Location center) {
                for (int i = 0; i < 8; i++) {
                    double angle = Math.random() * Math.PI * 2;
                    double radius = Math.random() * 4;
                    double x = center.getX() + Math.cos(angle) * radius;
                    double z = center.getZ() + Math.sin(angle) * radius;
                    Location spikeLoc = new Location(center.getWorld(), x, center.getY(), z);

                    for (double y = 0; y < 3; y += 0.2) {
                        Location particleLoc = spikeLoc.clone().subtract(0, y, 0);
                        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(75, 0, 130), 1.5f);
                        player.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0, 0, 0, 0, dustOptions);

                        if (Math.random() > 0.5) {
                            Particle.DustOptions darkDustOptions = new Particle.DustOptions(Color.fromRGB(50, 50, 50), 1.2f);
                            player.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0.05, 0, 0.05, 0, darkDustOptions);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 2);
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
        return plugin.getConfig().getString("stones.darkness.ability_name", "Shadow Curse");
    }

    @Override
    public long getCooldown() {
        return plugin.getConfig().getLong("stones.darkness.cooldown", DEFAULT_COOLDOWN / 1000) * 1000;
    }
}