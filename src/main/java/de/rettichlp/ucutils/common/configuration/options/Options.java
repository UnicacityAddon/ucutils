package de.rettichlp.ucutils.common.configuration.options;

import de.rettichlp.ucutils.common.gui.screens.components.CyclingButtonEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.ucutils.common.configuration.options.Options.ReinforcementType.UNICACITYADDON;
import static net.minecraft.ChatFormatting.AQUA;
import static net.minecraft.ChatFormatting.BLUE;
import static net.minecraft.ChatFormatting.DARK_AQUA;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;

@Getter
@Setter
@Accessors(fluent = true)
public class Options {

    private final NameTagOptions nameTag = new NameTagOptions();
    private final ChatOptions chatOptions = new ChatOptions();
    private final CarOptions car = new CarOptions();
    private final SoundOptions sound = new SoundOptions();
    private final NotificationOptions notification = new NotificationOptions();
    private final OtherOptions other = new OtherOptions();

    private boolean checkUnicacityServer = true;
    private ReinforcementType reinforcementType = UNICACITYADDON;

    @Getter
    @AllArgsConstructor
    @Accessors(fluent = false)
    public enum ReinforcementType implements CyclingButtonEntry {

        UCUTILS(empty()
                .append(literal("UC").withStyle(DARK_AQUA))
                .append(literal("Utils").withStyle(AQUA))),
        UNICACITYADDON(empty()
                .append(literal("U").withStyle(BLUE))
                .append(literal("nica"))
                .append(literal("C").withStyle(RED))
                .append(literal("ity"))
                .append(literal("A").withStyle(BLUE))
                .append(literal("ddon")));

        private final Component displayName;

        @Contract(value = " -> new", pure = true)
        @Override
        public @NotNull Tooltip getTooltip() {
            return create(this.displayName);
        }
    }
}
