package de.rettichlp.ucutils.listener;

import net.minecraft.text.Text;

public interface IMessageReceiveListener extends IUCUtilsListener {

    boolean onMessageReceive(Text text, String message);
}
