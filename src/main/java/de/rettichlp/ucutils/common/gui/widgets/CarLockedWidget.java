package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.ucutils.common.gui.screens.components.CyclingButtonEntry;
import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsTextWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.IOptionWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.gui.widgets.CarLockedWidget.Style.MINIMALISTIC;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

@UCUtilsWidget(registryName = "car_locked", defaultX = 110.0, defaultY = 4.0)
public class CarLockedWidget extends AbstractUCUtilsTextWidget<CarLockedWidget.Configuration> {

    private static final Component WIDGETS_CAR_LOCKED_OPTIONS_NAME = translatable("ucutils.options.widgets.car_locked.options.name");
    private static final Component WIDGETS_CAR_LOCKED_OPTIONS_TOOLTIP = translatable("ucutils.options.widgets.car_locked.options.tooltip");
    private static final Component WIDGETS_CAR_LOCKED_OPTIONS_STYLE_NAME = translatable("ucutils.options.widgets.car_locked.options.style.name");

    @Override
    public Component text() {
        return getWidgetConfiguration().getStyle() == MINIMALISTIC
                ? (storage.isCarLocked() ? literal("🔒").withStyle(GREEN) : literal("🔓").withStyle(GOLD))
                : empty()
                .append(literal("Fahrzeug").withStyle(GRAY))
                .append(literal(":").withStyle(DARK_GRAY)).append(" ")
                .append(storage.isCarLocked() ? literal("zu").withStyle(GREEN) : literal("offen").withStyle(GOLD));
    }

    @Override
    public Component getDisplayName() {
        return WIDGETS_CAR_LOCKED_OPTIONS_NAME;
    }

    @Override
    public Component getTooltip() {
        return WIDGETS_CAR_LOCKED_OPTIONS_TOOLTIP;
    }

    @Getter
    @AllArgsConstructor
    public enum Style implements CyclingButtonEntry {

        DEFAULT(translatable("ucutils.options.widgets.car_locked.options.style.value.default.name"), translatable("ucutils.options.widgets.car_locked.options.style.value.default.tooltip")),
        MINIMALISTIC(translatable("ucutils.options.widgets.car_locked.options.style.value.minimalistic.name"), translatable("ucutils.options.widgets.car_locked.options.style.value.minimalistic.tooltip"));

        private final Component name;
        private final Component tooltip;

        @Override
        public @NotNull Component getDisplayName() {
            return this.name;
        }

        @Override
        public @NotNull Tooltip getTooltip() {
            return Tooltip.create(this.tooltip);
        }
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Configuration extends UCUtilsWidgetConfiguration implements IOptionWidget {

        private Style style = MINIMALISTIC;

        @Override
        public LayoutElement optionsWidget() {
            return CycleButton.builder(Style::getDisplayName, this.style)
                    .withValues(Style.values())
                    .withTooltip(Style::getTooltip)
                    .create(WIDGETS_CAR_LOCKED_OPTIONS_STYLE_NAME, (_, style) -> this.style = style);
        }
    }
}
