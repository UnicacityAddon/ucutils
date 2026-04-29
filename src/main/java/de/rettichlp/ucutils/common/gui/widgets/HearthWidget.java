package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsTextWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import lombok.AllArgsConstructor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.awt.Color;

import static java.lang.String.format;
import static net.minecraft.text.Text.of;
import static net.minecraft.text.Text.translatable;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.Formatting.YELLOW;

@UCUtilsWidget(registryName = "hearth", defaultX = 4.0, defaultY = 4.0, defaultEnabled = false)
public class HearthWidget extends AbstractUCUtilsTextWidget<HearthWidget.Configuration> {

    @Override
    public Text text() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        assert player != null; // cannot be null at this point

        float absorptionAmount = player.getAbsorptionAmount();
        float overallAmount = player.getHealth() + absorptionAmount;

        String overallAmountString = format("%.1f", overallAmount / 2).replaceAll(",0$", "");
        MutableText text = of(overallAmountString).copy().formatted(absorptionAmount > 0 ? YELLOW : GRAY);
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
        return translatable("ucutils.options.widgets.hearth.options.name");
    }

    @Override
    public Text getTooltip() {
        return translatable("ucutils.options.widgets.hearth.options.tooltip");
    }

    @AllArgsConstructor
    public static class Configuration extends UCUtilsWidgetConfiguration {}
}
