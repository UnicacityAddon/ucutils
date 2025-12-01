package de.rettichlp.ucutils.command.chat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.Storage;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.PKUtilsCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.ucutils.PKUtils.notificationService;
import static de.rettichlp.ucutils.PKUtils.storage;
import static de.rettichlp.ucutils.common.Storage.ToggledChat.NONE;
import static de.rettichlp.ucutils.common.Storage.ToggledChat.W_CHAT;

@PKUtilsCommand(label = "ww")
public class ToggleWChatCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .executes(context -> {
                    Storage.ToggledChat newState = storage.getToggledChat() == W_CHAT ? NONE : W_CHAT;
                    storage.setToggledChat(newState);
                    notificationService.sendInfoNotification(newState.getToggleMessage());
                    return 1;
                });
    }
}
