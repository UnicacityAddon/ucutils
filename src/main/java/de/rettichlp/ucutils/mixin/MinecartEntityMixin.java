package de.rettichlp.ucutils.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.lang.System.currentTimeMillis;

@Mixin(MinecartEntity.class)
public abstract class MinecartEntityMixin {

    @Unique
    private long lastClick = 0;

    @Inject(method = "interact", at = @At("HEAD"))
    private void ucutils$interactHead(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        Entity entity = (Entity) (Object) this;
        if (entity instanceof MinecartEntity && player.isSneaking() && storage.isUnicaCity() && currentTimeMillis() - this.lastClick > 1000) {
            commandService.sendCommand("checkkfz");
            this.lastClick = currentTimeMillis();
        }
    }
}
