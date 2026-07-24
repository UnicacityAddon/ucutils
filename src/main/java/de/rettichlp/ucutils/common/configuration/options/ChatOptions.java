package de.rettichlp.ucutils.common.configuration.options;

import de.rettichlp.ucutils.common.models.Color;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static de.rettichlp.ucutils.common.models.Color.BLUE;
import static de.rettichlp.ucutils.common.models.Color.DARK_AQUA;

@Getter
@Setter
@Accessors(fluent = true)
public class ChatOptions {

    private boolean changeFactionChatColor = false;
    private Color factionChatColorPrimary = BLUE;
    private Color factionChatColorSecondary = DARK_AQUA;
}
