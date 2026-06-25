package carnage.cAbilityStones.managers;

import carnage.cAbilityStones.CAbilityStones;
import carnage.cAbilityStones.abilities.*;
import carnage.cAbilityStones.models.StoneType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class AbilityManager {
    private final CAbilityStones plugin;
    private final Map<StoneType, Ability> abilities;

    public AbilityManager(CAbilityStones plugin) {
        this.plugin = plugin;
        this.abilities = new HashMap<>();
        registerAbilities();
    }

    private void registerAbilities() {
        abilities.put(StoneType.FIRE, new FireBurstAbility(plugin));
        abilities.put(StoneType.WATER, new HealWaveAbility(plugin));
        abilities.put(StoneType.EARTH, new StoneShieldAbility(plugin));
        abilities.put(StoneType.AIR, new DashForwardAbility(plugin));
        abilities.put(StoneType.LIGHTNING, new ChainLightningAbility(plugin));
        abilities.put(StoneType.DARKNESS, new ShadowCurseAbility(plugin));
    }

    public boolean activateAbility(Player player, StoneType type) {
        Ability ability = abilities.get(type);
        return ability != null && ability.activate(player);
    }

    public Ability getAbility(StoneType type) {
        return abilities.get(type);
    }
}