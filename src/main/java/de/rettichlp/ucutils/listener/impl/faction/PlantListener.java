package de.rettichlp.ucutils.listener.impl.faction;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IBlockRightClickListener;
import de.rettichlp.ucutils.listener.IScreenOpenListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.messageService;
import static de.rettichlp.ucutils.UCUtils.player;
import static net.minecraft.block.Blocks.FERN;
import static net.minecraft.block.Blocks.PODZOL;
import static net.minecraft.entity.EquipmentSlot.MAINHAND;
import static net.minecraft.item.Items.BEETROOT_SEEDS;
import static net.minecraft.item.Items.BONE_MEAL;
import static net.minecraft.item.Items.WATER_BUCKET;
import static net.minecraft.screen.slot.SlotActionType.PICKUP;

@UCUtilsListener
public class PlantListener implements IBlockRightClickListener, IScreenOpenListener {

    private static final String PLANT_TEXT = "Plantage";

    @Override
    public void onBlockRightClick(@NotNull World world, Hand hand, @NotNull BlockHitResult hitResult) {
        BlockPos blockPos = hitResult.getBlockPos();

        boolean targetBlockIsPlant = world.getBlockState(blockPos).getBlock().equals(FERN) && world.getBlockState(blockPos.down()).getBlock().equals(PODZOL);
        if (!targetBlockIsPlant) {
            // check for plant placing
            ItemStack mainHandStack = player.getEquippedStack(MAINHAND);

            if (player.isSneaking() && (mainHandStack.isOf(BEETROOT_SEEDS))) {
                commandService.sendCommand("plant plant");
            }

            return;
        }

        boolean isStandingOnPlant = player.getBlockPos().equals(blockPos);
        if (!isStandingOnPlant) {
            messageService.sendModMessage("Du musst auf der Plantage stehen, um sie via UCUtils zu verwalten.", false);
            return;
        }

        commandService.sendCommand("plant");
    }

    @Override
    public void onScreenOpen(Screen screen, int scaledWidth, int scaledHeight) {
        ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;

        if (interactionManager != null && screen instanceof HopperScreen hopperScreen && PLANT_TEXT.equals(hopperScreen.getTitle().getString())) {
            ItemStack mainHandStack = player.getEquippedStack(MAINHAND);

            int syncId = hopperScreen.getScreenHandler().syncId;
            // https://i.imgur.com/b8INthP.png
            if (mainHandStack.isOf(WATER_BUCKET)) {
                interactionManager.clickSlot(syncId, 4, 0, PICKUP, player);
            } else if (mainHandStack.isOf(BONE_MEAL)) {
                interactionManager.clickSlot(syncId, 3, 0, PICKUP, player);
            }
        }
    }
}
