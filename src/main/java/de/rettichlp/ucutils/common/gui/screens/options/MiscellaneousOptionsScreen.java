package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.configuration.options.MiscellaneousOptions;
import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import de.rettichlp.ucutils.common.gui.screens.components.ToggleButtonWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.renderService;
import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.client.gui.layouts.LinearLayout.horizontal;
import static net.minecraft.client.gui.layouts.LinearLayout.vertical;
import static net.minecraft.network.chat.CommonComponents.NEW_LINE;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.util.CommonColors.RED;

public class MiscellaneousOptionsScreen extends OptionsScreen {

    private static final Component TEXT_MISCELLANEOUS = translatable("ucutils.options.text.miscellaneous");
    private static final Component HYDRATION_NAME = translatable("ucutils.options.hydration.name");
    private static final Component HYDRATION_TOOLTIP = translatable("ucutils.options.hydration.tooltip");
    private static final Component BANK_INFORMATION_NAME = translatable("ucutils.options.atm_information.name");
    private static final Component AUTO_TRASH_CAN_NAME = translatable("ucutils.options.auto_trash_can.name");
    private static final Component AUTO_TRASH_CAN_TOOLTIP = translatable("ucutils.options.auto_trash_can.tooltip");
    private static final Component HIGHLIGHT_CORPSES_NAME = translatable("ucutils.options.highlight_corpses.name");
    private static final Component HIGHLIGHT_CORPSES_TOOLTIP = translatable("ucutils.options.highlight_corpses.tooltip").append(NEW_LINE).append(translatable("ucutils.feature_disabled").withColor(RED));
    private static final Component HIDE_DOLPHINS_NAME = translatable("ucutils.options.hide_dolphins.name");
    private static final Component HIDE_DOLPHINS_TOOLTIP = translatable("ucutils.options.hide_dolphins.tooltip");
    private static final Component BLOCK_MALLE_SOUND_NAME = translatable("ucutils.options.block_malle_sound.name");
    private static final Component BLOCK_MALLE_SOUND_TOOLTIP = translatable("ucutils.options.block_malle_sound.tooltip");

    public MiscellaneousOptionsScreen(Screen parent) {
        super(parent, TEXT_MISCELLANEOUS);
    }

    @Override
    public void initBody() {
        LinearLayout directionalLayoutWidget = this.layout.addToContents(vertical().spacing(4));

        LinearLayout directionalLayoutWidget1 = directionalLayoutWidget.addChild(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget1, HYDRATION_NAME, HYDRATION_TOOLTIP, (options, value) -> options.miscellaneous().showHydration(value), options -> options.miscellaneous().showHydration(), 150);
        renderService.addCyclingButton(directionalLayoutWidget1, BANK_INFORMATION_NAME, MiscellaneousOptions.AtmInformationType.values(), MiscellaneousOptions.AtmInformationType::getDisplayName, (options, value) -> options.miscellaneous().atmInformationType(value), options -> options.miscellaneous().atmInformationType(), 150);

        LinearLayout directionalLayoutWidget2 = directionalLayoutWidget.addChild(horizontal().spacing(8));
        directionalLayoutWidget2.addChild(Button.builder(AUTO_TRASH_CAN_NAME, _ -> this.minecraft.gui.setScreen(new TrashCanOptionsScreen(this))).tooltip(create(AUTO_TRASH_CAN_TOOLTIP)).width(150).build());
        ToggleButtonWidget toggleButtonWidget = renderService.addToggleButton(directionalLayoutWidget2, HIGHLIGHT_CORPSES_NAME, HIGHLIGHT_CORPSES_TOOLTIP, (options, value) -> options.miscellaneous().highlightCorpses(value), options -> options.miscellaneous().highlightCorpses(), 150);
        toggleButtonWidget.active = commandService.isSuperUser();

        LinearLayout directionalLayoutWidget3 = directionalLayoutWidget.addChild(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget3, HIDE_DOLPHINS_NAME, HIDE_DOLPHINS_TOOLTIP, (options, value) -> options.miscellaneous().hideDolphins(value), options -> options.miscellaneous().hideDolphins(), 150);
        renderService.addToggleButton(directionalLayoutWidget3, BLOCK_MALLE_SOUND_NAME, BLOCK_MALLE_SOUND_TOOLTIP, (options, value) -> options.miscellaneous().blockMalleSound(value), options -> options.miscellaneous().blockMalleSound(), 150);

        directionalLayoutWidget.visitWidgets(this::addRenderableWidget);
    }
}
