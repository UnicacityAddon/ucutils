package de.rettichlp.ucutils.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.of;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.DARK_GREEN;
import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.Formatting.YELLOW;

@Getter
@AllArgsConstructor
public class BlackMarket {

    private final Type type;
    @Nullable
    private final LocalDateTime visitedAt;
    private boolean found; // whether the black market was found or not

    public Text getText() {
        return empty()
                .styled(style -> style
                        .withHoverEvent(new HoverEvent.ShowText(of("Klicke, um die Navigation zu starten").copy().formatted(GOLD)))
                        .withClickEvent(new ClickEvent.RunCommand(this.type.getNavigationCommand())))
                .append(of(this.type.getDisplayName())).append(" ")
                .append(of("(").copy().formatted(DARK_GRAY))
                .append(of("Besucht:").copy().formatted(GRAY)).append(" ")
                .append(getLastVisitedText())
                .append(of(")").copy().formatted(DARK_GRAY))
                .append(this.found ? of(" ←").copy().formatted(GREEN) : empty());
    }

    private Text getLastVisitedText() {
        if (isNull(this.visitedAt)) {
            return of("Nie").copy().formatted(GRAY);
        }

        Duration duration = Duration.between(this.visitedAt, now());
        long minutes = duration.toMinutes();

        if (minutes >= 60) {
            return of("vor über einer Stunde").copy().formatted(RED);
        }

        if (minutes == 0) {
            return of("Jetzt").copy().formatted(DARK_GREEN);
        }

        return of("vor " + minutes + (minutes == 1 ? " Minute" : " Minuten")).copy().formatted(YELLOW);
    }

    @Getter
    @AllArgsConstructor
    public enum Type {

        JAIL("Gefängnis", -777, 64, 143),
        PANEL_BUILDING("Plattenbau", 470, 69, 424),
        AIRPORT("Flughafen", -292, 69, 636),
        SH_PARK("Park an der Stadthalle", 58, 70, 355),
        FARM("Farm", 427, 82, 512),
        CINEMA("Kino", 770, 68, 331),
        CHURCH("Wohnwagen an der Kirche", -304, 71, -206),
        SUBWAY_MEXICAN("U-Bahn (Mexikanisches Kartell)", -94, 51, -34);

        private final String displayName;
        private final int x;
        private final int y;
        private final int z;

        @Contract(pure = true)
        public @NotNull String getNavigationCommand() {
            return "/navi " + this.x + "/" + this.y + "/" + this.z;
        }

        @Contract(value = " -> new", pure = true)
        public @NotNull BlockPos getBlockPos() {
            return new BlockPos(this.x, this.y, this.z);
        }
    }
}
