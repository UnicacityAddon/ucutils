package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.configuration.options.SoundOptions;
import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import de.rettichlp.ucutils.common.gui.screens.components.CyclingButtonEntry;
import de.rettichlp.ucutils.common.gui.screens.components.ToggleButtonWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static net.minecraft.client.gui.components.Tooltip.create;
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
        GridLayout gridLayout = this.layout.addToContents(new GridLayout());
        gridLayout.columnSpacing(8).rowSpacing(4);
        GridLayout.RowHelper gridLayoutRowHelper = gridLayout.createRowHelper(2);

        SoundOptions soundOptions = configuration.getOptions().sound();

        CycleButton<SoundOptions.StateSelect> cycleButton1 = CycleButton.builder(SoundOptions.StateSelect::getDisplayName, soundOptions.bankRobbery())
                .withValues(SoundOptions.StateSelect.values())
                .withTooltip(CyclingButtonEntry::getTooltip)
                .create(SOUND_BANK_ROBBERY_NAME, (_, value) -> soundOptions.bankRobbery(value));
        cycleButton1.setWidth(150);
        gridLayoutRowHelper.addChild(cycleButton1);

        CycleButton<SoundOptions.StateSelect> cycleButton2 = CycleButton.builder(SoundOptions.StateSelect::getDisplayName, soundOptions.bomb())
                .withValues(SoundOptions.StateSelect.values())
                .withTooltip(CyclingButtonEntry::getTooltip)
                .create(SOUND_BOMB_NAME, (_, value) -> soundOptions.bomb(value));
        cycleButton2.setWidth(150);
        gridLayoutRowHelper.addChild(cycleButton2);

        ToggleButtonWidget toggleButton1 = new ToggleButtonWidget(SOUND_CONTRACT_SET_NAME, soundOptions::contractSet, soundOptions.contractSet());
        toggleButton1.setWidth(150);
        toggleButton1.setTooltip(create(SOUND_CONTRACT_SET_TOOLTIP));
        gridLayoutRowHelper.addChild(toggleButton1);

        ToggleButtonWidget toggleButton2 = new ToggleButtonWidget(SOUND_CONTRACT_FULFILLED_NAME, soundOptions::contractFulfilled, soundOptions.contractFulfilled());
        toggleButton2.setWidth(150);
        toggleButton2.setTooltip(create(SOUND_CONTRACT_FULFILLED_TOOLTIP));
        gridLayoutRowHelper.addChild(toggleButton2);

        ToggleButtonWidget toggleButton3 = new ToggleButtonWidget(SOUND_SERVICE_NAME, soundOptions::service, soundOptions.service());
        toggleButton3.setWidth(150);
        toggleButton3.setTooltip(create(SOUND_SERVICE_TOOLTIP));
        gridLayoutRowHelper.addChild(toggleButton3);

        CycleButton<SoundOptions.MedicSelect> cycleButton3 = CycleButton.builder(SoundOptions.MedicSelect::getDisplayName, soundOptions.fire())
                .withValues(SoundOptions.MedicSelect.values())
                .withTooltip(CyclingButtonEntry::getTooltip)
                .create(SOUND_FIRE_NAME, (_, value) -> soundOptions.fire(value));
        cycleButton3.setWidth(150);
        gridLayoutRowHelper.addChild(cycleButton3);

        ToggleButtonWidget toggleButton4 = new ToggleButtonWidget(SOUND_REPORT_NAME, soundOptions::report, soundOptions.report());
        toggleButton4.setWidth(150);
        toggleButton4.setTooltip(create(SOUND_REPORT_TOOLTIP));
        gridLayoutRowHelper.addChild(toggleButton4);

        ToggleButtonWidget toggleButton5 = new ToggleButtonWidget(SOUND_NOTIFICATION_NAME, soundOptions::notification, soundOptions.notification());
        toggleButton5.setWidth(150);
        toggleButton5.setTooltip(create(SOUND_NOTIFICATION_TOOLTIP));
        gridLayoutRowHelper.addChild(toggleButton5);

        gridLayout.arrangeElements();
        gridLayout.visitWidgets(this::addRenderableWidget);
    }
}
