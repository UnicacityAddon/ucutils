package de.rettichlp.ucutils.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
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

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin extends AbstractClientPlayer {

    @Unique
    private final static List<Vec3> SHOP_LOCATIONS = List.of(
            new Vec3(47, 69, 203),
            new Vec3(1027, 69, 275)
    );

    public LocalPlayerMixin(ClientLevel level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
    private void ucutils$dropHead(boolean all, CallbackInfoReturnable<Boolean> cir) {
        if (!storage.isUnicaCity()) {
            return;
        }

        ItemStack selectedItem = getInventory().getSelectedItem();

        if (selectedItem.is(GLASS_BOTTLE) && isNearShop()) {
            // cancel drop
            cir.cancel();

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
