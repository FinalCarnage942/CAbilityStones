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
            player.sendMessage(Component.text("No enemies nearby!", NamedTextColor.RED));
            return false;
        }

        startParticleEffect(player);
        player.sendMessage(Component.text("Shadow Curse affected " + affected + " enemies!", NamedTextColor.DARK_PURPLE));
        return true;
    }

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

    private void startParticleEffect(Player player) {
        int[] ticks = {0};
        Particle.DustOptions purpleOpt = new Particle.DustOptions(Color.fromRGB(75, 0, 130), 1.5f);
        Particle.DustOptions darkOpt = new Particle.DustOptions(Color.fromRGB(50, 50, 50), 1.2f);

        plugin.getServer().getScheduler().runTaskTimer(plugin, task -> {
            if (ticks[0] >= PARTICLE_TICKS) {
                task.cancel();
                return;
            }

            Location center = player.getLocation().clone().add(0, 3, 0);
            for (int i = 0; i < 8; i++) {
                double angle = Math.random() * Math.PI * 2;
                double radius = Math.random() * 4;
                Location spikeLoc = new Location(center.getWorld(),
                    center.getX() + Math.cos(angle) * radius, center.getY(),
                    center.getZ() + Math.sin(angle) * radius);

                for (double y = 0; y < 3; y += 0.2) {
                    Location particleLoc = spikeLoc.clone().subtract(0, y, 0);
                    player.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0, 0, 0, 0, purpleOpt);
                    if (Math.random() > 0.5)
                        player.getWorld().spawnParticle(Particle.DUST, particleLoc, 1, 0.05, 0, 0.05, 0, darkOpt);
                }
            }
            ticks[0]++;
        }, 0, 2);
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