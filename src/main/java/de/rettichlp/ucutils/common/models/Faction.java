package de.rettichlp.ucutils.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Set;

import static de.rettichlp.ucutils.UCUtils.storage;
import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static net.minecraft.text.Text.empty;
import static net.minecraft.util.Formatting.AQUA;
import static net.minecraft.util.Formatting.BLUE;
import static net.minecraft.util.Formatting.DARK_AQUA;
import static net.minecraft.util.Formatting.DARK_BLUE;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.DARK_GREEN;
import static net.minecraft.util.Formatting.DARK_PURPLE;
import static net.minecraft.util.Formatting.DARK_RED;
import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.LIGHT_PURPLE;
import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.Formatting.WHITE;
import static net.minecraft.util.Formatting.YELLOW;

@Getter
@AllArgsConstructor
public enum Faction {

    NULL("", false, GRAY, ""),
    POLIZEI("Polizei", false, BLUE, "✯"),
    FBI("FBI", false, DARK_BLUE, "✯"),
    RETTUNGSDIENST("Rettungsdienst", false, DARK_RED, "✚"),

    LA_COSA_NOSTRA("La Cosa Nostra", true, DARK_AQUA, "⚜"),
    WESTSIDE_BALLAS("Westside Ballas", true, DARK_PURPLE, "☠"),
    CALDERON_KARTELL("Calderón Kartell", true, GOLD, "☀"),
    KERZAKOV_FAMILIE("Kerzakov Familie", true, RED, "✮"),
    HAYAT_KARTELL("Hayat Kartell", true, DARK_GREEN, "Ħ"),
    YAKUZA("Yakuza", true, GREEN, "☯"),
    VELENTZAS("Velentzas", true, WHITE, "δ"),

    HITMAN("Hitman", false, AQUA, "➹"),
    TERRORISTEN("Terroristen", false, GRAY, "❇"),
    KIRCHE("Kirche", false, LIGHT_PURPLE, "†"),
    NEWS("News", false, YELLOW, "✉");

    private final String displayName;
    private final boolean isBadFaction;
    private final Formatting color;
    private final String icon;

    public Text getNameTagSuffix() {
        return this == NULL ? empty() : empty()
                .append(Text.of("⌜")
                        .copy()
                        .formatted(DARK_GRAY))
                .append(Text.of(this.icon)
                        .copy()
                        .formatted(this.color))
                .append(Text.of("⌟")
                        .copy()
                        .formatted(DARK_GRAY));
    }

    public Set<FactionMember> getMembers() {
        return storage.getFactionEntries().stream()
                .filter(factionEntry -> factionEntry.faction() == this)
                .findFirst()
                .map(FactionEntry::members)
                .orElse(emptySet());
    }

    public static @NotNull Optional<Faction> fromDisplayName(String displayName) {
        return stream(values())
                .filter(faction -> displayName.contains(faction.getDisplayName()))
                .findFirst();
    }
}
