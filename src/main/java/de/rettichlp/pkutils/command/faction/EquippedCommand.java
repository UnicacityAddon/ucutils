package de.rettichlp.pkutils.command.faction;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.pkutils.common.models.Faction;
import de.rettichlp.pkutils.common.models.FactionMember;
import de.rettichlp.pkutils.common.registry.CommandBase;
import de.rettichlp.pkutils.common.registry.PKUtilsCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static com.mojang.brigadier.suggestion.Suggestions.empty;
import static de.rettichlp.pkutils.PKUtils.commandService;
import static de.rettichlp.pkutils.PKUtils.messageService;
import static de.rettichlp.pkutils.PKUtils.player;
import static de.rettichlp.pkutils.PKUtils.storage;
import static de.rettichlp.pkutils.common.models.Faction.NULL;
import static java.lang.Integer.MIN_VALUE;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static net.minecraft.command.CommandSource.suggestMatching;

@PKUtilsCommand(label = "equipped")
public class EquippedCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node
                .then(argument("weeksAgo", integer(MIN_VALUE, 0))
                        .suggests((context, builder) -> suggestMatching(List.of("0", "-1", "-2", "-3", "-4", "-5"), builder))
                        .executes(context -> {
                            throw new IllegalStateException("Not implemented yet");
                        }))
                .then(literal("player")
                        .then(argument("player", word())
                                .requires(fabricClientCommandSource -> {
                                    String playerName = player.getGameProfile().getName();
                                    Faction faction = storage.getFaction(playerName);
                                    // rank 4 or higher in own faction
                                    return commandService.isSuperUser() || faction.getMembers().stream()
                                            .filter(factionMember -> factionMember.playerName().equals(playerName))
                                            .findFirst()
                                            .map(factionMember -> factionMember.rank() >= 4)
                                            .orElse(false);
                                })
                                .suggests((context, builder) -> {
                                    String playerName = player.getGameProfile().getName();
                                    Faction faction = storage.getFaction(playerName);

                                    return faction != NULL ? suggestMatching(faction.getMembers().stream()
                                            .map(FactionMember::playerName), builder) : empty();
                                })
                                .then(argument("weeksAgo", integer(MIN_VALUE, 0))
                                        .suggests((context, builder) -> suggestMatching(List.of("0", "-1", "-2", "-3", "-4", "-5"), builder))
                                        .executes(context -> {
                                            String playerName = player.getGameProfile().getName();
                                            Faction faction = storage.getFaction(playerName);

                                            String targetName = getString(context, "player");
                                            Faction targetFaction = storage.getFaction(targetName);

                                            int weeksAgo = context.getArgument("weeksAgo", Integer.class);

                                            // check faction
                                            if (faction != targetFaction && !commandService.isSuperUser()) {
                                                messageService.sendModMessage("Der Spieler ist nicht in deiner Fraktion.", false);
                                                return 1;
                                            }

                                            throw new IllegalStateException("Not implemented yet");
                                        }))
                                .executes(context -> {
                                    String playerName = player.getGameProfile().getName();
                                    Faction faction = storage.getFaction(playerName);

                                    String targetName = getString(context, "player");
                                    Faction targetFaction = storage.getFaction(targetName);

                                    // check faction
                                    if (faction != targetFaction && !commandService.isSuperUser()) {
                                        messageService.sendModMessage("Der Spieler ist nicht in deiner Fraktion.", false);
                                        return 1;
                                    }

                                    throw new IllegalStateException("Not implemented yet");
                                })))
                .executes(context -> {
                    throw new IllegalStateException("Not implemented yet");
                });
    }
}
