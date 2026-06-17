package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.configuration.options.NameTagOptions;
import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import de.rettichlp.ucutils.common.gui.screens.components.ToggleButtonWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static net.minecraft.network.chat.Component.translatable;

public class NameTagOptionsScreen extends OptionsScreen {

    private static final Component TEXT_NAMETAG = translatable("ucutils.options.text.nametag");
    private static final Component NAMETAG_AFK_NAME = translatable("ucutils.options.nametag.afk.name");
    private static final Component NAMETAG_AFK_TOOLTIP = translatable("ucutils.options.nametag.afk.tooltip");
    private static final Component NAMETAG_MEDICAL_INFORMATION_NAME = translatable("ucutils.options.nametag.medical_information.name");
    private static final Component NAMETAG_MEDICAL_INFORMATION_TOOLTIP = translatable("ucutils.options.nametag.medical_information.tooltip");

    public NameTagOptionsScreen(Screen parent) {
        super(parent, TEXT_NAMETAG);
    }

    @Override
    public void initBody() {
        GridLayout gridLayout = this.layout.addToContents(new GridLayout());
        gridLayout.columnSpacing(8).rowSpacing(4);
        GridLayout.RowHelper gridLayoutRowHelper = gridLayout.createRowHelper(2);

        NameTagOptions nameTagOptions = configuration.getOptions().nameTag();

        ToggleButtonWidget toggleButton1 = new ToggleButtonWidget(NAMETAG_AFK_NAME, nameTagOptions::afk, nameTagOptions.afk());
        toggleButton1.setWidth(150);
        toggleButton1.setTooltip(Tooltip.create(NAMETAG_AFK_TOOLTIP));
        gridLayoutRowHelper.addChild(toggleButton1);

        ToggleButtonWidget toggleButton2 = new ToggleButtonWidget(NAMETAG_MEDICAL_INFORMATION_NAME, nameTagOptions::medicalInformation, nameTagOptions.medicalInformation());
        toggleButton2.setWidth(150);
        toggleButton2.setTooltip(Tooltip.create(NAMETAG_MEDICAL_INFORMATION_TOOLTIP));
        gridLayoutRowHelper.addChild(toggleButton2);

        gridLayout.arrangeElements();
        gridLayout.visitWidgets(this::addRenderableWidget);
    }
}
