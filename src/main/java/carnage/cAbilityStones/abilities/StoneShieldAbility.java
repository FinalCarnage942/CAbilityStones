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
import org.bukkit.util.Vector;

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
        player.sendMessage(Component.text("Stone Shield activated!", NamedTextColor.GREEN));
        return true;
    }

    private void applyResistance(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, RESISTANCE_DURATION, RESISTANCE_AMPLIFIER));
    }

    private void knockbackNearbyEnemies(Player player) {
        for (Entity entity : player.getNearbyEntities(KNOCKBACK_RANGE, KNOCKBACK_RANGE, KNOCKBACK_RANGE)) {
            if (entity instanceof LivingEntity && !(entity instanceof Player && entity.equals(player))) {
                Vector direction = entity.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
                entity.setVelocity(direction.multiply(KNOCKBACK_FORCE).setY(KNOCKBACK_Y));
            }
        }
    }

    private void startParticleEffect(Player player) {
        ArmorStand[] stoneHeads = createStoneHeads(player.getLocation());
        double[] angle = {0};
        int[] ticks = {0};

        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (ticks[0] >= PARTICLE_TICKS) {
                for (ArmorStand head : stoneHeads) head.remove();
                task.cancel();
                return;
            }

            Location playerLoc = player.getLocation().clone().add(0, 0.5, 0);
            for (int i = 0; i < 3; i++) {
                double curAngle = angle[0] + (i * 120);
                double rad = Math.toRadians(curAngle);
                Location headLoc = playerLoc.clone().add(Math.cos(rad) * 1.5, 0, Math.sin(rad) * 1.5);
                headLoc.setYaw((float) curAngle);
                stoneHeads[i].teleport(headLoc);
            }

            angle[0] += 5;
            ticks[0]++;
        }, 0, 1);
    }

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

    @Override
    public String getName() {
        return plugin.getConfig().getString("stones.earth.ability_name", "Stone Shield");
    }

    @Override
    public long getCooldown() {
        return plugin.getConfig().getLong("stones.earth.cooldown", DEFAULT_COOLDOWN / 1000) * 1000;
    }
}