package de.rettichlp.ucutils.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.Color;

@Getter
@AllArgsConstructor
public enum Faction {

    NULL("", "", false, new Color(255, 255, 255), ""),
    FBI("FBI", "fbi", false, new Color(0, 0, 170), "✯"),
    POLIZEI("Polizei", "police", false, new Color(85, 85, 255), "✯"),
    RETTUNGSDIENST("Rettungsdienst", "medic", false, new Color(170, 0, 0), "✚"),

    LA_COSA_NOSTRA("La Cosa Nostra", "mafia", true, new Color(0, 170, 170), "⚜"),
    WESTSIDE_BALLAS("Westside Ballas", "gang", true, new Color(170, 0, 170), "☠"),
    CALDERON_KARTELL("Calderón Kartell", "mexican", true, new Color(255, 170, 0), "☀"),
    KERZAKOV_FAMILIE("Kerzakov Familie", "kerzakov", true, new Color(255, 85, 85), "✮"),
    YAKUZA("Yakuza", "yakuza", true, new Color(85, 255, 85), "☯"),

    MERCENARY("Söldner", "söldner", false, new Color(170, 170, 170), "❇"),
    KIRCHE("Kirche", "church", false, new Color(255, 85, 255), "†"),
    NEWS("News", "news", false, new Color(255, 255, 85), "✉");

    private final String displayName;
    private final String apiKey;
    private final boolean isBadFaction;
    private final Color color;
    private final String icon;
}
