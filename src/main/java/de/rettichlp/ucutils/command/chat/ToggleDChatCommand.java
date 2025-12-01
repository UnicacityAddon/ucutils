package de.rettichlp.ucutils.command.chat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.Storage;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.ucutils.UCUtils.notificationService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.Storage.ToggledChat.D_CHAT;
import static de.rettichlp.ucutils.common.Storage.ToggledChat.NONE;

@UCUtilsCommand(label = "dd")
public class ToggleDChatCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .executes(context -> {
                    Storage.ToggledChat newState = storage.getToggledChat() == D_CHAT ? NONE : D_CHAT;
                    storage.setToggledChat(newState);
                    notificationService.sendInfoNotification(newState.getToggleMessage());
                    return 1;
                });
    }
}
