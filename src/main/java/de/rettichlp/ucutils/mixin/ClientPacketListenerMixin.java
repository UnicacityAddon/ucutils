package de.rettichlp.ucutils.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.util.Objects.requireNonNull;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static org.spongepowered.asm.mixin.injection.At.Shift.AFTER;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleSetEntityData",
            at = @At(value = "INVOKE",
                     target = "Lnet/minecraft/network/syncher/SynchedEntityData;assignValues(Ljava/util/List;)V",
                     shift = AFTER))
    private void ucutils$handleSetEntityDataInvoke(ClientboundSetEntityDataPacket packet, CallbackInfo ci) {
        if (!storage.isUnicaCity()) {
            return;
        }

        Level world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }

        Entity entity = world.getEntity(packet.id());
        if (!(entity instanceof Villager villager) || !villager.hasCustomName()) {
            return;
        }

        String customNameString = requireNonNull(villager.getCustomName()).getString();
        Vec3 entityPos = villager.position();

        // already notified check
        if (entityPos.equals(storage.getDealerPosition()) || entityPos.equals(storage.getBlackMarketPosition())) {
            return;
        }

        switch (customNameString) {
            case "Dealer" -> {
                storage.setBlackMarketPosition(entityPos);
                player.sendSystemMessage(empty()
                        .append(literal("[").withStyle(DARK_GRAY))
                        .append(literal("Dealer").withStyle(RED))
                        .append(literal("] ").withStyle(DARK_GRAY))
                        .append(literal("Der Dealer ist in der Nähe!").withStyle(GRAY)));
            }
            case "Schwarzmarkt" -> {
                storage.setDealerPosition(entityPos);
                player.sendSystemMessage(empty()
                        .append(literal("[").withStyle(DARK_GRAY))
                        .append(literal("Schwarzmarkt").withStyle(RED))
                        .append(literal("] ").withStyle(DARK_GRAY))
                        .append(literal("Der Schwarzmarkt ist in der Nähe!").withStyle(GRAY)));
            }
        }
    }
}
