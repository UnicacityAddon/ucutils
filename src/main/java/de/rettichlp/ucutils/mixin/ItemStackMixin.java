package de.rettichlp.ucutils.mixin;

import net.minecraft.core.TypedInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static net.minecraft.world.item.Items.POTION;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Inject(method = "finishUsingItem", at = @At("HEAD"))
    private void ucutils$finishUsingItemHead(Level level, LivingEntity livingEntity, CallbackInfoReturnable<ItemStack> cir) {
        if (!storage.isUnicaCity()) {
            return;
        }

        TypedInstance<Item> itemStack = (ItemStack) (Object) this;
        if (itemStack.is(POTION)) {
            commandService.sendCommandWithHiddenOutput("health");
        }
    }
}
