package de.rettichlp.ucutils.mixin;

import com.mojang.authlib.GameProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.notificationService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.awt.Color.WHITE;
import static net.minecraft.ChatFormatting.BLUE;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.YELLOW;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Unique
    private static final MutableComponent A_DUTY_PREFIX = empty()
            .append(literal("[").withStyle(DARK_GRAY)
                    .append(literal("UC").withStyle(BLUE))
                    .append(literal("]").withStyle(DARK_GRAY)));

    @Unique
    private static final MutableComponent BUILD_MODE_PREFIX = empty()
            .append(literal("[").withStyle(DARK_GRAY)
                    .append(literal("B").withStyle(YELLOW))
                    .append(literal("]").withStyle(DARK_GRAY)));

    @Unique
    private static final MutableComponent REPORT_PREFIX = empty()
            .append(literal("[").withStyle(DARK_GRAY)
                    .append(literal("R").withStyle(GOLD))
                    .append(literal("]").withStyle(DARK_GRAY)));

    @Unique
    private final Collection<EnrichedGameProfile> enrichedGameProfiles = new HashSet<>();

    @Inject(method = "handlePlayerInfoRemove",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/network/protocol/PacketUtils;ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/network/PacketProcessor;)V",
                     shift = AFTER))
    private void ucutils$handlePlayerInfoRemoveInvoke(ClientboundPlayerInfoRemovePacket packet, CallbackInfo ci) {
        if (!storage.isUnicaCity()) {
            return;
        }

        if (!configuration.getOptions().notification().joinQuit()) {
            return;
        }

        for (UUID uuid : packet.profileIds()) {
            this.enrichedGameProfiles.stream()
                    .filter(egp -> egp.getProfile().id().equals(uuid))
                    .findFirst()
                    .ifPresent(egp -> sendChangeNotification(egp, "ucutils.notification.player_quit"));
        }
    }

//    @Inject(method = "", at = @At("HEAD"))
//    private void ucutils$handlePlayerListActionHead(PlayerListS2CPacket.Action action,
//                                                    PlayerListS2CPacket.Entry receivedEntry,
//                                                    PlayerListEntry currentEntry,
//                                                    CallbackInfo ci) {
//        if (!storage.isUnicaCity()) {
//            return;
//        }
//
//
//
//        GameProfile profile = receivedEntry.profile();
//        UUID profileId = receivedEntry.profileId();
//        Component currentDisplayName = receivedEntry.displayName();
//
//        if (currentDisplayName == null) {
//            return;
//        }
//
//        switch (action) {
//            case ADD_PLAYER -> {
//                if (!configuration.getOptions().notification().joinQuit()) {
//                    return;
//                }
//
//                EnrichedGameProfile enrichedGameProfile = new EnrichedGameProfile(profile, currentDisplayName, currentDisplayName);
//                this.enrichedGameProfiles.removeIf(egp -> egp.getProfile().id().equals(profileId));
//                this.enrichedGameProfiles.add(enrichedGameProfile);
//
//                // if the client joined the server few moments ago, hide notifications due to initial sync of player list
//                if (currentTimeMillis() - storage.getJoinTimestamp() < 1000) {
//                    return;
//                }
//
//                sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_join");
//            }
//            case UPDATE_DISPLAY_NAME -> {
//                EnrichedGameProfile enrichedGameProfile = this.enrichedGameProfiles.stream()
//                        .filter(egp -> egp.getProfile().id().equals(profileId))
//                        .findFirst()
//                        .orElseGet(() -> {
//                            EnrichedGameProfile egp = new EnrichedGameProfile(profile, currentDisplayName, currentDisplayName);
//                            this.enrichedGameProfiles.add(egp);
//                            return egp;
//                        });
//
//                Component previousDisplayName = enrichedGameProfile.getPreviousDisplayName();
//
//                // handle report change
//                if (configuration.getOptions().notification().report()) {
//                    if (!previousDisplayName.contains(REPORT_PREFIX) && currentDisplayName.contains(REPORT_PREFIX)) {
//                        sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_enter_report");
//                        enrichedGameProfile.setPreviousDisplayName(currentDisplayName);
//                        return;
//                    }
//
//                    if (previousDisplayName.contains(REPORT_PREFIX) && !currentDisplayName.contains(REPORT_PREFIX)) {
//                        sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_leave_report");
//                        enrichedGameProfile.setPreviousDisplayName(currentDisplayName);
//                        return;
//                    }
//                }
//
//                // handle build mode change
//                if (configuration.getOptions().notification().buildMode()) {
//                    if (!previousDisplayName.contains(BUILD_MODE_PREFIX) && currentDisplayName.contains(BUILD_MODE_PREFIX)) {
//                        sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_enter_buildmode");
//                        enrichedGameProfile.setPreviousDisplayName(currentDisplayName);
//                        return;
//                    }
//
//                    if (previousDisplayName.contains(BUILD_MODE_PREFIX) && !currentDisplayName.contains(BUILD_MODE_PREFIX)) {
//                        sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_leave_buildmode");
//                        enrichedGameProfile.setPreviousDisplayName(currentDisplayName);
//                        return;
//                    }
//                }
//
//                // handle admin-duty change
//                if (configuration.getOptions().notification().aDuty()) {
//                    if (!previousDisplayName.contains(A_DUTY_PREFIX) && currentDisplayName.contains(A_DUTY_PREFIX)) {
//                        sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_enter_a_duty");
//                    }
//
//                    if (previousDisplayName.contains(A_DUTY_PREFIX) && !currentDisplayName.contains(A_DUTY_PREFIX)) {
//                        sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_leave_a_duty");
//                    }
//                }
//
//                enrichedGameProfile.setPreviousDisplayName(currentDisplayName);
//            }
//        }
//    }

    @Unique
    private void sendChangeNotification(@NonNull EnrichedGameProfile enrichedGameProfile, String translationKey) {
        Component currentDisplayName = enrichedGameProfile.getCurrentDisplayName();
        if (currentDisplayName.equals(empty())) {
            return;
        }

        MutableComponent text = translatable(translationKey, currentDisplayName);
        notificationService.sendNotification(text, WHITE, 5000);
    }

    @Data
    @AllArgsConstructor
    public static class EnrichedGameProfile {

        private final GameProfile profile;
        private Component previousDisplayName;
        private Component currentDisplayName;

        public boolean isTeamMember() {
            return storage.getTeam().ucTeam().stream().anyMatch(teamMember -> teamMember.uuid().equals(this.profile.id()));
        }
    }
}
