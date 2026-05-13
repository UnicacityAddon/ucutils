package de.rettichlp.ucutils.mixin;

import com.mojang.authlib.GameProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRemoveS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import static de.rettichlp.ucutils.UCUtils.notificationService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.awt.Color.WHITE;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.translatable;
import static net.minecraft.util.Formatting.BLUE;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.YELLOW;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Unique
    private static final MutableText A_DUTY_PREFIX = empty()
            .append(literal("[").formatted(DARK_GRAY)
                    .append(literal("UC").formatted(BLUE))
                    .append(literal("]").formatted(DARK_GRAY)));

    @Unique
    private static final MutableText BUILD_MODE_PREFIX = empty()
            .append(literal("[").formatted(DARK_GRAY)
                    .append(literal("B").formatted(YELLOW))
                    .append(literal("]").formatted(DARK_GRAY)));

    @Unique
    private static final MutableText REPORT_PREFIX = empty()
            .append(literal("[").formatted(DARK_GRAY)
                    .append(literal("R").formatted(GOLD))
                    .append(literal("]").formatted(DARK_GRAY)));

    @Unique
    private final Collection<EnrichedGameProfile> enrichedGameProfiles = new HashSet<>();

    @Inject(method = "onPlayerRemove",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/network/PacketApplyBatcher;)V",
                     shift = AFTER))
    private void ucutils$onPlayerRemoveHead(PlayerRemoveS2CPacket packet, CallbackInfo ci) {
        if (!storage.isUnicaCity()) {
            return;
        }

        for (UUID uuid : packet.profileIds()) {
            this.enrichedGameProfiles.stream()
                    .filter(egp -> egp.getProfile().id().equals(uuid))
                    .findFirst()
                    .ifPresent(egp -> sendChangeNotification(egp, "ucutils.notification.player_quit"));
        }
    }

    @Inject(method = "handlePlayerListAction", at = @At("HEAD"))
    private void ucutils$handlePlayerListActionHead(PlayerListS2CPacket.Action action,
                                                    PlayerListS2CPacket.Entry receivedEntry,
                                                    PlayerListEntry currentEntry,
                                                    CallbackInfo ci) {
        if (!storage.isUnicaCity()) {
            return;
        }

        GameProfile profile = receivedEntry.profile();
        UUID profileId = receivedEntry.profileId();
        Text currentDisplayName = receivedEntry.displayName();

        if (currentDisplayName == null) {
            return;
        }

        switch (action) {
            case ADD_PLAYER -> {
                EnrichedGameProfile enrichedGameProfile = new EnrichedGameProfile(profile, currentDisplayName, currentDisplayName);
                this.enrichedGameProfiles.removeIf(egp -> egp.getProfile().id().equals(profileId));
                this.enrichedGameProfiles.add(enrichedGameProfile);
                sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_join");
            }
            case UPDATE_DISPLAY_NAME -> {
                EnrichedGameProfile enrichedGameProfile = this.enrichedGameProfiles.stream()
                        .filter(egp -> egp.getProfile().id().equals(profileId))
                        .findFirst()
                        .orElseGet(() -> {
                            EnrichedGameProfile egp = new EnrichedGameProfile(profile, currentDisplayName, currentDisplayName);
                            this.enrichedGameProfiles.add(egp);
                            return egp;
                        });

                Text previousDisplayName = enrichedGameProfile.getPreviousDisplayName();

                // handle admin-duty change

                if (!previousDisplayName.contains(A_DUTY_PREFIX) && currentDisplayName.contains(A_DUTY_PREFIX)) {
                    sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_enter_a_duty");
                }

                if (previousDisplayName.contains(A_DUTY_PREFIX) && !currentDisplayName.contains(A_DUTY_PREFIX)) {
                    sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_leave_a_duty");
                }

                // handle build mode change

                if (!previousDisplayName.contains(BUILD_MODE_PREFIX) && currentDisplayName.contains(BUILD_MODE_PREFIX)) {
                    sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_enter_buildmode");
                }

                if (previousDisplayName.contains(BUILD_MODE_PREFIX) && !currentDisplayName.contains(BUILD_MODE_PREFIX)) {
                    sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_leave_buildmode");
                }

                // handle report change

                if (!previousDisplayName.contains(REPORT_PREFIX) && currentDisplayName.contains(REPORT_PREFIX)) {
                    sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_enter_report");
                }

                if (previousDisplayName.contains(REPORT_PREFIX) && !currentDisplayName.contains(REPORT_PREFIX)) {
                    sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_leave_report");
                }

                enrichedGameProfile.setPreviousDisplayName(currentDisplayName);
            }
        }
    }

    @Unique
    private void sendChangeNotification(@NonNull EnrichedGameProfile enrichedGameProfile, String translationKey) {
        Text currentDisplayName = enrichedGameProfile.getCurrentDisplayName();
        if (currentDisplayName.equals(empty())) {
            return;
        }

        MutableText text = translatable(translationKey, currentDisplayName);
        notificationService.sendNotification(text, WHITE, 5000);
    }

    @Data
    @AllArgsConstructor
    public static class EnrichedGameProfile {

        private final GameProfile profile;
        private Text previousDisplayName;
        private Text currentDisplayName;

        public boolean isTeamMember() {
            return storage.getTeam().ucTeam().stream().anyMatch(teamMember -> teamMember.uuid().equals(this.profile.id()));
        }
    }
}
