package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.configuration.options.SoundOptions;
import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static de.rettichlp.ucutils.UCUtils.renderService;
import static net.minecraft.client.gui.layouts.LinearLayout.horizontal;
import static net.minecraft.client.gui.layouts.LinearLayout.vertical;
import static net.minecraft.network.chat.Component.translatable;

public class SoundOptionsScreen extends OptionsScreen {

    private static final Component TEXT_SOUND = translatable("ucutils.options.text.sounds");
    private static final Component SOUND_BANK_ROBBERY_NAME = translatable("ucutils.sound.bank_robbery.name");
    private static final Component SOUND_BOMB_NAME = translatable("ucutils.sound.bomb.name");
    private static final Component SOUND_CONTRACT_SET_NAME = translatable("ucutils.sound.contract_set.name");
    private static final Component SOUND_CONTRACT_SET_TOOLTIP = translatable("ucutils.sound.contract_set.tooltip");
    private static final Component SOUND_CONTRACT_FULFILLED_NAME = translatable("ucutils.sound.contract_fulfilled.name");
    private static final Component SOUND_CONTRACT_FULFILLED_TOOLTIP = translatable("ucutils.sound.contract_fulfilled.tooltip");
    private static final Component SOUND_SERVICE_NAME = translatable("ucutils.sound.service.name");
    private static final Component SOUND_SERVICE_TOOLTIP = translatable("ucutils.sound.service.tooltip");
    private static final Component SOUND_FIRE_NAME = translatable("ucutils.sound.fire.name");
    private static final Component SOUND_REPORT_NAME = translatable("ucutils.sound.report.name");
    private static final Component SOUND_REPORT_TOOLTIP = translatable("ucutils.sound.report.tooltip");
    private static final Component SOUND_NOTIFICATION_NAME = translatable("ucutils.sound.notification.name");
    private static final Component SOUND_NOTIFICATION_TOOLTIP = translatable("ucutils.sound.notification.tooltip");

    public SoundOptionsScreen(Screen parent) {
        super(parent, TEXT_SOUND);
    }

    @Override
    public void initBody() {
        LinearLayout directionalLayoutWidget = this.layout.addToContents(vertical().spacing(4));

        LinearLayout directionalLayoutWidget1 = directionalLayoutWidget.addChild(horizontal().spacing(8));
        renderService.addCyclingButton(directionalLayoutWidget1, SOUND_BANK_ROBBERY_NAME, SoundOptions.StateSelect.values(), SoundOptions.StateSelect::getDisplayName, (options, e) -> options.sound().bankRobbery(e), options -> options.sound().bankRobbery(), 150);
        renderService.addCyclingButton(directionalLayoutWidget1, SOUND_BOMB_NAME, SoundOptions.StateSelect.values(), SoundOptions.StateSelect::getDisplayName, (options, e) -> options.sound().bomb(e), options -> options.sound().bomb(), 150);

        LinearLayout directionalLayoutWidget2 = directionalLayoutWidget.addChild(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget2, SOUND_CONTRACT_SET_NAME, SOUND_CONTRACT_SET_TOOLTIP, (options, value) -> options.sound().contractSet(value), options -> options.sound().contractSet(), 150);
        renderService.addToggleButton(directionalLayoutWidget2, SOUND_CONTRACT_FULFILLED_NAME, SOUND_CONTRACT_FULFILLED_TOOLTIP, (options, value) -> options.sound().contractFulfilled(value), options -> options.sound().contractFulfilled(), 150);

        LinearLayout directionalLayoutWidget3 = directionalLayoutWidget.addChild(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget3, SOUND_SERVICE_NAME, SOUND_SERVICE_TOOLTIP, (options, value) -> options.sound().service(value), options -> options.sound().service(), 150);
        renderService.addCyclingButton(directionalLayoutWidget3, SOUND_FIRE_NAME, SoundOptions.MedicSelect.values(), SoundOptions.MedicSelect::getDisplayName, (options, e) -> options.sound().fire(e), options -> options.sound().fire(), 150);

        LinearLayout directionalLayoutWidget4 = directionalLayoutWidget.addChild(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget4, SOUND_REPORT_NAME, SOUND_REPORT_TOOLTIP, (options, value) -> options.sound().report(value), options -> options.sound().report(), 150);
        renderService.addToggleButton(directionalLayoutWidget4, SOUND_NOTIFICATION_NAME, SOUND_NOTIFICATION_TOOLTIP, (options, value) -> options.sound().notification(value), options -> options.sound().notification(), 150);

        directionalLayoutWidget.visitWidgets(this::addRenderableWidget);
    }
}
