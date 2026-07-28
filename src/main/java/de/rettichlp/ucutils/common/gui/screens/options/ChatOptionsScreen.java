package de.rettichlp.ucutils.common.gui.screens.options;

import de.rettichlp.therettingtoncompanion.gui.ColorButton;
import de.rettichlp.therettingtoncompanion.gui.screens.ColorSelectionPopupScreen;
import de.rettichlp.ucutils.common.configuration.options.Options;
import de.rettichlp.ucutils.common.gui.screens.OptionsScreen;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.awt.Color;

import static de.rettichlp.ucutils.UCUtils.configuration;
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

        Color factionChatPrimaryColor = new Color(configuration.getOptions().chatOptions().factionChatPrimaryColorValue());
        ColorButton factionChatPrimaryColorButton = new ColorButton(0, 0, 308, 20, factionChatPrimaryColor, button -> this.minecraft.gui.setScreen(new ColorSelectionPopupScreen(this.minecraft.gui.screen(), factionChatPrimaryColor, color -> {
            configuration.getOptions().factionChatPrimaryColorValue(color.getRGB());
            ((ColorButton) button).setColor(color);
        })));

        directionalLayoutWidget.addChild(factionChatPrimaryColorButton);

        Color factionChatSecondaryColor = new Color(configuration.getOptions().chatOptions().factionChatSecondaryColorValue());
        ColorButton factionChatSecondaryColorButton = new ColorButton(0, 0, 308, 20, factionChatSecondaryColor, button -> this.minecraft.gui.setScreen(new ColorSelectionPopupScreen(this.minecraft.gui.screen(), factionChatSecondaryColor, color -> {
            configuration.getOptions().factionChatSecondaryColorValue(color.getRGB());
            ((ColorButton) button).setColor(color);
        })));

        directionalLayoutWidget.addChild(factionChatSecondaryColorButton);

        directionalLayoutWidget.visitWidgets(this::addRenderableWidget);
    }
}
