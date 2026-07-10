package de.rettichlp.ucutils.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.function.Consumer;

import static net.minecraft.client.Screenshot.takeScreenshot;
import static net.minecraft.util.Util.getFilenameFormattedDateTime;

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

    private static final File RUN_DIRECTORY = Minecraft.getInstance().gameDirectory;

    private final String displayName;

    public void take(Consumer<File> onSuccess) {
        Minecraft.getInstance().execute(() -> takeScreenshot(Minecraft.getInstance().getMainRenderTarget(), nativeImage -> {
            try {
                File screenshotFile = getScreenshotFile();
                nativeImage.writeToFile(screenshotFile);
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
        String name = this.displayName.toLowerCase() + "_" + getFilenameFormattedDateTime();
        int count = 1;
        File file;
        while ((file = new File(screenshotDirectory, name + (count == 1 ? "" : "_" + count) + ".png")).exists()) {
            ++count;
        }
        return file;
    }
}
