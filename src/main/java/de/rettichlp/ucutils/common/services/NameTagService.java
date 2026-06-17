package de.rettichlp.ucutils.common.services;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;

import static de.rettichlp.ucutils.UCUtils.networkHandler;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;
import static net.minecraft.ChatFormatting.BLUE;
import static net.minecraft.ChatFormatting.BOLD;
import static net.minecraft.ChatFormatting.DARK_GRAY;
import static net.minecraft.ChatFormatting.DARK_GREEN;
import static net.minecraft.ChatFormatting.DARK_RED;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.GRAY;
import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.ChatFormatting.YELLOW;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.world.scores.Team.CollisionRule.NEVER;

public class NameTagService {

    public static final Component AFK_TAG = literal("ᴀꜰᴋ").withStyle(GOLD, BOLD);

    private static final Component A_DUTY_PREFIX = empty()
            .append(literal("[").withStyle(DARK_GRAY))
            .append(literal("UC").withStyle(BLUE))
            .append(literal("]").withStyle(DARK_GRAY));

    public boolean isAfk(String targetName) {
        return networkHandler.getOnlinePlayers().stream()
                .filter(entry -> entry.getProfile().name().equals(targetName))
                .anyMatch(entry -> {
                    Team team = entry.getTeam();
                    return team != null && !isADuty(targetName) && team.getCollisionRule() == NEVER;
                });
    }

    public boolean isADuty(String targetName) {
        return networkHandler.getOnlinePlayers().stream()
                .filter(entry -> entry.getProfile().name().equals(targetName))
                .anyMatch(entry -> {
                    Component displayName = entry.getTabListDisplayName();
                    return displayName != null && displayName.contains(A_DUTY_PREFIX);
                });
    }

    public @NotNull ChatFormatting getWantedPointColor(int wantedPointAmount) {
        ChatFormatting color;

        if (wantedPointAmount >= 60) {
            color = DARK_RED;
        } else if (wantedPointAmount >= 50) {
            color = RED;
        } else if (wantedPointAmount >= 25) {
            color = GOLD;
        } else if (wantedPointAmount >= 15) {
            color = YELLOW;
        } else if (wantedPointAmount >= 2) {
            color = GREEN;
        } else {
            color = DARK_GREEN;
        }
        return color;
    }

    public MutableComponent getMedicInformation(String playerName) {
        MutableComponent text = empty();

        LocalDateTime bandageCooldownExpiration = storage.getMedicBandageCooldowns().getOrDefault(playerName, now());
        Duration bandageExpirationDuration = between(now(), bandageCooldownExpiration);
        if (bandageExpirationDuration.isPositive()) {
            text
                    .append(literal("Bandage").withStyle(GRAY))
                    .append(literal(": ").withStyle(DARK_GRAY))
                    .append(literal(bandageExpirationDuration.toSeconds() + "s"));
        }

        LocalDateTime pillCooldownExpiration = storage.getMedicPillCooldowns().getOrDefault(playerName, now());
        Duration pillExpirationDuration = between(now(), pillCooldownExpiration);
        if (pillExpirationDuration.isPositive()) {
            if (!text.getSiblings().isEmpty()) {
                text.append(" ");
            }

            text
                    .append(literal("Schmerzpille").withStyle(GRAY))
                    .append(literal(": ").withStyle(DARK_GRAY))
                    .append(literal(pillExpirationDuration.toSeconds() + "s"));
        }

        return text;
    }
}
