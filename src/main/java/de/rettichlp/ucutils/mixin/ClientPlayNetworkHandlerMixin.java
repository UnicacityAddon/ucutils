package de.rettichlp.ucutils.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.notificationService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.awt.Color.WHITE;
import static java.lang.System.currentTimeMillis;
import static java.util.Optional.ofNullable;
import static net.minecraft.ChatFormatting.BLUE;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.YELLOW;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER;
import static net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME;
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
    private final Map<UUID, Component> displayNames = new HashMap<>();

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
            ofNullable(this.displayNames.get(uuid)).ifPresent(displayName -> sendChangeNotification(displayName, "ucutils.notification.player_quit"));
        }
    }

    @Inject(method = "handlePlayerInfoUpdate", at = @At("HEAD"))
    private void ucutils$handlePlayerListActionHead(ClientboundPlayerInfoUpdatePacket packet, CallbackInfo ci) {
        if (!storage.isUnicaCity()) {
            return;
        }

        EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = packet.actions();
        List<ClientboundPlayerInfoUpdatePacket.Entry> entries = packet.entries();

        for (ClientboundPlayerInfoUpdatePacket.Entry entry : entries) {
            UUID profileId = entry.profileId();
            Component displayName = entry.displayName();

            if (displayName == null) {
                continue;
            }

            if (actions.contains(ADD_PLAYER)) {
                if (!configuration.getOptions().notification().joinQuit()) {
                    return;
                }

                // if the client joined the server few moments ago, hide notifications due to initial sync of player list
                if (currentTimeMillis() - storage.getJoinTimestamp() < 1000) {
                    return;
                }

                sendChangeNotification(displayName, "ucutils.notification.player_join");
            } else if (actions.contains(UPDATE_DISPLAY_NAME)) {
                Component previousDisplayName = this.displayNames.get(profileId);

                // handle report change
                if (configuration.getOptions().notification().report()) {
                    if (!previousDisplayName.contains(REPORT_PREFIX) && displayName.contains(REPORT_PREFIX)) {
                        sendChangeNotification(displayName, "ucutils.notification.player_enter_report");
                        this.displayNames.put(profileId, displayName);
                        continue;
                    }

                    if (previousDisplayName.contains(REPORT_PREFIX) && !displayName.contains(REPORT_PREFIX)) {
                        sendChangeNotification(displayName, "ucutils.notification.player_leave_report");
                        this.displayNames.put(profileId, displayName);
                        continue;
                    }
                }

                // handle build mode change
                if (configuration.getOptions().notification().buildMode()) {
                    if (!previousDisplayName.contains(BUILD_MODE_PREFIX) && displayName.contains(BUILD_MODE_PREFIX)) {
                        sendChangeNotification(displayName, "ucutils.notification.player_enter_buildmode");
                        this.displayNames.put(profileId, displayName);
                        continue;
                    }

                    if (previousDisplayName.contains(BUILD_MODE_PREFIX) && !displayName.contains(BUILD_MODE_PREFIX)) {
                        sendChangeNotification(displayName, "ucutils.notification.player_leave_buildmode");
                        this.displayNames.put(profileId, displayName);
                        continue;
                    }
                }

                // handle admin-duty change
                if (configuration.getOptions().notification().aDuty()) {
                    if (!previousDisplayName.contains(A_DUTY_PREFIX) && displayName.contains(A_DUTY_PREFIX)) {
                        sendChangeNotification(displayName, "ucutils.notification.player_enter_a_duty");
                        this.displayNames.put(profileId, displayName);
                        continue;
                    }

                    if (previousDisplayName.contains(A_DUTY_PREFIX) && !displayName.contains(A_DUTY_PREFIX)) {
                        sendChangeNotification(displayName, "ucutils.notification.player_leave_a_duty");
                        this.displayNames.put(profileId, displayName);
                        continue;
                    }
                }
            }

            this.displayNames.put(profileId, displayName);
        }
    }

    @Unique
    private void sendChangeNotification(Component displayName, String translationKey) {
        MutableComponent text = translatable(translationKey, displayName);
        notificationService.sendNotification(text, WHITE, 5000);
    }
}
