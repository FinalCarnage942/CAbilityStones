package carnage.cAbilityStones;

import carnage.cAbilityStones.commands.StoneCommand;
import carnage.cAbilityStones.listeners.StoneInteractListener;
import carnage.cAbilityStones.managers.AbilityManager;
import carnage.cAbilityStones.managers.CooldownManager;
import carnage.cAbilityStones.managers.StoneManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for CAbilityStones, managing initialization and component access.
 */
public final class CAbilityStones extends JavaPlugin {
    private static CAbilityStones instance;
    private StoneManager stoneManager;
    private AbilityManager abilityManager;
    private CooldownManager cooldownManager;

    @Override
    public void onEnable() {
        instance = this;
        initializeComponents();
        registerComponents();
        getLogger().info("AbilityStones enabled successfully!");
    }

    @Override
    public void onDisable() {
        getLogger().info("AbilityStones disabled!");
    }

    /**
     * Initializes plugin components.
     */
    private void initializeComponents() {
        saveDefaultConfig();
        cooldownManager = new CooldownManager();
        abilityManager = new AbilityManager(this);
        stoneManager = new StoneManager(this);
    }

    /**
     * Registers event listeners and commands.
     */
    private void registerComponents() {
        getServer().getPluginManager().registerEvents(new StoneInteractListener(this), this);
        getCommand("stone").setExecutor(new StoneCommand(this));
    }

    /**
     * Gets the singleton instance of the plugin.
     *
     * @return the plugin instance
     */
    public static CAbilityStones getInstance() {
        return instance;
    }

    /**
     * Gets the stone manager.
     *
     * @return the stone manager
     */
    public StoneManager getStoneManager() {
        return stoneManager;
    }

    /**
     * Gets the ability manager.
     *
     * @return the ability manager
     */
    public AbilityManager getAbilityManager() {
        return abilityManager;
    }

    /**
     * Gets the cooldown manager.
     *
     * @return the cooldown manager
     */
    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }
}