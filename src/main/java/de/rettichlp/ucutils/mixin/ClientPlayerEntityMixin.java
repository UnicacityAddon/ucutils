package de.rettichlp.ucutils.mixin;

import de.rettichlp.ucutils.common.models.BlackMarket;
import de.rettichlp.ucutils.common.models.Dealer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Predicate;

import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.models.Job.URANIUM_TRANSPORT;
import static java.lang.System.currentTimeMillis;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static net.minecraft.item.Items.GLASS_BOTTLE;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Unique
    private final static List<BlockPos> SHOP_LOCATIONS = List.of(
            new BlockPos(45, 69, 200),
            new BlockPos(1049, 69, -189)
    );

    @Unique
    private long lastBlackMarketAndDealerCheck = 0;

    @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
    private void ucutils$dropSelectedItemHead(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        if (!storage.isUnicaCity()) {
            return;
        }

        if (player.getMainHandStack().isOf(GLASS_BOTTLE) && isNearShop()) {
            // cancel drop
            cir.setReturnValue(null);

            // execute command
            commandService.sendCommand("sell pfand");
        }
    }

    @Inject(method = "move", at = @At("HEAD"))
    private void ucutils$moveHead(MovementType type, Vec3d movement, CallbackInfo ci) {
        BlockPos playerPos = player.getBlockPos();

        // mark the black market and dealer spots as visited if within 60 blocks
        if (currentTimeMillis() - this.lastBlackMarketAndDealerCheck >= 3000) { // every 3 seconds to reduce performance impact
            this.lastBlackMarketAndDealerCheck = currentTimeMillis();

            stream(BlackMarket.Type.values())
                    .filter(t -> t.getBlockPos().isWithinDistance(playerPos, 60))
                    .forEach(t -> {
                        // remove old type association if exists
                        storage.getBlackMarkets().removeIf(blackMarket -> blackMarket.getType() == t);

                        // check if black market was found there
                        Box box = player.getBoundingBox().expand(60);
                        Predicate<VillagerEntity> isBlackMarket = villagerEntity -> ofNullable(villagerEntity.getCustomName())
                                .map(text -> text.getString().contains("Schwarzmarkt"))
                                .orElse(false);

                        assert MinecraftClient.getInstance().world != null; // cannot be null at this point
                        boolean found = !MinecraftClient.getInstance().world.getEntitiesByClass(VillagerEntity.class, box, isBlackMarket).isEmpty();

                        // add new black market entry
                        BlackMarket blackMarket = new BlackMarket(t, now(), found);
                        storage.getBlackMarkets().add(blackMarket);
                        LOGGER.info("Marked black market spot as visited: {}", t);
                    });

            stream(Dealer.Type.values())
                    .filter(t -> t.getBlockPos().isWithinDistance(playerPos, 60))
                    .forEach(t -> {
                        // remove old type association if exists
                        storage.getDealers().removeIf(dealer -> dealer.getType() == t);

                        // check if black market was found there
                        Box box = player.getBoundingBox().expand(60);
                        Predicate<VillagerEntity> isBlackMarket = villagerEntity -> ofNullable(villagerEntity.getCustomName())
                                .map(text -> text.getString().contains("Dealer"))
                                .orElse(false);

                        assert MinecraftClient.getInstance().world != null; // cannot be null at this point
                        boolean found = !MinecraftClient.getInstance().world.getEntitiesByClass(VillagerEntity.class, box, isBlackMarket).isEmpty();

                        // add new black market entry
                        Dealer dealer = new Dealer(t, now(), found);
                        storage.getDealers().add(dealer);
                        LOGGER.info("Marked dealer spot as visited: {}", t);
                    });
        }

        // job specific stuff
        if (storage.getCurrentJob() != null) {
            if (storage.getCurrentJob() == URANIUM_TRANSPORT && player.getBlockPos().isWithinDistance(new BlockPos(1132, 68, 396), 2)) {
                commandService.sendCommand("dropuran");
            }
        }
    }

    @Unique
    private boolean isNearShop() {
        BlockPos playerPos = player.getBlockPos();
        return SHOP_LOCATIONS.stream()
                .anyMatch(blockPos -> playerPos.isWithinDistance(blockPos, 10));
    }
}
