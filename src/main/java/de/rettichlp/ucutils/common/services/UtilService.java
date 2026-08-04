package de.rettichlp.ucutils.common.services;

import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.time.ZoneId;
import java.util.Timer;
import java.util.TimerTask;

import static de.rettichlp.ucutils.UCUtils.MOD_ID;
import static net.minecraft.world.item.Items.SKELETON_SKULL;
import static net.minecraft.world.item.Items.WITHER_SKELETON_SKULL;

public class UtilService {

    @Getter
    private final ZoneId serverZoneId = ZoneId.of("Europe/Berlin");

    public void delayedAction(Runnable runnable, long milliseconds) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Minecraft.getInstance().execute(runnable);
            }
        }, milliseconds);
    }

    public String getVersion() {
        return FabricLoader.getInstance().getModContainer(MOD_ID)
                .map(modContainer -> modContainer.getMetadata().getVersion().getFriendlyString())
                .orElseThrow(() -> new NullPointerException("Cannot find version"));
    }

    public static boolean isCorpse(@NonNull ItemEntity itemEntity) {
        ItemStack itemStack = itemEntity.getItem();
        if (!itemStack.is(SKELETON_SKULL) && !itemStack.is(WITHER_SKELETON_SKULL)) {
            return false;
        }

        if (!itemEntity.hasCustomName()) {
            return false;
        }

        Component customName = itemEntity.getCustomName();
        return customName != null && customName.getString().contains("✟");
    }
}
