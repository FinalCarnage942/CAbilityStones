package carnage.cAbilityStones.abilities;

import carnage.cAbilityStones.CAbilityStones;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Implements the Stone Shield ability, granting resistance and knocking back enemies with orbiting stone particles.
 */
public class StoneShieldAbility implements Ability {
    private static final double KNOCKBACK_RANGE = 3.0;
    private static final double KNOCKBACK_FORCE = 0.8;
    private static final double KNOCKBACK_Y = 0.3;
    private static final int RESISTANCE_DURATION = 100;
    private static final int RESISTANCE_AMPLIFIER = 2;
    private static final int PARTICLE_TICKS = 100;
    private static final long DEFAULT_COOLDOWN = 15_000L;

    private final CAbilityStones plugin;

    public StoneShieldAbility(CAbilityStones plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean activate(Player player) {
        applyResistance(player);
        knockbackNearbyEnemies(player);
        startParticleEffect(player);
        sendMessage(player, Component.text("Stone Shield activated!", NamedTextColor.GREEN));
        return true;
    }

    /**
     * Applies resistance effect to the player.
     *
     * @param player the player to receive the effect
     */
    private void applyResistance(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, RESISTANCE_DURATION, RESISTANCE_AMPLIFIER));
    }

    /**
     * Knocks back nearby enemies.
     *
     * @param player the player activating the ability
     */
    private void knockbackNearbyEnemies(Player player) {
        for (Entity entity : player.getNearbyEntities(KNOCKBACK_RANGE, KNOCKBACK_RANGE, KNOCKBACK_RANGE)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player && entity.equals(player))) {
                Vector direction = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                entity.setVelocity(direction.multiply(KNOCKBACK_FORCE).setY(KNOCKBACK_Y));
            }
        }
    }

    /**
     * Starts the particle effect with orbiting stone heads.
     *
     * @param player the player to show particles for
     */
    private void startParticleEffect(Player player) {
        ArmorStand[] stoneHeads = createStoneHeads(player.getLocation());
        new BukkitRunnable() {
            int ticks = 0;
            double angle = 0;

            @Override
            public void run() {
                if (ticks >= PARTICLE_TICKS) {
                    for (ArmorStand head : stoneHeads) {
                        head.remove();
                    }
                    cancel();
                    return;
                }

                Location playerLoc = player.getLocation().clone().add(0, 0.5, 0);
                updateStoneHeadPositions(playerLoc, stoneHeads, angle);
                angle += 5;
                ticks++;
            }

            private void updateStoneHeadPositions(Location playerLoc, ArmorStand[] stoneHeads, double angle) {
                for (int i = 0; i < 3; i++) {
                    double currentAngle = angle + (i * 120);
                    double radians = Math.toRadians(currentAngle);
                    double x = Math.cos(radians) * 1.5;
                    double z = Math.sin(radians) * 1.5;
                    Location headLoc = playerLoc.clone().add(x, 0, z);
                    headLoc.setYaw((float) currentAngle);
                    stoneHeads[i].teleport(headLoc);
                }
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    /**
     * Creates armor stands with stone helmets for the particle effect.
     *
     * @param location the starting location
     * @return array of armor stands
     */
    private ArmorStand[] createStoneHeads(Location location) {
        ArmorStand[] stoneHeads = new ArmorStand[3];
        for (int i = 0; i < 3; i++) {
            ArmorStand head = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
            head.setVisible(false);
            head.setGravity(false);
            head.setInvulnerable(true);
            head.setMarker(true);
            head.setSmall(true);
            head.getEquipment().setHelmet(new ItemStack(Material.STONE));
            stoneHeads[i] = head;
        }
        return stoneHeads;
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
        return plugin.getConfig().getString("stones.earth.ability_name", "Stone Shield");
    }

    @Override
    public long getCooldown() {
        return plugin.getConfig().getLong("stones.earth.cooldown", DEFAULT_COOLDOWN / 1000) * 1000;
    }
}