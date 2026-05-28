package de.rettichlp.ucutils.listener.impl.job;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import lombok.NonNull;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.util.regex.Pattern.compile;
import static net.minecraft.block.MapColor.getRenderColor;
import static net.minecraft.component.DataComponentTypes.MAP_ID;
import static net.minecraft.item.FilledMapItem.getMapState;
import static net.minecraft.item.Items.FILLED_MAP;
import static net.minecraft.util.Hand.MAIN_HAND;

@UCUtilsListener
public class AnglerListener implements IMessageReceiveListener {

    private static final Pattern ANGLER_CAPTCHA_PATTERN = compile("^\\[AntiBot] Zufällige Überprüfung$");
    private static final Pattern ANGLER_CAPTCHA_CONTINUED_PATTERN = compile("^Deine Combo wurde fortgesetzt! \\(\\d+\\)$");

    @Override
    public boolean onMessageReceive(Text text, String message) {
        MinecraftClient client = MinecraftClient.getInstance();

        Matcher anglerCaptchaMatcher = ANGLER_CAPTCHA_PATTERN.matcher(message);
        if (anglerCaptchaMatcher.find()) {
            ItemStack offHandStack = player.getOffHandStack();
            if (!offHandStack.isOf(FILLED_MAP)) {
                return true;
            }

            storage.setCaptchaMap(offHandStack.get(MAP_ID));
            return true;
        }

        Matcher anglerCaptchaContinuedMatcher = ANGLER_CAPTCHA_CONTINUED_PATTERN.matcher(message);
        if (anglerCaptchaContinuedMatcher.find()) {
            ClientPlayerInteractionManager interactionManager = client.interactionManager;
            if (interactionManager == null) {
                return true;
            }

            interactionManager.interactItem(player, MAIN_HAND);
            player.swingHand(MAIN_HAND);
            return true;
        }

        return true;
    }

    private @Nullable BufferedImage getMapImage(@NonNull ItemStack mapItemStack) {
        MapIdComponent mapId = mapItemStack.get(MAP_ID);
        if (mapId == null) {
            return null;
        }

        MapState mapState = getMapState(mapId, MinecraftClient.getInstance().world);
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
