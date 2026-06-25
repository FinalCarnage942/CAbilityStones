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
import org.bukkit.util.Vector;

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
        player.sendMessage(Component.text("Fire Burst activated!", NamedTextColor.RED));
        return true;
    }

    private ArmorStand createCore(Location startLoc) {
        ArmorStand core = (ArmorStand) startLoc.getWorld().spawnEntity(startLoc, EntityType.ARMOR_STAND);
        core.setVisible(false);
        core.setGravity(false);
        core.setInvulnerable(true);
        core.setMarker(true);
        core.getEquipment().setHelmet(new ItemStack(Material.MAGMA_BLOCK));
        return core;
    }

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

    private void startFireballMovement(Player player, ArmorStand core, ArmorStand[] orbitingBlocks, Location startLoc, Vector direction) {
        Location currentLoc = startLoc.clone();
        Vector moveDir = direction.clone().multiply(MOVE_SPEED);
        int[] counter = {0};

        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (counter[0] >= MAX_TICKS || currentLoc.getBlock().getType().isSolid()) {
                currentLoc.getWorld().createExplosion(currentLoc, EXPLOSION_POWER, false, false);
                currentLoc.getWorld().spawnParticle(Particle.EXPLOSION, currentLoc, 5, 0.5, 0.5, 0.5);
                currentLoc.getWorld().spawnParticle(Particle.FLAME, currentLoc, 50, 1, 1, 1, 0.1);
                core.remove();
                for (ArmorStand block : orbitingBlocks) block.remove();
                task.cancel();
                return;
            }

            currentLoc.add(moveDir.clone());
            core.teleport(currentLoc);

            double angle = counter[0] * 20.0;
            for (int i = 0; i < 3; i++) {
                double a = Math.toRadians(angle + (i * 120));
                orbitingBlocks[i].teleport(currentLoc.clone().add(Math.cos(a) * 0.6, -1.5, Math.sin(a) * 0.6));
            }

            currentLoc.getWorld().spawnParticle(Particle.FLAME, currentLoc, 10, 0.3, 0.3, 0.3, 0.02);
            currentLoc.getWorld().spawnParticle(Particle.SMOKE, currentLoc, 5, 0.2, 0.2, 0.2, 0.01);
            currentLoc.getWorld().spawnParticle(Particle.LAVA, currentLoc, 2, 0.2, 0.2, 0.2);
            counter[0]++;
        }, 0, 1);
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