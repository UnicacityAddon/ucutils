package de.rettichlp.ucutils.mixin;

import de.rettichlp.ucutils.common.configuration.options.NameTagOptions;
import de.rettichlp.ucutils.common.models.BlacklistEntry;
import de.rettichlp.ucutils.common.models.ContractEntry;
import de.rettichlp.ucutils.common.models.Faction;
import de.rettichlp.ucutils.common.models.HousebanEntry;
import de.rettichlp.ucutils.common.models.WantedEntry;
import de.rettichlp.ucutils.listener.callback.PlayerEnterVehicleCallback;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.entity.UniquelyIdentifiable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.nameTagService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.models.Color.WHITE;
import static java.time.LocalDateTime.now;
import static net.minecraft.item.Items.SKELETON_SKULL;
import static net.minecraft.item.Items.WITHER_SKELETON_SKULL;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.of;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.DARK_RED;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.RED;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "startRiding(Lnet/minecraft/entity/Entity;ZZ)Z", at = @At("RETURN"))
    private void ucutils$startRidingReturn(Entity vehicle,
                                           boolean force,
                                           boolean emitEvent,
                                           @NotNull CallbackInfoReturnable<Boolean> cir) {
        if (!storage.isUnicaCity()) {
            return;
        }

        // only for successful start riding
        if (!cir.getReturnValue()) {
            return;
        }

        UniquelyIdentifiable self = (Entity) (Object) this;
        if (self.getUuid().equals(player.getUuid())) {
            PlayerEnterVehicleCallback.EVENT.invoker().onEnter(vehicle);
        }
    }

    @Inject(method = "stopRiding", at = @At("HEAD"))
    private void ucutils$stopRidingHead(CallbackInfo ci) {
        if (!storage.isUnicaCity()) {
            return;
        }

        Entity self = (Entity) (Object) this;
        if (self instanceof ClientPlayerEntity && self.hasVehicle() && self.getVehicle() instanceof MinecartEntity minecartEntity) {
            storage.setMinecartEntityToHighlight(minecartEntity);
        }
    }

    @Inject(method = "getCustomName", at = @At("RETURN"), cancellable = true)
    private void ucutils$getDisplayNameReturn(@NotNull CallbackInfoReturnable<Text> cir) {
        if (!storage.isUnicaCity()) {
            return;
        }

        Entity self = (Entity) (Object) this;
        if (!(self instanceof ItemEntity itemEntity) || !itemEntity.hasCustomName()) {
            return;
        }

        ItemStack itemStack = itemEntity.getStack();
        Text returnValue = cir.getReturnValue();
        if (returnValue == null || (!itemStack.isOf(SKELETON_SKULL) && !itemStack.isOf(WITHER_SKELETON_SKULL))) {
            return;
        }

        String displayNameString = returnValue.getString();

        // extract player name (✟RettichLP -> RettichLP)
        String playerName = displayNameString.substring(1);

        // enrich player name with faction information (RettichLP -> RettichLP ⌜✚⌟)
        MutableText enrichedDisplayName = getEnrichedDisplayName(playerName);

        cir.setReturnValue(empty()
                .append(literal("✟").copy().formatted(GRAY))
                .append(enrichedDisplayName));
    }

    @Unique
    private MutableText getEnrichedDisplayName(String targetName) {
        NameTagOptions nameTagOptions = configuration.getOptions().nameTag();
        Faction targetFaction = storage.getCachedFaction(targetName);

        Text newTargetDisplayNamePrefix = empty();
        Text newTargetDisplayName = literal(targetName);
        Text newTargetDisplayNameSuffix = targetFaction.getNameTagSuffix();
        Formatting newTargetDisplayNameColor;

        // highlight factions
        newTargetDisplayNameColor = WHITE.getFormatting();

        // blacklist
        Optional<BlacklistEntry> optionalTargetBlacklistEntry = storage.getBlacklistEntries().stream()
                .filter(blacklistEntry -> blacklistEntry.getPlayerName().equals(targetName))
                .findAny();

        if (optionalTargetBlacklistEntry.isPresent() && nameTagOptions.outlaw()) {
            newTargetDisplayNameColor = RED;
            newTargetDisplayNamePrefix = optionalTargetBlacklistEntry.get().isOutlaw()
                    ? empty()
                      .append(of("[").copy().formatted(DARK_GRAY))
                      .append(of("V").copy().formatted(DARK_RED))
                      .append(of("]").copy().formatted(DARK_GRAY))
                    : empty();
        }

        // contract
        Optional<ContractEntry> optionalTargetContractEntry = storage.getContractEntries().stream()
                .filter(contractEntry -> contractEntry.getPlayerName().equals(targetName))
                .findAny();

        if (optionalTargetContractEntry.isPresent()) {
            newTargetDisplayNameColor = RED;
        }

        // houseban
        Optional<HousebanEntry> optionalTargetHousebanEntry = storage.getHousebanEntries().stream()
                .filter(housebanEntry -> housebanEntry.getPlayerName().equals(targetName))
                .filter(housebanEntry -> housebanEntry.getUnbanDateTime().isAfter(now()))
                .findAny();

        if (optionalTargetHousebanEntry.isPresent() && nameTagOptions.houseBan()) {
            newTargetDisplayNamePrefix = empty()
                    .append(of("[").copy().formatted(DARK_GRAY))
                    .append(of("HV").copy().formatted(DARK_RED))
                    .append(of("]").copy().formatted(DARK_GRAY));
        }

        // wanted
        Optional<WantedEntry> optionalTargetWantedEntry = storage.getWantedEntries().stream()
                .filter(wantedEntry -> wantedEntry.getPlayerName().equals(targetName))
                .findAny();

        if (optionalTargetWantedEntry.isPresent()) {
            newTargetDisplayNameColor = nameTagService.getWantedPointColor(optionalTargetWantedEntry.get().getWantedPointAmount());
        }

        return empty()
                .append(newTargetDisplayNamePrefix)
                .append(" ")
                .append(newTargetDisplayName.copy().formatted(newTargetDisplayNameColor))
                .append(" ")
                .append(newTargetDisplayNameSuffix);
    }
}
