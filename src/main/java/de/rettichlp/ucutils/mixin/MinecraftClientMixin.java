package de.rettichlp.ucutils.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.ItemEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.nameTagService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.models.Faction.RETTUNGSDIENST;
import static net.minecraft.entity.projectile.ProjectileUtil.raycast;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin {

    @Unique
    private static final int DISTANCE = 3;

    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void ucutils$handleInputEventsHead(CallbackInfo ci) {
        if (!storage.isUnicaCity()) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();

        if (client.options.useKey.wasPressed()) {
            float partialTick = client.getRenderTickCounter().getTickProgress(true);

            Vec3d from = player.getCameraPosVec(partialTick);

            Vec3d direction = player.getRotationVec(partialTick);
            Vec3d to = from.add(direction.multiply(DISTANCE));

            Box box = player.getBoundingBox()
                    .stretch(direction.multiply(DISTANCE))
                    .expand(1.0);

            EntityHitResult result = raycast(player, from, to, box, e -> e instanceof ItemEntity, DISTANCE * DISTANCE);

            if (result == null || !(result.getEntity() instanceof ItemEntity itemEntity) || !itemEntity.hasCustomName()) {
                return;
            }

            Text customName = itemEntity.getCustomName();
            assert customName != null;
            String playerName = nameTagService.revertEnrichment(customName);

            if (player.isSneaking()) {
                commandService.sendCommand("erstehilfe " + playerName);
            } else if (storage.getFaction(player.getStringifiedName()) == RETTUNGSDIENST) {
                commandService.sendCommand("revive " + playerName);
            }
        }
    }
}
