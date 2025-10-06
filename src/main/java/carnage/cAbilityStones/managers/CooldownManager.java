package carnage.cAbilityStones.managers;

import carnage.cAbilityStones.models.StoneType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages cooldowns for ability stones on a per-player basis.
 */
public class CooldownManager {
    private final Map<UUID, Map<StoneType, Long>> cooldowns;

    public CooldownManager() {
        this.cooldowns = new HashMap<>();
    }

    /**
     * Sets a cooldown for a player and stone type.
     *
     * @param player the player's UUID
     * @param type the stone type
     * @param duration the cooldown duration in milliseconds
     */
    public void setCooldown(UUID player, StoneType type, long duration) {
        cooldowns.computeIfAbsent(player, k -> new HashMap<>()).put(type, System.currentTimeMillis() + duration);
    }

    /**
     * Checks if a player has an active cooldown for a stone type.
     *
     * @param player the player's UUID
     * @param type the stone type
     * @return true if the player is on cooldown
     */
    public boolean hasCooldown(UUID player, StoneType type) {
        Map<StoneType, Long> playerCooldowns = cooldowns.get(player);
        if (playerCooldowns == null) {
            return false;
        }

        Long expiry = playerCooldowns.get(type);
        if (expiry == null) {
            return false;
        }

        if (System.currentTimeMillis() >= expiry) {
            playerCooldowns.remove(type);
            return false;
        }

        return true;
    }

    /**
     * Gets the remaining cooldown time for a player and stone type.
     *
     * @param player the player's UUID
     * @param type the stone type
     * @return the remaining cooldown in milliseconds, or 0 if none
     */
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
        return Math.max(0, remaining);
    }

}