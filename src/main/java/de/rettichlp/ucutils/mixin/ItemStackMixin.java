package de.rettichlp.ucutils.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static net.minecraft.item.Items.POTION;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "finishUsing", at = @At("HEAD"))
    private void ucutils$finishUsingHead(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (!storage.isUnicaCity()) {
            return;
        }

        ItemStack itemStack = (ItemStack) (Object) this;
        if (itemStack.isOf(POTION)) {
            commandService.sendCommandWithHiddenOutput("health");
        }
    }
}
