package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.text.Text;

import static de.rettichlp.ucutils.UCUtils.renderService;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.horizontal;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.vertical;
import static net.minecraft.text.Text.translatable;

public class NameTagOptionsScreen extends OptionsScreen {

    private static final Text TEXT_NAMETAG = translatable("ucutils.options.text.nametag");
    private static final Text NAMETAG_ADDITIONAL_CONTRACT_NAME = translatable("ucutils.options.nametag.additional.contract.name");
    private static final Text NAMETAG_ADDITIONAL_CONTRACT_TOOLTIP = translatable("ucutils.options.nametag.additional.contract.tooltip");
    private static final Text NAMETAG_ADDITIONAL_HOUSEBAN_NAME = translatable("ucutils.options.nametag.additional.houseban.name");
    private static final Text NAMETAG_ADDITIONAL_HOUSEBAN_TOOLTIP = translatable("ucutils.options.nametag.additional.houseban.tooltip");
    private static final Text NAMETAG_ADDITIONAL_AFK_NAME = translatable("ucutils.options.nametag.additional.afk.name");
    private static final Text NAMETAG_ADDITIONAL_AFK_TOOLTIP = translatable("ucutils.options.nametag.additional.afk.tooltip");
    private static final Text NAMETAG_ADDITIONAL_MEDICAL_INFORMATION_NAME = translatable("ucutils.options.nametag.additional.medical_information.name");
    private static final Text NAMETAG_ADDITIONAL_MEDICAL_INFORMATION_TOOLTIP = translatable("ucutils.options.nametag.additional.medical_information.tooltip");

    public NameTagOptionsScreen(Screen parent) {
        super(parent, TEXT_NAMETAG);
    }

    @Override
    public void initBody() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(vertical().spacing(4));

        DirectionalLayoutWidget directionalLayoutWidget1 = directionalLayoutWidget.add(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget1, NAMETAG_ADDITIONAL_CONTRACT_NAME, NAMETAG_ADDITIONAL_CONTRACT_TOOLTIP, (options, value) -> options.nameTag().additionalContract(value), options -> options.nameTag().additionalContract(), 150);

        DirectionalLayoutWidget directionalLayoutWidget2 = directionalLayoutWidget.add(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget2, NAMETAG_ADDITIONAL_HOUSEBAN_NAME, NAMETAG_ADDITIONAL_HOUSEBAN_TOOLTIP, (options, value) -> options.nameTag().additionalHouseban(value), options -> options.nameTag().additionalHouseban(), 150);

        DirectionalLayoutWidget directionalLayoutWidget3 = directionalLayoutWidget.add(horizontal().spacing(8));
        renderService.addToggleButton(directionalLayoutWidget3, NAMETAG_ADDITIONAL_AFK_NAME, NAMETAG_ADDITIONAL_AFK_TOOLTIP, (options, value) -> options.nameTag().additionalAfk(value), options -> options.nameTag().additionalAfk(), 150);
        renderService.addToggleButton(directionalLayoutWidget3, NAMETAG_ADDITIONAL_MEDICAL_INFORMATION_NAME, NAMETAG_ADDITIONAL_MEDICAL_INFORMATION_TOOLTIP, (options, value) -> options.nameTag().additionalMedicalInformation(value), options -> options.nameTag().additionalMedicalInformation(), 150);

        directionalLayoutWidget.forEachChild(this::addDrawableChild);
    }
}
