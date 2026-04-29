package de.rettichlp.ucutils.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static de.rettichlp.ucutils.UCUtils.nameTagService;
import static de.rettichlp.ucutils.UCUtils.networkHandler;
import static net.minecraft.scoreboard.AbstractTeam.CollisionRule.NEVER;
import static net.minecraft.text.TextColor.fromFormatting;
import static net.minecraft.util.Formatting.DARK_GRAY;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void ucutils$getDisplayNameReturn(@NotNull CallbackInfoReturnable<Text> cir) {
        GameProfile gameProfile = ((PlayerEntity) (Object) this).getGameProfile();
        cir.setReturnValue(nameTagService.getEnrichedDisplayName(gameProfile.name()));
    }

    @Unique
    private boolean isAfk(String targetName) {
        return networkHandler.getPlayerList().stream()
                .filter(entry -> entry.getProfile().name().equals(targetName))
                .anyMatch(entry -> {
                    Team team = entry.getScoreboardTeam();
                    if (team == null) {
                        return false;
                    }

                    if (team.getCollisionRule() != NEVER) {
                        return false; // only afk & aduty players have collision rule set to NEVER
                    }

                    Text displayName = entry.getDisplayName();
                    if (displayName == null) {
                        return false;
                    }

                    List<Text> siblings = displayName.getSiblings();
                    if (siblings.isEmpty()) {
                        return false;
                    }

                    TextColor color = siblings.getFirst().getStyle().getColor();
                    if (color == null) {
                        return false;
                    }

                    return !color.equals(fromFormatting(DARK_GRAY)); // filter out aduty players (dark gray)
                });
    }
}
