package de.rettichlp.ucutils.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.ChatFormatting;

import java.awt.Color;

import static net.minecraft.ChatFormatting.BLUE;
import static net.minecraft.ChatFormatting.DARK_AQUA;
import static net.minecraft.ChatFormatting.DARK_BLUE;
import static net.minecraft.ChatFormatting.DARK_PURPLE;
import static net.minecraft.ChatFormatting.DARK_RED;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.LIGHT_PURPLE;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.ChatFormatting.WHITE;
import static net.minecraft.ChatFormatting.YELLOW;

@Getter
@AllArgsConstructor
public enum Faction {

    NULL("", "", false, WHITE, ""),
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

    public Color getAwtColor() {
        return this.color.getColor() != null ? new Color(this.color.getColor()) : Color.WHITE;
    }
}
