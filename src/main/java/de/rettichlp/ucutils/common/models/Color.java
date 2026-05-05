package de.rettichlp.ucutils.common.models;

import de.rettichlp.ucutils.common.gui.screens.components.CyclingButtonEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.text.Text.translatable;
import static net.minecraft.util.Formatting.byName;

@Getter
@AllArgsConstructor
public enum Color implements CyclingButtonEntry {

    BLACK,
    DARK_BLUE,
    DARK_GREEN,
    DARK_AQUA,
    DARK_RED,
    DARK_PURPLE,
    GOLD,
    GRAY,
    DARK_GRAY,
    BLUE,
    GREEN,
    AQUA,
    RED,
    LIGHT_PURPLE,
    YELLOW,
    WHITE;

    @Override
    public MutableText getDisplayName() {
        return translatable("ucutils.color." + this.name().toLowerCase()).formatted(getFormatting());
    }

    @Override
    public @NotNull Tooltip getTooltip() {
        return Tooltip.of(translatable("ucutils.color." + this.name().toLowerCase()).formatted(getFormatting()));
    }

    public Formatting getFormatting() {
        return byName(name());
    }
}
