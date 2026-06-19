package de.rettichlp.ucutils.common.services;

import de.rettichlp.ucutils.common.configuration.options.Options;
import de.rettichlp.ucutils.common.gui.screens.components.CyclingButtonEntry;
import de.rettichlp.ucutils.common.gui.screens.components.ToggleButtonWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidget;
import lombok.Getter;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.StreamSupport.stream;
import static org.atteo.classindex.ClassIndex.getAnnotated;

public class RenderService {

    public static final int TEXT_BOX_PADDING = 3;

    @Getter
    private LinkedHashSet<AbstractUCUtilsWidget<?>> widgets = new LinkedHashSet<>();

    public Color getSecondaryColor(@NotNull Color color) {
        return new Color(color.getRed() / 2, color.getGreen() / 2, color.getBlue() / 2, 100);
    }

    public void initializeWidgets() {
        this.widgets = stream(getAnnotated(UCUtilsWidget.class).spliterator(), false)
                .map(ucUtilsWidgetClass -> {
                    try {
                        return (AbstractUCUtilsWidget<?>) ucUtilsWidgetClass.getConstructor().newInstance();
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .peek(AbstractUCUtilsWidget::init)
                .sorted(comparing(AbstractUCUtilsWidget::getRegistryName))
                .collect(toCollection(LinkedHashSet::new));
    }

    public <E extends CyclingButtonEntry> void addCyclingButton(@NotNull LinearLayout widget,
                                                                Component name,
                                                                E[] values,
                                                                Function<E, Component> displayNameFunction,
                                                                BiConsumer<Options, E> onValueChange,
                                                                @NotNull Function<Options, E> currentValue,
                                                                int width) {
//        FIXME CyclingButtonWidget<E> cyclingButton = CyclingButtonWidget.builder(displayNameFunction)
//                .values(values)
//                .initially(currentValue.apply(configuration.getOptions()))
//                .tooltip(CyclingButtonEntry::getTooltip)
//                .build(name, (button, value) -> onValueChange.accept(configuration.getOptions(), value));
//
//        cyclingButton.setWidth(width);
//
//        widget.addChild(cyclingButton);
    }

    public void addToggleButton(@NotNull LinearLayout widget,
                                Component name,
                                Component tooltip,
                                BiConsumer<Options, Boolean> onPress,
                                @NotNull Function<Options, Boolean> currentValue,
                                int width) {
        ToggleButtonWidget toggleButton = new ToggleButtonWidget(name, value -> onPress.accept(configuration.getOptions(), value), currentValue.apply(configuration.getOptions()));

        toggleButton.setWidth(width);
        toggleButton.setTooltip(Tooltip.create(tooltip));

        widget.addChild(toggleButton);
    }
}
