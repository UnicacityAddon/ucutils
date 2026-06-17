package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.ucutils.common.configuration.options.Options;
import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import de.rettichlp.ucutils.common.gui.screens.components.CyclingButtonEntry;
import de.rettichlp.ucutils.common.models.Color;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static de.rettichlp.ucutils.UCUtils.renderService;
import static net.minecraft.client.gui.layouts.LinearLayout.vertical;
import static net.minecraft.network.chat.Component.translatable;

public class ChatOptionsScreen extends OptionsScreen {

    private static final Component TEXT_CHAT = translatable("ucutils.options.text.chat");
    private static final Component TEXT_FACTION = translatable("ucutils.options.text.faction");
    private static final Component FACTION_COLOR_NAME = translatable("ucutils.options.faction_chat_color.name");
    private static final Component FACTION_COLOR_TOOLTIP = translatable("ucutils.options.faction_chat_color.tooltip");
    private static final Component FACTION_COLOR_PRIMARY = translatable("ucutils.options.faction_chat_color_primary.name");
    private static final Component FACTION_COLOR_SECONDARY = translatable("ucutils.options.faction_chat_color_secondary.name");

    public ChatOptionsScreen(Screen parent) {
        super(parent, TEXT_CHAT);
    }

    @Override
    public void initBody() {
        LinearLayout directionalLayoutWidget = this.layout.addToContents(vertical().spacing(4));

        directionalLayoutWidget.addChild(new StringWidget(TEXT_FACTION, this.font), LayoutSettings::alignHorizontallyCenter);

        renderService.addToggleButton(directionalLayoutWidget, FACTION_COLOR_NAME, FACTION_COLOR_TOOLTIP, Options::changeFactionChatColor, Options::changeFactionChatColor, 308);
        renderService.addCyclingButton(directionalLayoutWidget, FACTION_COLOR_PRIMARY, Color.values(), CyclingButtonEntry::getDisplayName, Options::factionChatColorPrimary, Options::factionChatColorPrimary, 308);
        renderService.addCyclingButton(directionalLayoutWidget, FACTION_COLOR_SECONDARY, Color.values(), CyclingButtonEntry::getDisplayName, Options::factionChatColorSecondary, Options::factionChatColorSecondary, 308);

        directionalLayoutWidget.visitWidgets(this::addRenderableWidget);
    }
}
