package de.rettichlp.ucutils.common.configuration.options;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jspecify.annotations.NonNull;

import java.awt.Color;

import static net.minecraft.network.chat.TextColor.BLUE;
import static net.minecraft.network.chat.TextColor.DARK_AQUA;

@Getter
@Setter
@Accessors(fluent = true)
public class ChatOptions {

    private boolean changeFactionChatColor = false;
    private int factionChatColorPrimary = BLUE.getValue();
    private int factionChatColorSecondary = DARK_AQUA.getValue();

    public Color factionChatColorPrimary() {
        return new Color(this.factionChatColorPrimary);
    }

    public void factionChatColorPrimary(@NonNull Color factionChatColorPrimary) {
        this.factionChatColorPrimary = factionChatColorPrimary.getRGB();
    }

    public Color factionChatColorSecondary() {
        return new Color(this.factionChatColorSecondary);
    }

    public void factionChatColorSecondary(@NonNull Color factionChatColorSecondary) {
        this.factionChatColorSecondary = factionChatColorSecondary.getRGB();
    }
}
