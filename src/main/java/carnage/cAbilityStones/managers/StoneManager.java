package carnage.cAbilityStones.managers;

import carnage.cAbilityStones.CAbilityStones;
import carnage.cAbilityStones.models.StoneType;
import carnage.cAbilityStones.utils.ColorTranslator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.stream.Collectors;

public class StoneManager {
    private static final Material DEFAULT_MATERIAL = Material.EMERALD;

    private final CAbilityStones plugin;
    private final NamespacedKey stoneKey;

    public StoneManager(CAbilityStones plugin) {
        this.plugin = plugin;
        this.stoneKey = new NamespacedKey(plugin, "stone_type");
    }

    public ItemStack createStone(StoneType type) {
        String path = "stones." + type.name().toLowerCase();
        Material material = parseMaterial(path);
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        setDisplayName(meta, path, type);
        setLore(meta, path, type);
        meta.getPersistentDataContainer().set(stoneKey, PersistentDataType.STRING, type.name());

        item.setItemMeta(meta);
        return item;
    }

    private Material parseMaterial(String path) {
        String materialName = plugin.getConfig().getString(path + ".material", "EMERALD");
        Material material = Material.getMaterial(materialName);
        return material != null ? material : DEFAULT_MATERIAL;
    }

    private void setDisplayName(ItemMeta meta, String path, StoneType type) {
        String displayName = plugin.getConfig().getString(path + ".name", type.getDefaultName());
        meta.displayName(ColorTranslator.translate(displayName).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
    }

    private void setLore(ItemMeta meta, String path, StoneType type) {
        List<String> configLore = plugin.getConfig().getStringList(path + ".lore");
        if (configLore.isEmpty()) {
            configLore = type.getDefaultLore();
        }
        List<Component> lore = configLore.stream()
                .map(ColorTranslator::translate)
                .map(component -> component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                .collect(Collectors.toList());
        meta.lore(lore);
    }

    public StoneType getStoneType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        String typeStr = meta.getPersistentDataContainer().get(stoneKey, PersistentDataType.STRING);
        if (typeStr == null) {
            return null;
        }

        try {
            return StoneType.valueOf(typeStr);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isAbilityStone(ItemStack item) {
        return getStoneType(item) != null;
    }
}