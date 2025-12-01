package de.rettichlp.ucutils.listener;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public interface IBlockRightClickListener extends IPKUtilsListener {

    void onBlockRightClick(World world, Hand hand, BlockHitResult hitResult);
}
