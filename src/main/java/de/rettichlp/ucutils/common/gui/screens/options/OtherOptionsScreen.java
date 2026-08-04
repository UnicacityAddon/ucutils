package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.configuration.options.OtherOptions;
import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import de.rettichlp.ucutils.common.gui.screens.components.ToggleButtonWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.renderService;
import static net.minecraft.client.gui.layouts.LinearLayout.horizontal;
import static net.minecraft.client.gui.layouts.LinearLayout.vertical;
import static net.minecraft.network.chat.CommonComponents.NEW_LINE;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.util.CommonColors.RED;

public class OtherOptionsScreen extends OptionsScreen {

    private static final Component TEXT_OTHER = translatable("ucutils.options.text.other");
    private static final Component HYDRATION_NAME = translatable("ucutils.options.hydration.name");
    private static final Component HYDRATION_TOOLTIP = translatable("ucutils.options.hydration.tooltip");
    private static final Component BANK_INFORMATION_NAME = translatable("ucutils.options.atm_information.name");
    private static final Component AUTO_TRASH_CAN_NAME = translatable("ucutils.options.auto_trash_can.name");
    private static final Component AUTO_TRASH_CAN_TOOLTIP = translatable("ucutils.options.auto_trash_can.tooltip");
    private static final Component HIGHLIGHT_CORPSES_NAME = translatable("ucutils.options.highlight_corpses.name");
    private static final Component HIGHLIGHT_CORPSES_TOOLTIP = translatable("ucutils.options.highlight_corpses.tooltip").append(NEW_LINE).append(translatable("ucutils.feature_disabled").withColor(RED));
    private static final Component HIDE_DOLPHINS_NAME = translatable("ucutils.options.hide_dolphins.name");
    private static final Component HIDE_DOLPHINS_TOOLTIP = translatable("ucutils.options.hide_dolphins.tooltip");

    public OtherOptionsScreen(Screen parent) {
        super(parent, TEXT_OTHER);
    }

    @Override
    public void initBody() {
        LinearLayout directionalLayoutWidget = this.layout.addToContents(vertical().spacing(4));

        LinearLayout directionalLayoutWidget1 = directionalLayoutWidget.addChild(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget1, HYDRATION_NAME, HYDRATION_TOOLTIP, (options, value) -> options.other().showHydration(value), options -> options.other().showHydration(), 150);
        renderService.addCyclingButton(directionalLayoutWidget1, BANK_INFORMATION_NAME, OtherOptions.AtmInformationType.values(), OtherOptions.AtmInformationType::getDisplayName, (options, value) -> options.other().atmInformationType(value), options -> options.other().atmInformationType(), 150);

        LinearLayout directionalLayoutWidget2 = directionalLayoutWidget.addChild(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget2, AUTO_TRASH_CAN_NAME, AUTO_TRASH_CAN_TOOLTIP, (options, value) -> options.other().autoCollectChestsFromTrashCans(value), options -> options.other().autoCollectChestsFromTrashCans(), 150);
        ToggleButtonWidget toggleButtonWidget = renderService.addToggleButton(directionalLayoutWidget2, HIGHLIGHT_CORPSES_NAME, HIGHLIGHT_CORPSES_TOOLTIP, (options, value) -> options.other().highlightCorpses(value), options -> options.other().highlightCorpses(), 150);
        toggleButtonWidget.active = commandService.isSuperUser();

        LinearLayout directionalLayoutWidget3 = directionalLayoutWidget.addChild(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget3, HIDE_DOLPHINS_NAME, HIDE_DOLPHINS_TOOLTIP, (options, value) -> options.other().hideDolphins(value), options -> options.other().hideDolphins(), 150);

        directionalLayoutWidget.visitWidgets(this::addRenderableWidget);
    }
}
