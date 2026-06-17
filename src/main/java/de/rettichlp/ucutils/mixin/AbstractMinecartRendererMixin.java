package de.rettichlp.ucutils.mixin;

import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.storage;

@Mixin(AbstractMinecartRenderer.class)
public abstract class AbstractMinecartRendererMixin<T extends AbstractMinecart, S extends MinecartRenderState> extends EntityRenderer<T, S> {

    protected AbstractMinecartRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;F)V",
            at = @At("TAIL"))
    private void ucutils$updateRenderStateTail(T entity, S state, float partialTicks, CallbackInfo ci) {
        if (!storage.isUnicaCity()) {
            return;
        }

        if (!configuration.getOptions().car().highlight()) {
            return;
        }

        if (storage.getMinecartEntityToHighlight() != null && storage.getMinecartEntityToHighlight().getUUID().equals(entity.getUUID())) {
            state.outlineColor = 0xFFFFAA00;
        }
    }
}
