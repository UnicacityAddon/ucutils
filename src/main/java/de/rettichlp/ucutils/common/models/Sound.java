package de.rettichlp.ucutils.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static de.rettichlp.ucutils.UCUtils.MOD_ID;
import static net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI;
import static net.minecraft.resources.Identifier.fromNamespaceAndPath;
import static net.minecraft.sounds.SoundEvent.createVariableRangeEvent;

@Getter
@AllArgsConstructor
public enum Sound {

    BANK_ROBBERY("bank_robbery"),
    BOMB_SOUND("bomb"),
    CONTRACT_FULFILLED("contract.fulfilled"),
    CONTRACT_SET("contract.set"),
    FIRE("fire"),
    NOTIFICATION("notification"),
    REPORT("report"), // TODO use sound
    SERVICE("service");

    private final String path;

    @Contract(" -> new")
    public @NotNull Identifier getIdentifier() {
        return fromNamespaceAndPath(MOD_ID, this.path);
    }

    @Contract(" -> new")
    public @NotNull SoundEvent getSoundEvent() {
        return createVariableRangeEvent(getIdentifier());
    }

    public void play() {
        SimpleSoundInstance simpleSoundInstance = forUI(getSoundEvent(), 1.0F, 1.0F);
        Minecraft.getInstance().getSoundManager().play(simpleSoundInstance);
    }

    public void play(float pitch, float volume) {
        SimpleSoundInstance simpleSoundInstance = forUI(getSoundEvent(), pitch, volume);
        Minecraft.getInstance().getSoundManager().play(simpleSoundInstance);
    }
}
