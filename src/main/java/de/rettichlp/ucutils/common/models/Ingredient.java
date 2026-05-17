package de.rettichlp.ucutils.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

import static java.util.Arrays.stream;

@Getter
@AllArgsConstructor
public enum Ingredient {

    POWDER("Pulver", true, true),
    HERBS("Kräuter", true, true),
    MEDICINAL_HERBS("Medizinische Kräuter"),
    CRYSTALS("Kristalle", true, true),
    SURPRISE_BAG("Wundertüte", false, true),
    COUGH_SYRUP("Hustensaft"),
    PAINKILLERS("Schmerzmittel"),
    ANTIBIOTICS("Antibiotika"),
    MASK("Maske"),
    IRON("Eisen"),
    GUNPOWDER("Schwarzpulver"),
    KEVLAR_FIBERS("Kevlarfasern");

    private final String displayName;
    private final boolean purity;
    private final boolean drugBankDropable;

    Ingredient(String displayName) {
        this.displayName = displayName;
        this.purity = false;
        this.drugBankDropable = false;
    }

    public static @NonNull Optional<Ingredient> fromDisplayName(String displayName) {
        return stream(values())
                .filter(ingredient -> ingredient.getDisplayName().equals(displayName))
                .findFirst();
    }
}
