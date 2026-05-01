package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.configuration.options.SoundOptions;
import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.text.Text;

import static de.rettichlp.ucutils.UCUtils.renderService;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.horizontal;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.vertical;
import static net.minecraft.text.Text.translatable;

public class SoundOptionsScreen extends OptionsScreen {

    private static final Text TEXT_SOUND = translatable("ucutils.options.text.sounds");
    private static final Text SOUND_BANK_ROBBERY_NAME = translatable("ucutils.sound.bank_robbery.name");
    private static final Text SOUND_BOMB_NAME = translatable("ucutils.sound.bomb.name");
    private static final Text SOUND_CONTRACT_SET_NAME = translatable("ucutils.sound.contract_set.name");
    private static final Text SOUND_CONTRACT_SET_TOOLTIP = translatable("ucutils.sound.contract_set.tooltip");
    private static final Text SOUND_CONTRACT_FULFILLED_NAME = translatable("ucutils.sound.contract_fulfilled.name");
    private static final Text SOUND_CONTRACT_FULFILLED_TOOLTIP = translatable("ucutils.sound.contract_fulfilled.tooltip");
    private static final Text SOUND_SERVICE_NAME = translatable("ucutils.sound.service.name");
    private static final Text SOUND_SERVICE_TOOLTIP = translatable("ucutils.sound.service.tooltip");
    private static final Text SOUND_FIRE_NAME = translatable("ucutils.sound.fire.name");
    private static final Text TEXT_REPORT_NAME = translatable("ucutils.sound.report.name");
    private static final Text TEXT_REPORT_TOOLTIP = translatable("ucutils.sound.report.tooltip");

    public SoundOptionsScreen(Screen parent) {
        super(parent, TEXT_SOUND);
    }

    @Override
    public void initBody() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(vertical().spacing(4));

        DirectionalLayoutWidget directionalLayoutWidget1 = directionalLayoutWidget.add(horizontal().spacing(8));
        renderService.addCyclingButton(directionalLayoutWidget1, SOUND_BANK_ROBBERY_NAME, SoundOptions.StateSelect.values(), SoundOptions.StateSelect::getDisplayName, (options, e) -> options.sound().bankRobbery(e), options -> options.sound().bankRobbery(), 150);
        renderService.addCyclingButton(directionalLayoutWidget1, SOUND_BOMB_NAME, SoundOptions.StateSelect.values(), SoundOptions.StateSelect::getDisplayName, (options, e) -> options.sound().bomb(e), options -> options.sound().bomb(), 150);

        DirectionalLayoutWidget directionalLayoutWidget2 = directionalLayoutWidget.add(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget2, SOUND_CONTRACT_SET_NAME, SOUND_CONTRACT_SET_TOOLTIP, (options, value) -> options.sound().contractSet(value), options -> options.sound().contractSet(), 150);
        renderService.addToggleButton(directionalLayoutWidget2, SOUND_CONTRACT_FULFILLED_NAME, SOUND_CONTRACT_FULFILLED_TOOLTIP, (options, value) -> options.sound().contractFulfilled(value), options -> options.sound().contractFulfilled(), 150);

        DirectionalLayoutWidget directionalLayoutWidget3 = directionalLayoutWidget.add(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget3, SOUND_SERVICE_NAME, SOUND_SERVICE_TOOLTIP, (options, value) -> options.sound().service(value), options -> options.sound().service(), 150);
        renderService.addCyclingButton(directionalLayoutWidget3, SOUND_FIRE_NAME, SoundOptions.MedicSelect.values(), SoundOptions.MedicSelect::getDisplayName, (options, e) -> options.sound().fire(e), options -> options.sound().fire(), 150);

        DirectionalLayoutWidget directionalLayoutWidget4 = directionalLayoutWidget.add(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget4, TEXT_REPORT_NAME, TEXT_REPORT_TOOLTIP, (options, value) -> options.sound().report(value), options -> options.sound().report(), 150);

        directionalLayoutWidget.forEachChild(this::addDrawableChild);
    }
}
