package de.rettichlp.ucutils.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static net.minecraft.world.item.Items.GLASS_BOTTLE;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Unique
    private final static List<Vec3> SHOP_LOCATIONS = List.of(
            new Vec3(47, 69, 203),
            new Vec3(1027, 69, 275)
    );

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void ucutils$dropHead(ItemStack itemStack, boolean randomly, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir) {
        if (!storage.isUnicaCity()) {
            return;
        }

        if (cir.getReturnValue() == null) {
            return;
        }

        if (itemStack.is(GLASS_BOTTLE) && isNearShop()) {
            // cancel drop
            cir.setReturnValue(null);

            // execute command
            commandService.sendCommand("sell pfand");
        }
    }

    @Unique
    private boolean isNearShop() {
        Vec3 position = player.position();
        return SHOP_LOCATIONS.stream().anyMatch(blockPos -> position.closerThan(blockPos, 3));
    }
}
