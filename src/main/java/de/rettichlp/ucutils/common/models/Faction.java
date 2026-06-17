package de.rettichlp.ucutils.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static java.util.Arrays.stream;
import static net.minecraft.ChatFormatting.BLUE;
import static net.minecraft.ChatFormatting.DARK_AQUA;
import static net.minecraft.ChatFormatting.DARK_BLUE;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.DARK_PURPLE;
import static net.minecraft.ChatFormatting.DARK_RED;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.LIGHT_PURPLE;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.ChatFormatting.YELLOW;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;

@Getter
@AllArgsConstructor
public enum Faction {

    NULL("", "", false, GRAY, ""),
    FBI("FBI", "fbi", false, DARK_BLUE, "✯"),
    POLIZEI("Polizei", "police", false, BLUE, "✯"),
    RETTUNGSDIENST("Rettungsdienst", "medic", false, DARK_RED, "✚"),

    LA_COSA_NOSTRA("La Cosa Nostra", "mafia", true, DARK_AQUA, "⚜"),
    WESTSIDE_BALLAS("Westside Ballas", "gang", true, DARK_PURPLE, "☠"),
    CALDERON_KARTELL("Calderón Kartell", "mexican", true, GOLD, "☀"),
    KERZAKOV_FAMILIE("Kerzakov Familie", "kerzakov", true, RED, "✮"),
    YAKUZA("Yakuza", "yakuza", true, GREEN, "☯"),

    MERCENARY("Söldner", "söldner", false, GRAY, "❇"),
    KIRCHE("Kirche", "church", false, LIGHT_PURPLE, "†"),
    NEWS("News", "news", false, YELLOW, "✉");

    private final String displayName;
    private final String apiKey;
    private final boolean isBadFaction;
    private final ChatFormatting color;
    private final String icon;

    public Component getNameTagSuffix() {
        return this != NULL
                ? empty()
                .append(literal("⌜").withStyle(DARK_GRAY))
                .append(literal(this.icon).withStyle(this.color))
                .append(literal("⌟").withStyle(DARK_GRAY))
                : empty();
    }

    public static @NotNull Optional<Faction> fromDisplayName(String displayName) {
        return stream(values())
                .filter(faction -> faction.getDisplayName().equals(displayName))
                .findFirst();
    }
}
