package de.rettichlp.ucutils.common.registry;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public abstract class CommandBase {

    protected static final SuggestionProvider<FabricClientCommandSource> PLAYER_NAMES_SUGGESTION_PROVIDER = (context, builder) -> {
        String remaining = builder.getRemaining();

        int lastSpaceIndex = remaining.lastIndexOf(' ');
        String currentToken = lastSpaceIndex == -1 ? remaining : remaining.substring(lastSpaceIndex + 1);
        String prefix = lastSpaceIndex == -1 ? "" : remaining.substring(0, lastSpaceIndex + 1);

        Set<String> alreadyTyped = stream(remaining.split(" "))
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toSet());

        Minecraft minecraft = Minecraft.getInstance();
        ClientPacketListener connection = minecraft.getConnection();
        if (connection != null) {
            List<String> playerNames = connection.getListedOnlinePlayers().stream()
                    .map(playerInfo -> playerInfo.getProfile().name())
                    .filter(name -> !alreadyTyped.contains(name))
                    .filter(name -> name.toLowerCase().startsWith(currentToken.toLowerCase()))
                    .toList();

            SuggestionsBuilder offsetBuilder = builder.createOffset(builder.getStart() + prefix.length());
            playerNames.forEach(offsetBuilder::suggest);
            return offsetBuilder.buildFuture();
        }

        return builder.buildFuture();
    };

    public abstract LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node);
}
