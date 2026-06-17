package de.rettichlp.ucutils.listener;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public interface IHudRenderListener extends IUCUtilsListener {

    void onHudRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);
}
