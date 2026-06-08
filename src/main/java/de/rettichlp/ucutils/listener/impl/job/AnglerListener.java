package de.rettichlp.ucutils.listener.impl.job;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.util.regex.Pattern.compile;
import static javax.imageio.ImageIO.write;
import static net.minecraft.block.MapColor.getRenderColor;
import static net.minecraft.component.DataComponentTypes.MAP_ID;
import static net.minecraft.item.FilledMapItem.getMapState;
import static net.minecraft.item.Items.FILLED_MAP;
import static net.minecraft.item.Items.FISHING_ROD;
import static net.minecraft.util.Hand.MAIN_HAND;

@UCUtilsListener
public class AnglerListener implements IMessageReceiveListener {

    private static final Pattern ANGLER_CAPTCHA_PATTERN = compile("^Lies den Code auf der Karte in deiner Nebenhand ab und schreib ihn in den Chat\\.$");
    private static final Pattern ANGLER_CAPTCHA_CONTINUED_PATTERN = compile("^Deine Combo wurde fortgesetzt! \\(\\d+\\)$");

    @Override
    public boolean onMessageReceive(Text text, String message) {
        MinecraftClient client = MinecraftClient.getInstance();

        Matcher anglerCaptchaMatcher = ANGLER_CAPTCHA_PATTERN.matcher(message);
        if (anglerCaptchaMatcher.find()) {
            utilService.delayedAction(() -> {
                ItemStack offHandStack = player.getOffHandStack();
                if (offHandStack.isOf(FILLED_MAP)) {
                    MapIdComponent captchaMap = offHandStack.get(MAP_ID);
                    storage.setCaptchaMap(captchaMap);
                    saveCaptchaImage(captchaMap);
                }
            }, 500);

            return true;
        }

        Matcher anglerCaptchaContinuedMatcher = ANGLER_CAPTCHA_CONTINUED_PATTERN.matcher(message);
        if (anglerCaptchaContinuedMatcher.find()) {
            storage.setCaptchaMap(null);

            ClientPlayerInteractionManager interactionManager = client.interactionManager;
            if (interactionManager == null || !player.getMainHandStack().isOf(FISHING_ROD)) {
                return true;
            }

            interactionManager.interactItem(player, MAIN_HAND);
            player.swingHand(MAIN_HAND);
            return true;
        }

        return true;
    }

    private void saveCaptchaImage(MapIdComponent captchaMap) {
        BufferedImage mapImage = getMapImage(captchaMap);
        if (mapImage == null) {
            return;
        }

        File outputFile = new File("screenshots/ucutils/captcha.png");

        try {
            outputFile.mkdirs();
            outputFile.createNewFile();
            write(mapImage, "PNG", outputFile);
        } catch (Exception e) {
            LOGGER.error("Failed to save captcha image", e);
        }
    }

    private @Nullable BufferedImage getMapImage(MapIdComponent mapIdComponent) {
        MapState mapState = getMapState(mapIdComponent, MinecraftClient.getInstance().world);
        if (mapState == null) {
            return null;
        }

        BufferedImage mapImage = new BufferedImage(128, 128, TYPE_INT_RGB);

        for (int i = 0; i < mapState.colors.length; i++) {
            int x = i % 128;
            int y = i / 128;

            int argb = getRenderColor(mapState.colors[i]);
            mapImage.setRGB(x, y, argb);
        }

        return mapImage;
    }
}
