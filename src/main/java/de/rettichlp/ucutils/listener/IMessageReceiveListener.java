package de.rettichlp.ucutils.listener;

import net.minecraft.network.chat.Component;

public interface IMessageReceiveListener extends IUCUtilsListener {

    boolean onMessageReceive(Component text, String message);
}
