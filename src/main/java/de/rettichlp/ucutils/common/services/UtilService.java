package de.rettichlp.ucutils.common.services;

import lombok.Getter;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.time.ZoneId;
import java.util.concurrent.ScheduledExecutorService;

import static de.rettichlp.ucutils.UCUtils.MOD_ID;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static net.minecraft.world.item.Items.SKELETON_SKULL;
import static net.minecraft.world.item.Items.WITHER_SKELETON_SKULL;

public class UtilService {

    @Getter
    private final ZoneId serverZoneId = ZoneId.of("Europe/Berlin");

    private final ScheduledExecutorService delayedActionScheduler = newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "ucutils-delayed-action");
        thread.setDaemon(true);
        return thread;
    });

    public void delayedAction(Runnable runnable, long milliseconds) {
        this.delayedActionScheduler.schedule(() -> Minecraft.getInstance().execute(runnable), milliseconds, MILLISECONDS);
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
