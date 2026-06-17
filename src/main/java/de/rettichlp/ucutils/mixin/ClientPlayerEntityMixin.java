package de.rettichlp.ucutils.mixin;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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

@Mixin(Player.class)
public abstract class ClientPlayerEntityMixin {

    @Unique
    private final static List<Vec3> SHOP_LOCATIONS = List.of(
            new Vec3(45, 69, 200),
            new Vec3(1049, 69, -189)
    );

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void ucutils$dropHead(ItemStack itemStack, boolean thrownFromHand, CallbackInfoReturnable<ItemEntity> cir) {
        if (!storage.isUnicaCity()) {
            return;
        }

        if (player.getMainHandItem().is(GLASS_BOTTLE) && isNearShop()) {
            // cancel drop
            cir.setReturnValue(null);

            // execute command
            commandService.sendCommand("sell pfand");
        }
    }

    @Unique
    private boolean isNearShop() {
        Vec3 position = player.position();
        return SHOP_LOCATIONS.stream()
                .anyMatch(blockPos -> position.closerThan(blockPos, 10));
    }
}
