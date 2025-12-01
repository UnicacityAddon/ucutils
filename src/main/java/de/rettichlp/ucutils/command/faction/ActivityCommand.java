package de.rettichlp.ucutils.command.faction;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.rettichlp.ucutils.common.gui.screens.FactionActivityScreen;
import de.rettichlp.ucutils.common.models.Faction;
import de.rettichlp.ucutils.common.models.FactionMember;
import de.rettichlp.ucutils.common.registry.CommandBase;
import de.rettichlp.ucutils.common.registry.UCUtilsCommand;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import static de.rettichlp.ucutils.UCUtils.api;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.gui.screens.FactionActivityScreen.SortingType.RANK;
import static de.rettichlp.ucutils.common.gui.screens.components.TableHeaderTextWidget.SortingDirection.DESCENDING;

@UCUtilsCommand(label = "activity")
public class ActivityCommand extends CommandBase {

    @Override
    public LiteralArgumentBuilder<FabricClientCommandSource> execute(@NotNull LiteralArgumentBuilder<FabricClientCommandSource> node) {
        return node.executes(context -> {
            Faction faction = storage.getFaction(player.getGameProfile().getName());
            api.getFactionResetTime(faction, weeklyTime -> {
                MinecraftClient client = MinecraftClient.getInstance();

                LocalDateTime to = weeklyTime.nextOccurrence();
                LocalDateTime from = to.minusWeeks(1);
                api.getFactionPlayerData(from, to, faction.getMembers().stream().map(FactionMember::playerName).toList(), factionPlayerDataResponse -> client.execute(() -> {
                    FactionActivityScreen factionActivityScreen = new FactionActivityScreen(faction, from, to, factionPlayerDataResponse, RANK, DESCENDING);
                    client.setScreen(factionActivityScreen);
                }));
            });

            return 1;
        });
    }
}
