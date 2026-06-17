package de.rettichlp.ucutils.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.level.entity.UniquelyIdentifyable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;ZZ)Z", at = @At("RETURN"))
    private void ucutils$startRidingReturn(Entity entityToRide,
                                           boolean force,
                                           boolean sendEventAndTriggers,
                                           @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (!storage.isUnicaCity()) {
            return;
        }

        // only for successful start riding
        if (!cir.getReturnValue()) {
            return;
        }

        UniquelyIdentifyable self = (Entity) (Object) this;
        if (self.getUUID().equals(player.getUUID()) && entityToRide instanceof Minecart) {
            storage.setMinecartEntityToHighlight(null);

            if (configuration.getOptions().car().automatedStart() && !storage.isPremium()) {
                // start the car with a small delay to ensure the player is fully in the vehicle
                utilService.delayedAction(() -> commandService.sendCommand("car start"), 500);
            }

            // lock the car after 1 second and the small delay if not already locked
            if (!storage.isCarLocked() && configuration.getOptions().car().automatedLock() && !storage.isPremium()) {
                utilService.delayedAction(() -> commandService.sendCommand("car lock"), 1500);
            }
        }
    }

    @Inject(method = "stopRiding", at = @At("HEAD"))
    private void ucutils$stopRidingHead(CallbackInfo ci) {
        if (!storage.isUnicaCity()) {
            return;
        }

        Entity self = (Entity) (Object) this;
        if (self instanceof Player && self.isPassenger() && self.getVehicle() instanceof Minecart minecart) {
            storage.setMinecartEntityToHighlight(minecart);
        }
    }
}
