package de.rettichlp.ucutils.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

import static java.util.Arrays.stream;
import static net.minecraft.client.util.ScreenshotRecorder.takeScreenshot;
import static net.minecraft.util.Util.getFormattedCurrentTime;

@Getter
@AllArgsConstructor
public enum ScreenshotType {

    ARREST("Verhaftung"),
    BLACKLIST("Blacklist"),
    CORRUPTION("Korruption"),
    DRUG("Drogeneinnahme"),
    EMERGENCY_SERVICE("Notruf"),
    EQUIP("Equip"),
    KILLS("Kills"),
    MAJOR_EVENT("Großeinsatz"),
    OTHER("Andere"),
    REINFORCEMENT("Reinforcement"),
    ROLEPLAY("Roleplay"),
    TICKET("Ticket");

    private static final File RUN_DIRECTORY = MinecraftClient.getInstance().runDirectory;

    private final String displayName;

    public void take(Consumer<File> onSuccess) {
        MinecraftClient.getInstance().execute(() -> takeScreenshot(MinecraftClient.getInstance().getFramebuffer(), nativeImage -> {
            try {
                File screenshotFile = getScreenshotFile();
                nativeImage.writeTo(screenshotFile);
                onSuccess.accept(screenshotFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    public @NotNull File getScreenshotDirectory() {
        File file = new File(RUN_DIRECTORY, "ucutils/screenshots/" + this.displayName.toLowerCase());
        file.mkdirs();
        return file;
    }

    private @NotNull File getScreenshotFile() {
        File screenshotDirectory = getScreenshotDirectory();
        String formattedCurrentTime = getFormattedCurrentTime();
        int i = 1;

        while (true) {
            File file = new File(screenshotDirectory, this.displayName.toLowerCase() + "_" + formattedCurrentTime + (i == 1 ? "" : "_" + i) + ".png");
            if (!file.exists()) {
                return file;
            }

            ++i;
        }
    }

    public static @NotNull Optional<ScreenshotType> fromDisplayName(String displayName) {
        return stream(values())
                .filter(screenshotType -> displayName.equalsIgnoreCase(screenshotType.getDisplayName()))
                .findFirst();
    }
}
