package de.rettichlp.pkutils.common.gui.widgets;

import de.rettichlp.pkutils.common.gui.widgets.base.AbstractPKUtilsTextWidget;
import de.rettichlp.pkutils.common.gui.widgets.base.PKUtilsWidget;
import de.rettichlp.pkutils.common.gui.widgets.base.PKUtilsWidgetConfiguration;
import lombok.AllArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

import static java.lang.String.format;
import static net.minecraft.text.Text.of;
import static net.minecraft.text.Text.translatable;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.Formatting.YELLOW;

@PKUtilsWidget(registryName = "hearth", defaultX = 4.0, defaultY = 4.0, defaultEnabled = false)
public class HearthWidget extends AbstractPKUtilsTextWidget<HearthWidget.Configuration> {

    @Override
    public Text text() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null; // cannot be null at this point

        float health = player.getHealth();
        float absorptionAmount = player.getAbsorptionAmount();

        String healthString = healthToString(health);
        MutableText text = of(healthString).copy().formatted(GRAY);

        if (absorptionAmount > 0) {
            String absorptionString = healthToString(absorptionAmount);
            text.append(of(" " + absorptionString).copy().formatted(YELLOW));
        }

        return text.append(of("❤").copy().formatted(RED));
    }

    @Override
    public Color getBorderColor() {
        return new Color(0, 0, 0, 0);
    }

    @Override
    public Color getBackgroundColor() {
        return new Color(0, 0, 0, 0);
    }

    @Override
    public Text getDisplayName() {
        return translatable("pkutils.options.widgets.hearth.options.name");
    }

    @Override
    public Text getTooltip() {
        return translatable("pkutils.options.widgets.hearth.options.tooltip");
    }

    private @NotNull String healthToString(float health) {
        return format("%.1f", health).replaceAll(",0$", "");
    }

    @AllArgsConstructor
    public static class Configuration extends PKUtilsWidgetConfiguration {}
}
