package carnage.cAbilityStones.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorTranslator {
    private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("&([0-9a-fA-Fk-oK-O])");

    public static Component translate(String text) {
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

    private static NamedTextColor getColorFromCode(char code) {
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
}
