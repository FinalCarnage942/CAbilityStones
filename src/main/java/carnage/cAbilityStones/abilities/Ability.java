package carnage.cAbilityStones.abilities;

import org.bukkit.entity.Player;

public interface Ability {
    boolean activate(Player player);

    String getName();

    long getCooldown();
}