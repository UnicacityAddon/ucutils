package de.rettichlp.ucutils.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

import static java.util.Arrays.stream;

@Getter
@AllArgsConstructor
public enum InventoryItem {

    // drugs
    POWDER("Pulver", true),
    MEDICINAL_HERBS("Medizinische Kräuter"),
    HERBS("Kräuter", true),
    CRYSTALS("Kristalle", true),
    GRAB_BAG("Wundertüte", true),

    // medical
    COUGH_SYRUP("Hustensaft"),
    PAINKILLERS("Schmerzmittel"),
    ANTIBIOTICS("Antibiotika"),

    // other
    MASK("Maske"),
    IRON("Eisen"),
    GUN_POWDER("Schwarzpulver"),
    KEVLAR_FIBERS("Kevlarfasern");

    private final String displayName;
    private final boolean drugBankItem;

    InventoryItem(String displayName) {
        this.displayName = displayName;
        this.drugBankItem = false;
    }

    public static @NonNull Optional<InventoryItem> fromDisplayName(String displayName) {
        return stream(values())
                .filter(inventoryItem -> inventoryItem.getDisplayName().equals(displayName))
                .findFirst();
    }
}
