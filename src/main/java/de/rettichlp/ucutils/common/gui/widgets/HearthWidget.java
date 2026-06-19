package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsTextWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.awt.Color;

import static java.lang.String.format;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.ChatFormatting.YELLOW;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

@UCUtilsWidget(registryName = "hearth", defaultX = 4.0, defaultY = 4.0, defaultEnabled = false)
public class HearthWidget extends AbstractUCUtilsTextWidget<HearthWidget.Configuration> {

    @Override
    public Component text() {
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null; // cannot be null at this point

        float absorptionAmount = player.getAbsorptionAmount();
        float overallAmount = player.getHealth() + absorptionAmount;

        String overallAmountString = format("%.1f", overallAmount / 2).replaceAll(",0$", "");
        MutableComponent text = literal(overallAmountString).withStyle(absorptionAmount > 0 ? YELLOW : GRAY);
        return text.append(literal("❤").withStyle(RED));
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
    public Component getDisplayName() {
        return translatable("ucutils.options.widgets.hearth.options.name");
    }

    @Override
    public Component getTooltip() {
        return translatable("ucutils.options.widgets.hearth.options.tooltip");
    }

    @AllArgsConstructor
    public static class Configuration extends UCUtilsWidgetConfiguration {}
}
