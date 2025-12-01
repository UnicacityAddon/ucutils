package de.rettichlp.ucutils.listener;

import net.minecraft.entity.Entity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public interface IEntityRightClickListener extends IPKUtilsListener {

    void onEntityRightClick(World world, Hand hand, Entity entity, EntityHitResult hitResult);
}
