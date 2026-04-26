package de.rettichlp.ucutils.mixin;

import de.rettichlp.ucutils.listener.callback.PlayerEnterVehicleCallback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.entity.UniquelyIdentifiable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.ucutils.UCUtils.factionService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static net.minecraft.item.Items.SKELETON_SKULL;
import static net.minecraft.item.Items.WITHER_SKELETON_SKULL;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.GRAY;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "startRiding(Lnet/minecraft/entity/Entity;ZZ)Z", at = @At("RETURN"))
    private void ucutils$startRidingReturn(Entity vehicle,
                                           boolean force,
                                           boolean emitEvent,
                                           @NotNull CallbackInfoReturnable<Boolean> cir) {
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

    @Inject(method = "getCustomName", at = @At("RETURN"), cancellable = true)
    private void ucutils$getDisplayNameReturn(@NotNull CallbackInfoReturnable<Text> cir) {
        Entity self = (Entity) (Object) this;
        if (!(self instanceof ItemEntity itemEntity) || !itemEntity.hasCustomName()) {
            return;
        }

        ItemStack itemStack = itemEntity.getStack();
        Text returnValue = cir.getReturnValue();
        if (returnValue == null || (!itemStack.isOf(SKELETON_SKULL) && !itemStack.isOf(WITHER_SKELETON_SKULL))) {
            return;
        }

        String displayNameString = returnValue.getString();

        // extract player name (✟RettichLP -> RettichLP)
        String playerName = displayNameString.substring(1);

        // enrich player name with faction information (RettichLP -> RettichLP ⌜✚⌟)
        MutableText enrichedDisplayName = factionService.getEnrichedDisplayName(playerName);

        cir.setReturnValue(empty()
                .append(literal("✟").copy().formatted(GRAY))
                .append(enrichedDisplayName));
    }
}
