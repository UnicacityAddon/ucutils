package de.rettichlp.ucutils.common.services;

import de.rettichlp.ucutils.common.configuration.options.Options;
import de.rettichlp.ucutils.common.gui.screens.components.CyclingButtonEntry;
import de.rettichlp.ucutils.common.gui.screens.components.ToggleButtonWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidget;
import lombok.Getter;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.text.Text;
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

    public <E extends CyclingButtonEntry> void addCyclingButton(@NotNull DirectionalLayoutWidget widget,
                                                                Text name,
                                                                E[] values,
                                                                Function<E, Text> displayNameFunction,
                                                                BiConsumer<Options, E> onValueChange,
                                                                @NotNull Function<Options, E> currentValue,
                                                                int width) {
        E initialValue = currentValue.apply(configuration.getOptions());
        CyclingButtonWidget<E> cyclingButton = CyclingButtonWidget.builder(displayNameFunction, initialValue)
                .values(values)
                .tooltip(CyclingButtonEntry::getTooltip)
                .build(name, (button, value) -> onValueChange.accept(configuration.getOptions(), value));

        cyclingButton.setWidth(width);

        widget.add(cyclingButton);
    }

    public void addToggleButton(@NotNull DirectionalLayoutWidget widget,
                                Text name,
                                Text tooltip,
                                BiConsumer<Options, Boolean> onPress,
                                @NotNull Function<Options, Boolean> currentValue,
                                int width) {
        ToggleButtonWidget toggleButton = new ToggleButtonWidget(name, value -> onPress.accept(configuration.getOptions(), value), currentValue.apply(configuration.getOptions()));

        toggleButton.setWidth(width);
        toggleButton.setTooltip(Tooltip.of(tooltip));

        widget.add(toggleButton);
    }
}
