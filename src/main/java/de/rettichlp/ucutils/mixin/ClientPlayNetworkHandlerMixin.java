package de.rettichlp.ucutils.mixin;

import com.mojang.authlib.GameProfile;
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
import java.util.Optional;
import java.util.UUID;

import static de.rettichlp.ucutils.UCUtils.notificationService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.awt.Color.WHITE;
import static net.minecraft.text.Text.literal;
import static net.minecraft.text.Text.translatable;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Unique
    private static final Collection<EnrichedGameProfile> PLAYER_PROFILES = new HashSet<>();

    @Inject(method = "onPlayerRemove",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/network/PacketApplyBatcher;)V",
                     shift = AFTER))
    private void ucutils$onPlayerRemoveHead(PlayerRemoveS2CPacket packet, CallbackInfo ci) {
        if (!storage.isUnicaCity()) {
            return;
        }

        for (UUID uuid : packet.profileIds()) {
            Optional<EnrichedGameProfile> optionalEnrichedGameProfile = PLAYER_PROFILES.stream()
                    .filter(enrichedGameProfile -> enrichedGameProfile.profile().id().equals(uuid))
                    .filter(enrichedGameProfile -> !enrichedGameProfile.profile().name().startsWith("CIT-"))
                    .findFirst();

            if (optionalEnrichedGameProfile.isEmpty()) {
                return;
            }

            EnrichedGameProfile enrichedGameProfile = optionalEnrichedGameProfile.get();
            sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_quit");
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
        Text newDisplayName = receivedEntry.displayName();

        if (profile == null || profile.name() == null || profile.name().startsWith("CIT-")) {
            return;
        }

        EnrichedGameProfile enrichedGameProfile = new EnrichedGameProfile(profile, newDisplayName);
        PLAYER_PROFILES.removeIf(egp -> egp.profile().id().equals(profile.id()));
        PLAYER_PROFILES.add(enrichedGameProfile);

        switch (action) {
            case ADD_PLAYER -> sendChangeNotification(enrichedGameProfile, "ucutils.notification.player_join"); // handle join
        }
    }

    @Unique
    private void sendChangeNotification(EnrichedGameProfile enrichedGameProfile, String translationKey) {
        if (enrichedGameProfile.isTeamMember()) {
            notificationService.notificationSound(1);
        }

        MutableText text = translatable(translationKey, Optional.ofNullable(enrichedGameProfile.displayName()).orElse(literal(enrichedGameProfile.profile().name())));
        notificationService.sendNotification(text, WHITE, 5000);
    }

    public record EnrichedGameProfile(GameProfile profile, Text displayName) {

        public boolean isTeamMember() {
            return storage.getTeam().ucTeam().stream().anyMatch(teamMember -> teamMember.uuid().equals(this.profile.id()));
        }
    }
}
