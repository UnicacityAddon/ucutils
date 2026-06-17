package de.rettichlp.ucutils.common.gui.screens;

import de.rettichlp.ucutils.common.models.ShutdownReason;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

import static de.rettichlp.ucutils.UCUtils.storage;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.client.gui.layouts.LinearLayout.vertical;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;

public class ShutdownScreen extends UCUtilsScreen {

    private static final Component BUTTON_SHUTDOWN_ABORT_NAME = translatable("ucutils.screen.shutdown_abort.button.name");

    private final ShutdownReason shutdownReason;

    public ShutdownScreen(ShutdownReason shutdownReason) {
        super(literal("Automatisches Herunterfahren"), literal(shutdownReason.getDisplayName()));
        this.shutdownReason = shutdownReason;
    }

    @Override
    public void initBody() {
        LinearLayout directionalLayoutWidget = this.layout.addToContents(vertical().spacing(4));
        directionalLayoutWidget.newCellSettings().alignHorizontallyCenter();

        directionalLayoutWidget.addChild(new StringWidget(empty()
                .append(literal("Das Spiel wird beendet und der PC heruntergefahren,").withStyle(GRAY)), this.font), positioner -> positioner.paddingTop(16));

        directionalLayoutWidget.addChild(new StringWidget(empty()
                .append(literal("wenn folgende Bedingung erfüllt ist:").withStyle(GRAY)), this.font), positioner -> positioner.paddingBottom(16));

        directionalLayoutWidget.addChild(new StringWidget(literal(this.shutdownReason.getConditionString()).withStyle(GOLD), this.font));

        directionalLayoutWidget.addChild(new StringWidget(empty()
                .append(literal("Wenn du dieses Fenster schließt,").withStyle(GRAY)), this.font), positioner -> positioner.paddingTop(16));

        directionalLayoutWidget.addChild(new StringWidget(empty()
                .append(literal("wird das automatische Herunterfahren gestoppt.").withStyle(GRAY)), this.font), positioner -> positioner.paddingBottom(16));

        directionalLayoutWidget.addChild(Button.builder(BUTTON_SHUTDOWN_ABORT_NAME, button -> onClose()).width(150).build());

        directionalLayoutWidget.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void doOnClose() {
        storage.getActiveShutdowns().clear();
    }
}
