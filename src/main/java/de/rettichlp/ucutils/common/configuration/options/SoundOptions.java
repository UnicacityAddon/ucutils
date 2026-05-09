package de.rettichlp.ucutils.common.configuration.options;

import de.rettichlp.ucutils.common.gui.screens.components.CyclingButtonEntry;
import de.rettichlp.ucutils.common.models.Faction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.ucutils.common.configuration.options.SoundOptions.MedicSelect.MEDIC;
import static de.rettichlp.ucutils.common.configuration.options.SoundOptions.StateSelect.STATE;
import static de.rettichlp.ucutils.common.models.Faction.FBI;
import static de.rettichlp.ucutils.common.models.Faction.POLIZEI;
import static de.rettichlp.ucutils.common.models.Faction.RETTUNGSDIENST;
import static net.minecraft.text.Text.translatable;

@Getter
@Setter
@Accessors(fluent = true)
public class SoundOptions {

    private StateSelect bankRobbery = STATE;
    private StateSelect bomb = STATE;
    private boolean contractFulfilled = true;
    private boolean contractSet = true;
    private MedicSelect fire = MEDIC;
    private boolean notification = true;
    private boolean report = false;
    private boolean service = true;

    public enum StateSelect implements CyclingButtonEntry {

        ALWAYS,
        STATE,
        NONE;

        @Contract(" -> new")
        @Override
        public @NotNull Text getDisplayName() {
            return translatable("ucutils.select_state." + name().toLowerCase() + ".name");
        }

        @Contract(" -> new")
        @Override
        public @NotNull Tooltip getTooltip() {
            return Tooltip.of(translatable("ucutils.select_state." + name().toLowerCase() + ".tooltip"));
        }

        public boolean verify(Faction faction) {
            return this == ALWAYS || (this == STATE && (faction == FBI || faction == POLIZEI || faction == RETTUNGSDIENST));
        }
    }

    public enum MedicSelect implements CyclingButtonEntry {

        ALWAYS,
        MEDIC,
        NONE;

        @Contract(" -> new")
        @Override
        public @NotNull Text getDisplayName() {
            return translatable("ucutils.select_medic." + name().toLowerCase() + ".name");
        }

        @Contract(" -> new")
        @Override
        public @NotNull Tooltip getTooltip() {
            return Tooltip.of(translatable("ucutils.select_medic." + name().toLowerCase() + ".tooltip"));
        }

        public boolean verify(Faction faction) {
            return this == ALWAYS || (this == MEDIC && faction == RETTUNGSDIENST);
        }
    }
}
