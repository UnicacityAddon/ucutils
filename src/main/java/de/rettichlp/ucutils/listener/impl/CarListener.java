package de.rettichlp.ucutils.listener.impl;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IEntityRenderListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import de.rettichlp.ucutils.listener.IScreenOpenListener;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.renderService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.lang.Integer.parseInt;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;
import static net.minecraft.screen.slot.SlotActionType.PICKUP;
import static net.minecraft.util.Formatting.AQUA;

@UCUtilsListener
public class CarListener implements IEntityRenderListener, IMessageReceiveListener, IScreenOpenListener {

    private static final Pattern CAR_UNLOCK_PATTERN = compile("^\\[Car] Du hast deinen .+ aufgeschlossen\\.$");
    private static final Pattern CAR_LOCK_PATTERN = compile("^\\[Car] Du hast deinen .+ abgeschlossen\\.$");
    private static final Pattern CAR_LOCKED_OWN_PATTERN = compile("^\\[Car] Dein Fahrzeug ist abgeschlossen\\.$");
    private static final Pattern CAR_FIND_PATTERN = compile("^\\[Car] Das Fahrzeug befindet sich bei » X: (?<x>-?\\d+) \\| Y: (?<y>-?\\d+) \\| Z: (?<z>-?\\d+)$");

    @Override
    public void onEntityRender(WorldRenderContext context) {
        ClientWorld world = MinecraftClient.getInstance().world;

        if (world != null && configuration.getOptions().car().highlight()) {
            ofNullable(storage.getMinecartEntityToHighlight())
                    .map(minecartEntity -> world.getEntityById(minecartEntity.getId()))
                    .ifPresent(minecartEntity -> renderService.renderTextAboveEntity(context, minecartEntity, Text.of("🚗").copy().formatted(AQUA), 0.05F));
        }
    }

    @Override
    public boolean onMessageReceive(Text text, String message) {
        Matcher carUnlockMatcher = CAR_UNLOCK_PATTERN.matcher(message);
        if (carUnlockMatcher.find()) {
            storage.setCarLocked(false);
            return true;
        }

        Matcher carLockMatcher = CAR_LOCK_PATTERN.matcher(message);
        if (carLockMatcher.find()) {
            storage.setCarLocked(true);
            return true;
        }

        Matcher carLockedOwnMatcher = CAR_LOCKED_OWN_PATTERN.matcher(message);
        if (carLockedOwnMatcher.find()) {
            commandService.sendCommand("car lock");
            return true;
        }

        Matcher carFindMatcher = CAR_FIND_PATTERN.matcher(message);
        if (carFindMatcher.find()) {
            int x = parseInt(carFindMatcher.group("x"));
            int y = parseInt(carFindMatcher.group("y"));
            int z = parseInt(carFindMatcher.group("z"));
            commandService.sendCommand("navi " + x + "/" + y + "/" + z);
            return true;
        }

        return true;
    }

    @Override
    public void onScreenOpen(Screen screen, int scaledWidth, int scaledHeight) {
        ClientPlayerInteractionManager interactionManager = MinecraftClient.getInstance().interactionManager;

        if (nonNull(interactionManager) && screen instanceof GenericContainerScreen genericContainerScreen) {
            String titleString = genericContainerScreen.getTitle().getString();

            switch (titleString) {
                case "CarControl" -> {
                    if (configuration.getOptions().car().fastLock()) {
                        interactionManager.clickSlot(genericContainerScreen.getScreenHandler().syncId, 0, 0, PICKUP, player);
                    }
                }
                case "Fahrzeuge" -> {
                    if (configuration.getOptions().car().fastFind()) {
                        interactionManager.clickSlot(genericContainerScreen.getScreenHandler().syncId, 0, 0, PICKUP, player);
                    }
                }
            }
        }
    }
}
