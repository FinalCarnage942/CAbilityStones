package carnage.cAbilityStones.managers;

import carnage.cAbilityStones.CAbilityStones;
import carnage.cAbilityStones.abilities.*;
import carnage.cAbilityStones.models.StoneType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the registration and activation of ability stones.
 */
public class AbilityManager {
    private final CAbilityStones plugin;
    private final Map<StoneType, Ability> abilities;

    public AbilityManager(CAbilityStones plugin) {
        this.plugin = plugin;
        this.abilities = new HashMap<>();
        registerAbilities();
    }

    /**
     * Registers all available abilities.
     */
    private void registerAbilities() {
        abilities.put(StoneType.FIRE, new FireBurstAbility(plugin));
        abilities.put(StoneType.WATER, new HealWaveAbility(plugin));
        abilities.put(StoneType.EARTH, new StoneShieldAbility(plugin));
        abilities.put(StoneType.AIR, new DashForwardAbility(plugin));
        abilities.put(StoneType.LIGHTNING, new ChainLightningAbility(plugin));
        abilities.put(StoneType.DARKNESS, new ShadowCurseAbility(plugin));
    }

    /**
     * Activates an ability for the specified player and stone type.
     *
     * @param player the player activating the ability
     * @param type the type of stone
     * @return true if the ability was activated successfully
     */
    public boolean activateAbility(Player player, StoneType type) {
        Ability ability = abilities.get(type);
        return ability != null && ability.activate(player);
    }

    /**
     * Gets the ability associated with the stone type.
     *
     * @param type the stone type
     * @return the ability, or null if not found
     */
    public Ability getAbility(StoneType type) {
        return abilities.get(type);
    }
}