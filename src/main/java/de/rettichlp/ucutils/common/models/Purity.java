package de.rettichlp.ucutils.common.models;

import de.rettichlp.ucutils.common.gui.screens.components.CyclingButtonEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

import static net.minecraft.text.Text.translatable;
import static net.minecraft.util.Formatting.DARK_GREEN;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.Formatting.YELLOW;

@Getter
@AllArgsConstructor
public enum Purity implements CyclingButtonEntry {

    BEST(translatable("ucutils.purity.best").formatted(DARK_GREEN)),
    GOOD(translatable("ucutils.purity.good").formatted(GREEN)),
    MEDIUM(translatable("ucutils.purity.medium").formatted(YELLOW)),
    BAD(translatable("ucutils.purity.bad").formatted(RED));

    private final Text displayName;

    @Override
    public Tooltip getTooltip() {
        return Tooltip.of(this.displayName);
    }
}
