package de.rettichlp.ucutils.listener;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public interface IHudRenderListener extends IPKUtilsListener {

    void onHudRender(DrawContext drawContext, RenderTickCounter renderTickCounter);
}
