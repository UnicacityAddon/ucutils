package de.rettichlp.ucutils.common.gui.screens.components;

import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public interface CyclingButtonEntry {

    Component getDisplayName();

    Tooltip getTooltip();
}
