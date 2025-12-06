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

    NULL("", "", false, GRAY, ""),
    POLIZEI("Polizei", "police", false, BLUE, "✯"),
    FBI("FBI", "fbi", false, DARK_BLUE, "✯"),
    RETTUNGSDIENST("Rettungsdienst", "medic", false, DARK_RED, "✚"),

    LA_COSA_NOSTRA("La Cosa Nostra", "mafia", true, DARK_AQUA, "⚜"),
    WESTSIDE_BALLAS("Westside Ballas", "gang", true, DARK_PURPLE, "☠"),
    CALDERON_KARTELL("Calderón Kartell", "mexican", true, GOLD, "☀"),
    KERZAKOV_FAMILIE("Kerzakov Familie", "kerzakov", true, RED, "✮"),
    HAYAT_KARTELL("Hayat Kartell", "hayat_kartell", true, DARK_GREEN, "Ħ"),
    YAKUZA("Yakuza", "yakuza", true, GREEN, "☯"),
    VELENTZAS("Velentzas", "velentzas", true, WHITE, "δ"),

    HITMAN("Hitman", "hitman", false, AQUA, "➹"),
    TERRORISTEN("Terroristen", "terror", false, GRAY, "❇"),
    KIRCHE("Kirche", "church", false, LIGHT_PURPLE, "†"),
    NEWS("News", "news", false, YELLOW, "✉");

    private final String displayName;
    private final String apiKey;
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

    public List<FactionMember> getMembers() {
        return storage.getFactionEntries().stream()
                .filter(factionEntry -> factionEntry.faction() == this)
                .findFirst()
                .map(FactionEntry::members)
                .orElse(new ArrayList<>());
    }

    public static @NotNull Optional<Faction> fromDisplayName(String displayName) {
        return stream(values())
                .filter(faction -> faction.getDisplayName().equals(displayName))
                .findFirst();
    }
}
