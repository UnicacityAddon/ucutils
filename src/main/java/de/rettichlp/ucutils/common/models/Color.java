package de.rettichlp.ucutils.common.models;

import de.rettichlp.ucutils.common.gui.screens.components.CyclingButtonEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.ChatFormatting.getByName;
import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.network.chat.Component.translatable;

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
    public Component getDisplayName() {
        return translatable("ucutils.color." + this.name().toLowerCase()).withStyle(getFormatting());
    }

    @Override
    public @NotNull Tooltip getTooltip() {
        return create(translatable("ucutils.color." + this.name().toLowerCase()).withStyle(getFormatting()));
    }

    public ChatFormatting getFormatting() {
        return getByName(name());
    }
}
