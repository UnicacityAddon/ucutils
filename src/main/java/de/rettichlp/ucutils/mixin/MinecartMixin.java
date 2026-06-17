package de.rettichlp.ucutils.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.lang.System.currentTimeMillis;
import static net.minecraft.world.InteractionResult.CONSUME;

@Mixin(Minecart.class)
public abstract class MinecartMixin {

    @Unique
    private long lastClick = 0;

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void ucutils$interactHead(Player player,
                                      InteractionHand hand,
                                      Vec3 location,
                                      CallbackInfoReturnable<InteractionResult> cir) {
        if (!storage.isUnicaCity()) {
            return;
        }

        Entity entity = (Entity) (Object) this;
        if (configuration.getOptions().car().automatedCheckKfz() && entity instanceof Minecart && player.isCrouching() && currentTimeMillis() - this.lastClick > 1000) {
            commandService.sendCommand("checkkfz");
            this.lastClick = currentTimeMillis();
            cir.setReturnValue(CONSUME);
        }
    }
}
