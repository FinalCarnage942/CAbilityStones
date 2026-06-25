package carnage.cAbilityStones;

import carnage.cAbilityStones.commands.StoneCommand;
import carnage.cAbilityStones.listeners.StoneInteractListener;
import carnage.cAbilityStones.managers.AbilityManager;
import carnage.cAbilityStones.managers.CooldownManager;
import carnage.cAbilityStones.managers.StoneManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class CAbilityStones extends JavaPlugin {
    private static CAbilityStones instance;
    private StoneManager stoneManager;
    private AbilityManager abilityManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        cooldownManager = new CooldownManager();
        abilityManager = new AbilityManager(this);
        stoneManager = new StoneManager(this);

        new StoneInteractListener(this);
        getCommand("stone").setExecutor(new StoneCommand(this));

        getLogger().info("AbilityStones enabled successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AbilityStones disabled!");
    }

    public static CAbilityStones getInstance() { return instance; }
    public StoneManager getStoneManager() { return stoneManager; }
    public AbilityManager getAbilityManager() { return abilityManager; }
    public CooldownManager getCooldownManager() { return cooldownManager; }
}