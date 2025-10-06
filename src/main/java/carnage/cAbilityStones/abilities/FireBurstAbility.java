package carnage.cAbilityStones.abilities;

import carnage.cAbilityStones.CAbilityStones;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * Implements the Fire Burst ability, launching a fireball with orbiting blocks that explodes on impact.
 */
public class FireBurstAbility implements Ability {
    private static final float EXPLOSION_POWER = 3.0f;
    private static final double MOVE_SPEED = 0.5;
    private static final int MAX_TICKS = 100;
    private static final long DEFAULT_COOLDOWN = 10_000L;

    private final CAbilityStones plugin;

    public FireBurstAbility(CAbilityStones plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean activate(Player player) {
        Location startLoc = player.getEyeLocation();
        Vector direction = startLoc.getDirection().normalize();
        ArmorStand core = createCore(startLoc);
        ArmorStand[] orbitingBlocks = createOrbitingBlocks(startLoc);

        startFireballMovement(player, core, orbitingBlocks, startLoc, direction);
        sendMessage(player, Component.text("Fire Burst activated!", NamedTextColor.RED));
        return true;
    }

    /**
     * Creates the core armor stand for the fireball.
     *
     * @param startLoc the starting location
     * @return the core armor stand
     */
    private ArmorStand createCore(Location startLoc) {
        ArmorStand core = (ArmorStand) startLoc.getWorld().spawnEntity(startLoc, EntityType.ARMOR_STAND);
        core.setVisible(false);
        core.setGravity(false);
        core.setInvulnerable(true);
        core.setMarker(true);
        core.getEquipment().setHelmet(new ItemStack(Material.MAGMA_BLOCK));
        return core;
    }

    /**
     * Creates orbiting armor stands with different block types.
     *
     * @param startLoc the starting location
     * @return array of orbiting armor stands
     */
    private ArmorStand[] createOrbitingBlocks(Location startLoc) {
        Material[] blocks = {Material.NETHERRACK, Material.BLACKSTONE, Material.COAL_BLOCK};
        ArmorStand[] orbitingBlocks = new ArmorStand[3];
        for (int i = 0; i < 3; i++) {
            ArmorStand block = (ArmorStand) startLoc.getWorld().spawnEntity(startLoc, EntityType.ARMOR_STAND);
            block.setVisible(false);
            block.setGravity(false);
            block.setInvulnerable(true);
            block.setMarker(true);
            block.getEquipment().setHelmet(new ItemStack(blocks[i]));
            orbitingBlocks[i] = block;
        }
        return orbitingBlocks;
    }

    /**
     * Starts the fireball movement and particle effects.
     *
     * @param player the player activating the ability
     * @param core the core armor stand
     * @param orbitingBlocks the orbiting armor stands
     * @param startLoc the starting location
     * @param direction the movement direction
     */
    private void startFireballMovement(Player player, ArmorStand core, ArmorStand[] orbitingBlocks, Location startLoc, Vector direction) {
        new BukkitRunnable() {
            int ticks = 0;
            double angle = 0;
            Location currentLoc = startLoc.clone();

            @Override
            public void run() {
                if (ticks >= MAX_TICKS || currentLoc.getBlock().getType().isSolid()) {
                    createExplosion(currentLoc);
                    core.remove();
                    for (ArmorStand block : orbitingBlocks) {
                        block.remove();
                    }
                    cancel();
                    return;
                }

                updateFireballPosition(currentLoc, direction, core, orbitingBlocks, angle);
                spawnFireballParticles(currentLoc);
                angle += 20;
                ticks++;
            }

            private void createExplosion(Location loc) {
                loc.getWorld().createExplosion(loc, EXPLOSION_POWER, false, false);
                loc.getWorld().spawnParticle(Particle.EXPLOSION, loc, 5, 0.5, 0.5, 0.5);
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 50, 1, 1, 1, 0.1);
            }

            private void updateFireballPosition(Location loc, Vector direction, ArmorStand core, ArmorStand[] orbitingBlocks, double angle) {
                loc.add(direction.clone().multiply(MOVE_SPEED));
                core.teleport(loc);
                for (int i = 0; i < 3; i++) {
                    double currentAngle = angle + (i * 120);
                    double radians = Math.toRadians(currentAngle);
                    double x = Math.cos(radians) * 0.6;
                    double z = Math.sin(radians) * 0.6;
                    Location blockLoc = loc.clone().add(x, -1.5, z);
                    orbitingBlocks[i].teleport(blockLoc);
                }
            }

            private void spawnFireballParticles(Location loc) {
                loc.getWorld().spawnParticle(Particle.FLAME, loc, 10, 0.3, 0.3, 0.3, 0.02);
                loc.getWorld().spawnParticle(Particle.SMOKE, loc, 5, 0.2, 0.2, 0.2, 0.01);
                loc.getWorld().spawnParticle(Particle.LAVA, loc, 2, 0.2, 0.2, 0.2);
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
        return plugin.getConfig().getString("stones.fire.ability_name", "Fire Burst");
    }

    @Override
    public long getCooldown() {
        return plugin.getConfig().getLong("stones.fire.cooldown", DEFAULT_COOLDOWN / 1000) * 1000;
    }
}