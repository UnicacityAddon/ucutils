package de.rettichlp.ucutils.listener;

import org.jetbrains.annotations.NotNull;

public interface ICommandSendListener extends IUCUtilsListener {

    boolean onCommandSend(@NotNull String command);
}
