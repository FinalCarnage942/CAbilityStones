package carnage.cAbilityStones.models;

import java.util.Arrays;
import java.util.List;

/**
 * Enum representing the types of ability stones with their default names and lore.
 */
public enum StoneType {
    FIRE(
            "&c&lFire Stone",
            Arrays.asList("&7Ability: &eFire Burst", "&7Shoot a fireball that explodes", "&7on impact", "", "&eCooldown: 10s")
    ),
    WATER(
            "&b&lWater Stone",
            Arrays.asList("&7Ability: &eHeal Wave", "&7Heal yourself and nearby allies", "&7and apply Regen II for 5s", "", "&eCooldown: 12s")
    ),
    EARTH(
            "&a&lEarth Stone",
            Arrays.asList("&7Ability: &eStone Shield", "&7Gain Resistance III for 5s", "&7and knockback nearby enemies", "", "&eCooldown: 15s")
    ),
    AIR(
            "&f&lAir Stone",
            Arrays.asList("&7Ability: &eDash Forward", "&7Quickly dash forward 5-7 blocks", "&7with Speed II and particles", "", "&eCooldown: 8s")
    ),
    LIGHTNING(
            "&e&lLightning Stone",
            Arrays.asList("&7Ability: &eChain Lightning", "&7Strike 3 nearest enemies within", "&76 blocks with lightning", "", "&eCooldown: 12s")
    ),
    DARKNESS(
            "&5&lDarkness Stone",
            Arrays.asList("&7Ability: &eShadow Curse", "&7Apply Blindness II and Slowness II", "&7to nearby enemies for 3s", "", "&eCooldown: 15s")
    );

    private final String defaultName;
    private final List<String> defaultLore;

    StoneType(String defaultName, List<String> defaultLore) {
        this.defaultName = defaultName;
        this.defaultLore = defaultLore;
    }

    /**
     * Gets the default display name of the stone.
     *
     * @return the default name
     */
    public String getDefaultName() {
        return defaultName;
    }

    /**
     * Gets the default lore of the stone.
     *
     * @return the default lore
     */
    public List<String> getDefaultLore() {
        return defaultLore;
    }
}