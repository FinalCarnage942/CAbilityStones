package carnage.cAbilityStones.abilities;

import org.bukkit.entity.Player;

/**
 * Defines the contract for an ability that can be activated by a player.
 */
public interface Ability {
    /**
     * Activates the ability for the specified player.
     *
     * @param player the player activating the ability
     * @return true if the ability was successfully activated, false otherwise
     */
    boolean activate(Player player);

    /**
     * Gets the name of the ability.
     *
     * @return the ability's name
     */
    String getName();

    /**
     * Gets the cooldown duration of the ability in milliseconds.
     *
     * @return the cooldown duration
     */
    long getCooldown();
}