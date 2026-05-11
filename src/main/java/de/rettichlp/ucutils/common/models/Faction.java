package de.rettichlp.ucutils.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.rettichlp.ucutils.UCUtils.storage;
import static java.util.Arrays.stream;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.BLUE;
import static net.minecraft.util.Formatting.DARK_AQUA;
import static net.minecraft.util.Formatting.DARK_BLUE;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.DARK_PURPLE;
import static net.minecraft.util.Formatting.DARK_RED;
import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.LIGHT_PURPLE;
import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.Formatting.YELLOW;

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
    private final Formatting color;
    private final String icon;

    public Text getNameTagSuffix() {
        return this != NULL
                ? empty()
                  .append(literal("⌜").copy().formatted(DARK_GRAY))
                  .append(literal(this.icon).copy().formatted(this.color))
                  .append(literal("⌟").copy().formatted(DARK_GRAY))
                : empty();
    }

    public static @NotNull Optional<Faction> fromDisplayName(String displayName) {
        return stream(values())
                .filter(faction -> faction.getDisplayName().equals(displayName))
                .findFirst();
    }
}
