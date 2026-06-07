package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.configuration.options.NameTagOptions;
import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import de.rettichlp.ucutils.common.gui.screens.components.ToggleButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static net.minecraft.text.Text.translatable;

public class NameTagOptionsScreen extends OptionsScreen {

    private static final Text TEXT_NAMETAG = translatable("ucutils.options.text.nametag");
    private static final Text NAMETAG_AFK_NAME = translatable("ucutils.options.nametag.afk.name");
    private static final Text NAMETAG_AFK_TOOLTIP = translatable("ucutils.options.nametag.afk.tooltip");
    private static final Text NAMETAG_MEDICAL_INFORMATION_NAME = translatable("ucutils.options.nametag.medical_information.name");
    private static final Text NAMETAG_MEDICAL_INFORMATION_TOOLTIP = translatable("ucutils.options.nametag.medical_information.tooltip");

    public NameTagOptionsScreen(Screen parent) {
        super(parent, TEXT_NAMETAG);
    }

    @Override
    public void initBody() {
        GridWidget gridWidget = this.layout.addBody(new GridWidget());
        gridWidget.setColumnSpacing(8).setRowSpacing(4);
        GridWidget.Adder gridWidgetAdder = gridWidget.createAdder(2);

        NameTagOptions nameTagOptions = configuration.getOptions().nameTag();

        ToggleButtonWidget toggleButton1 = new ToggleButtonWidget(NAMETAG_AFK_NAME, nameTagOptions::afk, nameTagOptions.afk());
        toggleButton1.setWidth(150);
        toggleButton1.setTooltip(Tooltip.of(NAMETAG_AFK_TOOLTIP));
        gridWidgetAdder.add(toggleButton1);

        ToggleButtonWidget toggleButton2 = new ToggleButtonWidget(NAMETAG_MEDICAL_INFORMATION_NAME, nameTagOptions::medicalInformation, nameTagOptions.medicalInformation());
        toggleButton2.setWidth(150);
        toggleButton2.setTooltip(Tooltip.of(NAMETAG_MEDICAL_INFORMATION_TOOLTIP));
        gridWidgetAdder.add(toggleButton2);

        gridWidget.refreshPositions();
        gridWidget.forEachChild(this::addDrawableChild);
    }
}
