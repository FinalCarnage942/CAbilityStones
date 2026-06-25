package carnage.cAbilityStones.managers;

import carnage.cAbilityStones.models.StoneType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {
    private final Map<UUID, Map<StoneType, Long>> cooldowns;

    public CooldownManager() {
        this.cooldowns = new HashMap<>();
    }

    public void setCooldown(UUID player, StoneType type, long duration) {
        cooldowns.computeIfAbsent(player, k -> new HashMap<>()).put(type, System.currentTimeMillis() + duration);
    }

    public long getRemainingCooldown(UUID player, StoneType type) {
        Map<StoneType, Long> playerCooldowns = cooldowns.get(player);
        if (playerCooldowns == null) {
            return 0;
        }

        Long expiry = playerCooldowns.get(type);
        if (expiry == null) {
            return 0;
        }

        long remaining = expiry - System.currentTimeMillis();
        if (remaining <= 0) {
            playerCooldowns.remove(type);
            return 0;
        }

        return remaining;
    }
}