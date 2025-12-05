package de.rettichlp.ucutils.mixin;

import de.rettichlp.ucutils.common.configuration.options.NameTagOptions;
import de.rettichlp.ucutils.common.models.BlacklistEntry;
import de.rettichlp.ucutils.common.models.ContractEntry;
import de.rettichlp.ucutils.common.models.Faction;
import de.rettichlp.ucutils.common.models.HousebanEntry;
import de.rettichlp.ucutils.common.models.WantedEntry;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.factionService;
import static de.rettichlp.ucutils.UCUtils.networkHandler;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.models.Color.WHITE;
import static java.time.LocalDateTime.now;
import static net.minecraft.scoreboard.AbstractTeam.CollisionRule.NEVER;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.of;
import static net.minecraft.text.TextColor.fromFormatting;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.DARK_RED;
import static net.minecraft.util.Formatting.RED;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void ucutils$getDisplayNameHead(T entity, CallbackInfoReturnable<Text> cir) {
        Text originalDisplayName = entity.getDisplayName();
        Text formattedDisplayName = entity instanceof PlayerEntity playerEntity && originalDisplayName != null
                ? getFormattedTargetDisplayName(originalDisplayName, playerEntity.getGameProfile().name())
                : originalDisplayName;
        cir.setReturnValue(formattedDisplayName);
    }

    @Unique
    private MutableText getFormattedTargetDisplayName(@NotNull Text targetDisplayName, String targetName) {
        NameTagOptions nameTagOptions = configuration.getOptions().nameTag();
        Faction targetFaction = storage.getCachedFaction(targetName);

        Text newTargetDisplayNamePrefix = empty();
        Text newTargetDisplayName = targetDisplayName.copy();
        Text newTargetDisplayNameSuffix = nameTagOptions.factionInformation() ? targetFaction.getNameTagSuffix() : empty();
        Formatting newTargetDisplayNameColor;

        // highlight factions
        newTargetDisplayNameColor = nameTagOptions.highlightFactions().getOrDefault(targetFaction, WHITE).getFormatting();

        // blacklist
        Optional<BlacklistEntry> optionalTargetBlacklistEntry = storage.getBlacklistEntries().stream()
                .filter(blacklistEntry -> blacklistEntry.getPlayerName().equals(targetName))
                .findAny();

        if (optionalTargetBlacklistEntry.isPresent() && nameTagOptions.additionalBlacklist()) {
            newTargetDisplayNameColor = RED;
            newTargetDisplayNamePrefix = !optionalTargetBlacklistEntry.get().isOutlaw() ? empty() : empty()
                    .append(of("[").copy().formatted(DARK_GRAY))
                    .append(of("V").copy().formatted(DARK_RED))
                    .append(of("]").copy().formatted(DARK_GRAY));
        }

        // contract
        Optional<ContractEntry> optionalTargetContractEntry = storage.getContractEntries().stream()
                .filter(contractEntry -> contractEntry.getPlayerName().equals(targetName))
                .findAny();

        if (optionalTargetContractEntry.isPresent() && nameTagOptions.additionalContract()) {
            newTargetDisplayNameColor = RED;
        }

        // houseban
        Optional<HousebanEntry> optionalTargetHousebanEntry = storage.getHousebanEntries().stream()
                .filter(housebanEntry -> housebanEntry.getPlayerName().equals(targetName))
                .filter(housebanEntry -> housebanEntry.getUnbanDateTime().isAfter(now()))
                .findAny();

        if (optionalTargetHousebanEntry.isPresent() && nameTagOptions.additionalHouseban()) {
            newTargetDisplayNamePrefix = empty()
                    .append(of("[").copy().formatted(DARK_GRAY))
                    .append(of("HV").copy().formatted(DARK_RED))
                    .append(of("]").copy().formatted(DARK_GRAY));
        }

        // wanted
        Optional<WantedEntry> optionalTargetWantedEntry = storage.getWantedEntries().stream()
                .filter(wantedEntry -> wantedEntry.getPlayerName().equals(targetName))
                .findAny();

        if (optionalTargetWantedEntry.isPresent() && nameTagOptions.additionalWanted()) {
            newTargetDisplayNameColor = factionService.getWantedPointColor(optionalTargetWantedEntry.get().getWantedPointAmount());
        }

        return empty()
                .append(newTargetDisplayNamePrefix)
                .append(" ")
                .append(newTargetDisplayName.copy().formatted(newTargetDisplayNameColor))
                .append(" ")
                .append(newTargetDisplayNameSuffix);
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
