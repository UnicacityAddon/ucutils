package de.rettichlp.ucutils.listener.impl.job;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.regex.Pattern.compile;
import static javax.imageio.ImageIO.write;
import static net.minecraft.core.component.DataComponents.MAP_ID;
import static net.minecraft.world.InteractionHand.MAIN_HAND;
import static net.minecraft.world.item.Items.FILLED_MAP;
import static net.minecraft.world.item.Items.FISHING_ROD;
import static net.minecraft.world.item.MapItem.getSavedData;
import static net.minecraft.world.level.material.MapColor.getColorFromPackedId;

@UCUtilsListener
public class AnglerListener implements IMessageReceiveListener {

    private static final Pattern ANGLER_CAPTCHA_PATTERN = compile("^Lies den Code auf der Karte in deiner Nebenhand ab und schreib ihn in den Chat\\.$");
    private static final Pattern ANGLER_CAPTCHA_CONTINUED_PATTERN = compile("^Deine Combo wurde fortgesetzt! \\(\\d+\\)$");

    private static final ExecutorService CAPTCHA_IMAGE_WRITER = newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "ucutils-captcha-image-writer");
        thread.setDaemon(true);
        return thread;
    });

    @Override
    public boolean onMessageReceive(Component text, String message) {
        Minecraft client = Minecraft.getInstance();

        Matcher anglerCaptchaMatcher = ANGLER_CAPTCHA_PATTERN.matcher(message);
        if (anglerCaptchaMatcher.find()) {
            utilService.delayedAction(() -> {
                ItemStack offHandStack = player.getOffhandItem();
                if (offHandStack.is(FILLED_MAP)) {
                    MapId mapId = offHandStack.get(MAP_ID);
                    storage.setCaptchaMap(mapId);
                    saveCaptchaImage(mapId);
                }
            }, 500);

            return true;
        }

        Matcher anglerCaptchaContinuedMatcher = ANGLER_CAPTCHA_CONTINUED_PATTERN.matcher(message);
        if (anglerCaptchaContinuedMatcher.find()) {
            storage.setCaptchaMap(null);

            MultiPlayerGameMode gameMode = client.gameMode;
            if (gameMode == null || !player.getMainHandItem().is(FISHING_ROD)) {
                return true;
            }

            gameMode.useItem(player, MAIN_HAND);
            player.swing(MAIN_HAND);
            return true;
        }

        return true;
    }

    private void saveCaptchaImage(MapId mapId) {
        BufferedImage mapImage = getMapImage(mapId);
        if (mapImage == null) {
            return;
        }

        CAPTCHA_IMAGE_WRITER.execute(() -> {
            File outputFile = new File("screenshots/ucutils/captcha.png");

            try {
                outputFile.getParentFile().mkdirs();
                outputFile.createNewFile();
                write(mapImage, "PNG", outputFile);
            } catch (Exception e) {
                LOGGER.error("Failed to save captcha image", e);
            }
        });
    }

    private @Nullable BufferedImage getMapImage(MapId mapId) {
        assert Minecraft.getInstance().level != null;
        MapItemSavedData savedData = getSavedData(mapId, Minecraft.getInstance().level);
        if (savedData == null) {
            return null;
        }

        BufferedImage mapImage = new BufferedImage(128, 128, TYPE_INT_RGB);

        for (int i = 0; i < savedData.colors.length; i++) {
            int x = i % 128;
            int y = i / 128;

            int argb = getColorFromPackedId(savedData.colors[i]);
            mapImage.setRGB(x, y, argb);
        }

        return mapImage;
    }
}
