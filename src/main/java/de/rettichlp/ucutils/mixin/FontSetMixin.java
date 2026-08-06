package de.rettichlp.ucutils.mixin;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.gui.font.FontSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FontSet.class)
public abstract class FontSetMixin {

    @Unique
    private static final IntSet EXCLUDED_RANDOM_GLYPHS = new IntOpenHashSet();

    static {
        "\uE000\uE001\uE002\uE003\uE004\uE005\uE006\uE007\uE008\uE009\uE00A\uE00B\uE00C\uE00D\uE00E\uE00F\uE010\uE011\uE012\uE013\uE014".codePoints().forEach(EXCLUDED_RANDOM_GLYPHS::add);
    }

    @ModifyVariable(method = "getRandomGlyph", at = @At("STORE"), name = "chars")
    private IntList ucutils$getRandomGlyphStore(IntList chars) {
        if (chars == null || chars.isEmpty()) {
            return chars;
        }

        IntList filtered = new IntArrayList(chars);
        filtered.removeIf(EXCLUDED_RANDOM_GLYPHS::contains);
        return filtered;
    }
}
