package de.rettichlp.ucutils.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.lang.Character.isUpperCase;
import static java.util.regex.Pattern.compile;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Unique
    private static final Pattern COMMAND_NAVI_HOUSE_NUMBER_PATTERN = compile("^navi (?<number>\\d+)$");

    @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
    private void ucutils$sendChatCommandHead(String command, CallbackInfo ci) {
        if (!storage.isUnicaCity()) {
            return;
        }

        // allow uppercase command labes and migrate them to lowercase
        String[] parts = command.split(" ", 2); // split the message into command label and arguments
        String commandLabel = parts[0]; // get the command label

        if (containsUppercase(commandLabel)) {
            String labelLowerCase = commandLabel.toLowerCase(); // get the lowercase command label

            StringJoiner stringJoiner = new StringJoiner(" ");
            stringJoiner.add(labelLowerCase);

            if (parts.length > 1) {
                stringJoiner.add(parts[1]);
            }

            commandService.sendCommand(stringJoiner.toString());
            LOGGER.info("UCUtils blocked command execution: /{}", command);
            ci.cancel();
            return;
        }

        // support /navi <house-number>
        Matcher commandNaviHouseNumberMatcher = COMMAND_NAVI_HOUSE_NUMBER_PATTERN.matcher(command);
        if (commandNaviHouseNumberMatcher.find()) {
            String number = commandNaviHouseNumberMatcher.group("number");
            commandService.sendCommand("navi Haus:" + number);
            LOGGER.info("UCUtils blocked command execution: /{}", command);
            ci.cancel();
        }
    }

    @Unique
    private boolean containsUppercase(@NotNull String input) {
        for (char c : input.toCharArray()) {
            if (isUpperCase(c)) {
                return true;
            }
        }

        return false;
    }
}
