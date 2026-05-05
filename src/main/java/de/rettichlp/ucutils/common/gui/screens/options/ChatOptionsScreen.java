package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.configuration.options.Options;
import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import de.rettichlp.ucutils.common.gui.screens.components.CyclingButtonEntry;
import de.rettichlp.ucutils.common.models.Color;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import static de.rettichlp.ucutils.UCUtils.renderService;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.vertical;
import static net.minecraft.text.Text.translatable;

public class ChatOptionsScreen extends OptionsScreen {

    private static final Text TEXT_CHAT = translatable("ucutils.options.text.chat");
    private static final Text TEXT_FACTION = translatable("ucutils.options.text.faction");
    private static final Text FACTION_COLOR_NAME = translatable("ucutils.options.faction_chat_color.name");
    private static final Text FACTION_COLOR_TOOLTIP = translatable("ucutils.options.faction_chat_color.tooltip");
    private static final Text FACTION_COLOR_PRIMARY = translatable("ucutils.options.faction_chat_color_primary.name");
    private static final Text FACTION_COLOR_SECONDARY = translatable("ucutils.options.faction_chat_color_secondary.name");

    public ChatOptionsScreen(Screen parent) {
        super(parent, TEXT_CHAT);
    }

    @Override
    public void initBody() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(vertical().spacing(4));

        directionalLayoutWidget.add(new TextWidget(TEXT_FACTION, this.textRenderer), Positioner::alignHorizontalCenter);

        renderService.addToggleButton(directionalLayoutWidget, FACTION_COLOR_NAME, FACTION_COLOR_TOOLTIP, Options::changeFactionChatColor, Options::changeFactionChatColor, 308);
        renderService.addCyclingButton(directionalLayoutWidget, FACTION_COLOR_PRIMARY, Color.values(), CyclingButtonEntry::getDisplayName, Options::factionChatColorPrimary, Options::factionChatColorPrimary, 308);
        renderService.addCyclingButton(directionalLayoutWidget, FACTION_COLOR_SECONDARY, Color.values(), CyclingButtonEntry::getDisplayName, Options::factionChatColorSecondary, Options::factionChatColorSecondary, 308);

        directionalLayoutWidget.forEachChild(this::addDrawableChild);
    }
}
