package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.configuration.options.Options;
import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import de.rettichlp.ucutils.common.gui.screens.components.CyclingButtonEntry;
import de.rettichlp.ucutils.common.models.Color;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.text.Text;

import static de.rettichlp.ucutils.UCUtils.renderService;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.horizontal;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.vertical;
import static net.minecraft.text.Text.translatable;

public class MainOptionsScreen extends OptionsScreen {

    private static final Text TEXT_CAR = translatable("ucutils.options.text.car");
    private static final Text TEXT_NAMETAG = translatable("ucutils.options.text.nametag");
    private static final Text TEXT_PERSONAL_USE = translatable("ucutils.options.text.personal_use");
    private static final Text TEXT_WIDGETS = translatable("ucutils.options.text.widgets");
    private static final Text TEXT_SOUNDS = translatable("ucutils.options.text.sounds");
    private static final Text REINFORCEMENT_STYLE_NAME = translatable("ucutils.options.reinforcement_style.name");
    private static final Text BANK_INFORMATION_NAME = translatable("ucutils.options.atm_information.name");
    private static final Text HYDRATION_NAME = translatable("ucutils.options.hydration.name");
    private static final Text HYDRATION_TOOLTIP = translatable("ucutils.options.hydration.tooltip");
    private static final Text CHECK_UNICACITY_SERVER_NAME = translatable("ucutils.options.check_unicacity_server.name");
    private static final Text CHECK_UNICACITY_SERVER_TOOLTIP = translatable("ucutils.options.check_unicacity_server.tooltip");
    private static final Text FACTION_COLOR_PRIMARY = translatable("ucutils.options.faction_color_primary.name");
    private static final Text FACTION_COLOR_SECONDARY = translatable("ucutils.options.faction_color_secondary.name");

    public MainOptionsScreen() {
        super(new GameMenuScreen(true));
    }

    @Override
    public void initBody() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(vertical().spacing(4));

        renderService.addCyclingButton(directionalLayoutWidget, REINFORCEMENT_STYLE_NAME, Options.ReinforcementType.values(), Options.ReinforcementType::getDisplayName, Options::reinforcementType, Options::reinforcementType, 308);

        DirectionalLayoutWidget directionalLayoutWidget1 = directionalLayoutWidget.add(horizontal().spacing(8));
        renderService.addButton(directionalLayoutWidget1, TEXT_NAMETAG, button -> this.client.setScreen(new NameTagOptionsScreen(this)), 150);
        renderService.addButton(directionalLayoutWidget1, TEXT_PERSONAL_USE, button -> this.client.setScreen(new PersonalUseOptionsScreen(this)), 150);

        DirectionalLayoutWidget directionalLayoutWidget2 = directionalLayoutWidget.add(horizontal().spacing(8));
        renderService.addButton(directionalLayoutWidget2, TEXT_CAR, button -> this.client.setScreen(new CarOptionsScreen(this)), 150);
        renderService.addButton(directionalLayoutWidget2, TEXT_SOUNDS, button -> this.client.setScreen(new SoundOptionsScreen(this)), 150);

        DirectionalLayoutWidget directionalLayoutWidget3 = directionalLayoutWidget.add(horizontal().spacing(8));
        renderService.addButton(directionalLayoutWidget3, TEXT_WIDGETS, button -> this.client.setScreen(new WidgetOptionsScreen(this)), 150);
        renderService.addCyclingButton(directionalLayoutWidget3, BANK_INFORMATION_NAME, Options.AtmInformationType.values(), Options.AtmInformationType::getDisplayName, Options::atmInformationType, Options::atmInformationType, 150);

        DirectionalLayoutWidget directionalLayoutWidget4 = directionalLayoutWidget.add(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget4, HYDRATION_NAME, HYDRATION_TOOLTIP, Options::showHydration, Options::showHydration, 150);
        renderService.addToggleButton(directionalLayoutWidget4, CHECK_UNICACITY_SERVER_NAME, CHECK_UNICACITY_SERVER_TOOLTIP, Options::checkUnicacityServer, Options::checkUnicacityServer, 150);

        DirectionalLayoutWidget directionalLayoutWidget5 = directionalLayoutWidget.add(horizontal().spacing(8));
        renderService.addCyclingButton(directionalLayoutWidget5, FACTION_COLOR_PRIMARY, Color.values(), CyclingButtonEntry::getDisplayName, Options::factionColorPrimary, Options::factionColorPrimary, 150);
        renderService.addCyclingButton(directionalLayoutWidget5, FACTION_COLOR_SECONDARY, Color.values(), CyclingButtonEntry::getDisplayName, Options::factionColorSecondary, Options::factionColorSecondary, 150);

        directionalLayoutWidget.forEachChild(this::addDrawableChild);
    }
}
