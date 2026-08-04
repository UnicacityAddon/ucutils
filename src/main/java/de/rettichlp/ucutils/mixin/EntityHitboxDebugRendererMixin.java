package de.rettichlp.ucutils.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.rettichlp.ucutils.common.models.Faction;
import net.minecraft.client.renderer.debug.EntityHitboxDebugRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static de.rettichlp.ucutils.UCUtils.storage;
import static java.util.Optional.ofNullable;
import static net.minecraft.gizmos.GizmoStyle.stroke;

@Mixin(EntityHitboxDebugRenderer.class)
public abstract class EntityHitboxDebugRendererMixin {

    @ModifyArg(method = "showHitboxes",
               at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/gizmos/Gizmos;cuboid(Lnet/minecraft/world/phys/AABB;Lnet/minecraft/gizmos/GizmoStyle;)Lnet/minecraft/gizmos/GizmoProperties;",
                        ordinal = 0),
               index = 1)
    private GizmoStyle ucutils$showHitboxesInvoke(GizmoStyle style,
                                                  @Local(name = "entity", argsOnly = true) Entity entity,
                                                  @Local(name = "isServerEntity", argsOnly = true) boolean isServerEntity) {
        if (!storage.isUnicaCity()) {
            return style;
        }

        if (!(entity instanceof Player player) || isServerEntity) {
            return style;
        }

        String playerName = player.getPlainTextName();
        Faction faction = ofNullable(storage.getPlayerFactionCache().get(playerName)).orElseGet(() -> storage.getFaction(playerName));
        return stroke(faction.getColor().getValue() | 0xFF000000);
    }
}
