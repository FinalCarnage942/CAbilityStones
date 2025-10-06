package carnage.cAbilityStones.managers;

import carnage.cAbilityStones.CAbilityStones;
import carnage.cAbilityStones.models.StoneType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Manages the creation and identification of ability stones.
 */
public class StoneManager {
    private static final Material DEFAULT_MATERIAL = Material.EMERALD;
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("&([0-9a-fA-Fk-oK-O])");

    private final CAbilityStones plugin;
    private final NamespacedKey stoneKey;

    public StoneManager(CAbilityStones plugin) {
        this.plugin = plugin;
        this.stoneKey = new NamespacedKey(plugin, "stone_type");
    }

    /**
     * Creates an ability stone item for the specified type.
     *
     * @param type the stone type
     * @return the created ItemStack
     */
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

    /**
     * Parses the material for the stone from the configuration.
     *
     * @param path the configuration path
     * @return the material, or default if invalid
     */
    private Material parseMaterial(String path) {
        String materialName = plugin.getConfig().getString(path + ".material", "EMERALD");
        Material material = Material.getMaterial(materialName);
        return material != null ? material : DEFAULT_MATERIAL;
    }

    /**
     * Sets the display name for the stone item.
     *
     * @param meta the item meta
     * @param path the configuration path
     * @param type the stone type
     */
    private void setDisplayName(ItemMeta meta, String path, StoneType type) {
        String displayName = plugin.getConfig().getString(path + ".name", type.getDefaultName());
        meta.displayName(translateLegacyColorCodes(displayName).decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE));
    }

    /**
     * Sets the lore for the stone item.
     *
     * @param meta the item meta
     * @param path the configuration path
     * @param type the stone type
     */
    private void setLore(ItemMeta meta, String path, StoneType type) {
        List<String> configLore = plugin.getConfig().getStringList(path + ".lore");
        if (configLore.isEmpty()) {
            configLore = type.getDefaultLore();
        }
        List<Component> lore = configLore.stream()
                .map(this::translateLegacyColorCodes)
                .map(component -> component.decorationIfAbsent(TextDecoration.ITALIC, TextDecoration.State.FALSE))
                .collect(Collectors.toList());
        meta.lore(lore);
    }

    /**
     * Translates legacy color codes (&x) to Adventure Component with NamedTextColor.
     *
     * @param text the text with legacy color codes
     * @return the formatted Component
     */
    private Component translateLegacyColorCodes(String text) {
        if (text == null) {
            return Component.empty();
        }

        StringBuilder builder = new StringBuilder();
        Matcher matcher = COLOR_CODE_PATTERN.matcher(text);
        int lastEnd = 0;
        NamedTextColor currentColor = NamedTextColor.WHITE;
        Component result = Component.empty();

        while (matcher.find()) {
            builder.append(text, lastEnd, matcher.start());
            if (!builder.isEmpty()) {
                result = result.append(Component.text(builder.toString(), currentColor));
                builder.setLength(0);
            }

            char code = matcher.group(1).toLowerCase().charAt(0);
            currentColor = getColorFromCode(code);
            lastEnd = matcher.end();
        }

        builder.append(text.substring(lastEnd));
        if (!builder.isEmpty()) {
            result = result.append(Component.text(builder.toString(), currentColor));
        }

        return result;
    }

    /**
     * Maps legacy color codes to NamedTextColor.
     *
     * @param code the color code character
     * @return the corresponding NamedTextColor
     */
    private NamedTextColor getColorFromCode(char code) {
        return switch (code) {
            case '0' -> NamedTextColor.BLACK;
            case '1' -> NamedTextColor.DARK_BLUE;
            case '2' -> NamedTextColor.DARK_GREEN;
            case '3' -> NamedTextColor.DARK_AQUA;
            case '4' -> NamedTextColor.DARK_RED;
            case '5' -> NamedTextColor.DARK_PURPLE;
            case '6' -> NamedTextColor.GOLD;
            case '7' -> NamedTextColor.GRAY;
            case '8' -> NamedTextColor.DARK_GRAY;
            case '9' -> NamedTextColor.BLUE;
            case 'a' -> NamedTextColor.GREEN;
            case 'b' -> NamedTextColor.AQUA;
            case 'c' -> NamedTextColor.RED;
            case 'd' -> NamedTextColor.LIGHT_PURPLE;
            case 'e' -> NamedTextColor.YELLOW;
            case 'f' -> NamedTextColor.WHITE;
            default -> NamedTextColor.WHITE;
        };
    }

    /**
     * Gets the stone type from an ItemStack.
     *
     * @param item the ItemStack to check
     * @return the StoneType, or null if not a stone
     */
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

    /**
     * Checks if an ItemStack is an ability stone.
     *
     * @param item the ItemStack to check
     * @return true if the item is an ability stone
     */
    public boolean isAbilityStone(ItemStack item) {
        return getStoneType(item) != null;
    }
}