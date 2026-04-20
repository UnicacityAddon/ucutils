package de.rettichlp.ucutils.mixin;

import de.rettichlp.ucutils.listener.callback.PlayerEnterVehicleCallback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.world.entity.UniquelyIdentifiable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "startRiding(Lnet/minecraft/entity/Entity;ZZ)Z", at = @At("RETURN"))
    private void ucutils$startRidingReturn(Entity vehicle, boolean force, boolean emitEvent, @NotNull CallbackInfoReturnable<Boolean> cir) {
        // only for successful start riding
        if (!cir.getReturnValue()) {
            return;
        }

        UniquelyIdentifiable self = (Entity) (Object) this;
        if (self.getUuid().equals(player.getUuid())) {
            PlayerEnterVehicleCallback.EVENT.invoker().onEnter(vehicle);
        }
    }

    @Inject(method = "stopRiding", at = @At("HEAD"))
    private void ucutils$stopRidingHead(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (self instanceof ClientPlayerEntity && self.hasVehicle() && self.getVehicle() instanceof MinecartEntity minecartEntity && storage.isUnicaCity()) {
            storage.setMinecartEntityToHighlight(minecartEntity);
        }
    }
}
