package de.rettichlp.ucutils.listener;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;

public interface IEntityRenderListener extends IUCUtilsListener {

    void onEntityRender(WorldRenderContext context);
}
