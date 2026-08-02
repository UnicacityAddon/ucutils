package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.therettingtoncompanion.gui.screens.TRCOptionsScreen;
import de.rettichlp.ucutils.common.configuration.options.Options;
import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;

import static de.rettichlp.ucutils.UCUtils.renderService;
import static net.minecraft.client.gui.layouts.LinearLayout.horizontal;
import static net.minecraft.client.gui.layouts.LinearLayout.vertical;
import static net.minecraft.network.chat.Component.translatable;

public class MainOptionsScreen extends OptionsScreen {

    private static final Component CHECK_UNICACITY_SERVER_NAME = translatable("ucutils.options.check_unicacity_server.name");
    private static final Component CHECK_UNICACITY_SERVER_TOOLTIP = translatable("ucutils.options.check_unicacity_server.tooltip");
    private static final Component REINFORCEMENT_STYLE_NAME = translatable("ucutils.options.reinforcement_style.name");
    private static final Component TEXT_NAMETAG = translatable("ucutils.options.text.nametag");
    private static final Component TEXT_CHAT = translatable("ucutils.options.text.chat");
    private static final Component TEXT_CAR = translatable("ucutils.options.text.car");
    private static final Component TEXT_SOUNDS = translatable("ucutils.options.text.sounds");
    private static final Component TEXT_WIDGETS = translatable("ucutils.options.text.widgets");
    private static final Component TEXT_NOTIFICATIONS = translatable("ucutils.options.text.notifications");
    private static final Component HYDRATION_NAME = translatable("ucutils.options.hydration.name");
    private static final Component HYDRATION_TOOLTIP = translatable("ucutils.options.hydration.tooltip");
    private static final Component BANK_INFORMATION_NAME = translatable("ucutils.options.atm_information.name");
    private static final Component AUTO_TRASH_CAN_NAME = translatable("ucutils.options.auto_trash_can.name");
    private static final Component AUTO_TRASH_CAN_TOOLTIP = translatable("ucutils.options.auto_trash_can.tooltip");

    public MainOptionsScreen() {
        super(new PauseScreen(true));
    }

    @Override
    public void initBody() {
        LinearLayout directionalLayoutWidget = this.layout.addToContents(vertical().spacing(4));

        renderService.addToggleButton(directionalLayoutWidget, CHECK_UNICACITY_SERVER_NAME, CHECK_UNICACITY_SERVER_TOOLTIP, Options::checkUnicacityServer, Options::checkUnicacityServer, 308);
        renderService.addCyclingButton(directionalLayoutWidget, REINFORCEMENT_STYLE_NAME, Options.ReinforcementType.values(), Options.ReinforcementType::getDisplayName, Options::reinforcementType, Options::reinforcementType, 308);

        LinearLayout directionalLayoutWidget1 = directionalLayoutWidget.addChild(horizontal().spacing(8));
        directionalLayoutWidget1.addChild(Button.builder(TEXT_NAMETAG, _ -> this.minecraft.gui.setScreen(new NameTagOptionsScreen(this))).width(150).build());
        directionalLayoutWidget1.addChild(Button.builder(TEXT_CHAT, _ -> this.minecraft.gui.setScreen(new ChatOptionsScreen(this))).width(150).build());

        LinearLayout directionalLayoutWidget2 = directionalLayoutWidget.addChild(horizontal().spacing(8));
        directionalLayoutWidget2.addChild(Button.builder(TEXT_CAR, _ -> this.minecraft.gui.setScreen(new CarOptionsScreen(this))).width(150).build());
        directionalLayoutWidget2.addChild(Button.builder(TEXT_SOUNDS, _ -> this.minecraft.gui.setScreen(new SoundOptionsScreen(this))).width(150).build());

        LinearLayout directionalLayoutWidget3 = directionalLayoutWidget.addChild(horizontal().spacing(8));
        directionalLayoutWidget3.addChild(Button.builder(TEXT_WIDGETS, _ -> this.minecraft.gui.setScreen(new TRCOptionsScreen("widgets", this, true))).width(150).build());
        directionalLayoutWidget3.addChild(Button.builder(TEXT_NOTIFICATIONS, _ -> this.minecraft.gui.setScreen(new NotificationOptionsScreen(this))).width(150).build());

        LinearLayout directionalLayoutWidget4 = directionalLayoutWidget.addChild(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget4, HYDRATION_NAME, HYDRATION_TOOLTIP, Options::showHydration, Options::showHydration, 150);
        renderService.addCyclingButton(directionalLayoutWidget4, BANK_INFORMATION_NAME, Options.AtmInformationType.values(), Options.AtmInformationType::getDisplayName, Options::atmInformationType, Options::atmInformationType, 150);

        LinearLayout directionalLayoutWidget5 = directionalLayoutWidget.addChild(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget5, AUTO_TRASH_CAN_NAME, AUTO_TRASH_CAN_TOOLTIP, Options::autoCollectChestsFromTrashCans, Options::autoCollectChestsFromTrashCans, 150);

        directionalLayoutWidget.visitWidgets(this::addRenderableWidget);
    }
}
