package de.rettichlp.ucutils.listener.impl.faction;

import de.rettichlp.ucutils.common.models.Faction;
import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IBlockRightClickListener;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Set;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.models.Faction.FBI;
import static de.rettichlp.ucutils.common.models.Faction.KERZAKOV_FAMILIE;
import static de.rettichlp.ucutils.common.models.Faction.WESTSIDE_BALLAS;
import static java.util.Collections.emptySet;
import static net.minecraft.util.Hand.MAIN_HAND;

@UCUtilsListener
public class FactionDoorListener implements IBlockRightClickListener {

    private static final Map<Faction, Set<BlockPos>> FACTION_DOOR_POSITIONS = Map.of(
            FBI, Set.of(new BlockPos(879, 62, -87)),
            KERZAKOV_FAMILIE, Set.of(new BlockPos(936, 69, 191), new BlockPos(936, 69, 174)),
            WESTSIDE_BALLAS, Set.of(new BlockPos(-166, 68, 205)));
    private static final int DISTANCE = 4;

    @Override
    public void onBlockRightClick(World world, Hand hand, BlockHitResult hitResult) {
        if (!player.getStackInHand(MAIN_HAND).isEmpty()) {
            return;
        }

        Faction faction = storage.getFaction(player.getGameProfile().name());
        Set<BlockPos> factionDoorPositions = FACTION_DOOR_POSITIONS.getOrDefault(faction, emptySet());

        factionDoorPositions.stream()
                .filter(blockPos -> blockPos.isWithinDistance(hitResult.getBlockPos(), DISTANCE))
                .findAny()
                .ifPresent(blockPos -> commandService.sendCommand("fdoor"));
    }
}
