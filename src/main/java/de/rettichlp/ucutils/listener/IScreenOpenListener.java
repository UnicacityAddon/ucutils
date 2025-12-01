package de.rettichlp.ucutils.listener;

import net.minecraft.client.gui.screen.Screen;

public interface IScreenOpenListener extends IUCUtilsListener {

    void onScreenOpen(Screen screen, int scaledWidth, int scaledHeight);
}
