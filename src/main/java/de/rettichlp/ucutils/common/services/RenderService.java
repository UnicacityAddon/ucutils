package de.rettichlp.ucutils.common.services;

import de.rettichlp.ucutils.common.configuration.options.Options;
import de.rettichlp.ucutils.common.gui.screens.components.CyclingButtonEntry;
import de.rettichlp.ucutils.common.gui.screens.components.ToggleButtonWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidget;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
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
import static net.minecraft.client.font.TextRenderer.TextLayerType.SEE_THROUGH;
import static net.minecraft.util.math.RotationAxis.POSITIVE_Y;
import static org.atteo.classindex.ClassIndex.getAnnotated;

public class RenderService {

    public static final int TEXT_BOX_PADDING = 3;

    @Getter
    private LinkedHashSet<AbstractUCUtilsWidget<?>> widgets = new LinkedHashSet<>();

    public void renderTextAboveEntity(@NotNull MatrixStack matrices,
                                      VertexConsumerProvider vertexConsumers,
                                      @NotNull Entity entity,
                                      Text text,
                                      float scale) {
        renderTextAt(matrices, vertexConsumers, entity.getX(), entity.getY() + 1.35, entity.getZ(), text, scale);
    }

    public void renderTextAt(@NotNull MatrixStack matrices,
                             VertexConsumerProvider vertexConsumers,
                             double x,
                             double y,
                             double z,
                             Text text,
                             float scale) {
        // save the current matrix state
        matrices.push();

        Camera camera = MinecraftClient.getInstance().gameRenderer.getCamera();
        double camX = camera.getPos().x;
        double camY = camera.getPos().y;
        double camZ = camera.getPos().z;

        matrices.translate(x - camX, y - camY, z - camZ);

        // make the text face the camera
        matrices.multiply(camera.getRotation());
        matrices.multiply(POSITIVE_Y.rotationDegrees(180.0F));

        // scale the text down so it's not too big
        matrices.scale(-scale, -scale, scale);

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        // calculate the width of the text to center it
        float textWidth = -textRenderer.getWidth(text) / 2.0F;

        // render the text
        textRenderer.draw(text, textWidth, 0.0F, 0xFFFFFFFF, false, matrices.peek().getPositionMatrix(), vertexConsumers, SEE_THROUGH, 0x55000000, 0xF000F0);

        // restore the previous matrix state
        matrices.pop();
    }

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

    public void addButton(@NotNull DirectionalLayoutWidget widget,
                          Text name,
                          ButtonWidget.PressAction onPress,
                          int width) {
        ButtonWidget buttonWidget = ButtonWidget.builder(name, onPress)
                .build();

        buttonWidget.setWidth(width);

        widget.add(buttonWidget);
    }

    public <E extends CyclingButtonEntry> void addCyclingButton(@NotNull DirectionalLayoutWidget widget,
                                                                Text name,
                                                                E[] values,
                                                                Function<E, Text> displayNameFunction,
                                                                BiConsumer<Options, E> onValueChange,
                                                                @NotNull Function<Options, E> currentValue,
                                                                int width) {
        CyclingButtonWidget<E> cyclingButton = CyclingButtonWidget.builder(displayNameFunction)
                .values(values)
                .initially(currentValue.apply(configuration.getOptions()))
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
