package de.rettichlp.ucutils.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static java.util.Arrays.stream;

@Getter
@AllArgsConstructor
public enum Company {

    SUPERMARKET("Supermarkt", 115),
    GAS_STATION("Tankstelle", 150),
    WALTER_S("Walter's", 90),
    GUN_SHOP("Waffenladen", 265),
    FISHERMAN("Fischer", 190),
    BAR("Bar", 75),
    DELICATESSEN("Feinkost", 85),
    INSURANCE_COMPANY("Versicherung", 135),
    SHISHA_BAR("Shishabar", 70),
    CAR_DEALERSHIP("Fahrzeughändler", 165),
    LUIGI_S("Luigi's", 80),
    DISCO("Disco", 100),
    PHARMACY("Apotheke", 215),
    HANKY_S("Hanky's", 105);

    private final String displayName;
    private final int minValue;

    public static Company fromDisplayName(String displayName) {
        return stream(values())
                .filter(company -> company.getDisplayName().equals(displayName))
                .findFirst()
                .orElse(null);
    }
}
