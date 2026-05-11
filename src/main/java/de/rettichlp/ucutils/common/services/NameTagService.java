package de.rettichlp.ucutils.common.services;

import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.LocalDateTime;

import static de.rettichlp.ucutils.UCUtils.networkHandler;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;
import static net.minecraft.scoreboard.AbstractTeam.CollisionRule.NEVER;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.literal;
import static net.minecraft.util.Formatting.BLUE;
import static net.minecraft.util.Formatting.BOLD;
import static net.minecraft.util.Formatting.DARK_GRAY;
import static net.minecraft.util.Formatting.DARK_GREEN;
import static net.minecraft.util.Formatting.DARK_RED;
import static net.minecraft.util.Formatting.GOLD;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.RED;
import static net.minecraft.util.Formatting.YELLOW;

public class NameTagService {

    public static final MutableText A_DUTY_TAG = empty()
            .append(literal("ᴀ").formatted(BLUE, BOLD))
            .append(literal("ᴅᴜᴛʏ").formatted(RED, BOLD));

    public static final MutableText AFK_TAG = literal("ᴀꜰᴋ").formatted(GOLD, BOLD);

    public static final MutableText HOUSE_BAN_TAG = literal("Hᴀᴜѕᴠᴇʀʙᴏᴛ").formatted(RED, BOLD);

    public static final MutableText OUTLAW_TAG = literal("Vᴏɢᴇʟꜰʀᴇɪ").formatted(RED, BOLD);

    private static final MutableText A_DUTY_PREFIX = empty()
            .append(literal("[").formatted(DARK_GRAY))
            .append(literal("UC").formatted(BLUE))
            .append(literal("]").formatted(DARK_GRAY));

    public boolean isAfk(String targetName) {
        return networkHandler.getPlayerList().stream()
                .filter(entry -> entry.getProfile().name().equals(targetName))
                .anyMatch(entry -> {
                    Team team = entry.getScoreboardTeam();
                    return team != null && !isADuty(targetName) && team.getCollisionRule() == NEVER;
                });
    }

    public boolean isADuty(String targetName) {
        return networkHandler.getPlayerList().stream()
                .filter(entry -> entry.getProfile().name().equals(targetName))
                .anyMatch(entry -> {
                    Text displayName = entry.getDisplayName();
                    return displayName != null && displayName.contains(A_DUTY_PREFIX);
                });
    }

    public @NotNull Formatting getWantedPointColor(int wantedPointAmount) {
        Formatting color;

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

    public MutableText getMedicInformation(String playerName) {
        MutableText text = empty();

        LocalDateTime bandageCooldownExpiration = storage.getMedicBandageCooldowns().getOrDefault(playerName, now());
        Duration bandageExpirationDuration = between(now(), bandageCooldownExpiration);
        if (bandageExpirationDuration.isPositive()) {
            text
                    .append(literal("Bandage").formatted(GRAY))
                    .append(literal(": ").formatted(DARK_GRAY))
                    .append(literal(bandageExpirationDuration.toSeconds() + "s"));
        }

        LocalDateTime pillCooldownExpiration = storage.getMedicPillCooldowns().getOrDefault(playerName, now());
        Duration pillExpirationDuration = between(now(), pillCooldownExpiration);
        if (pillExpirationDuration.isPositive()) {
            if (!text.getSiblings().isEmpty()) {
                text.append(" ");
            }

            text
                    .append(literal("Schmerzpille").formatted(GRAY))
                    .append(literal(": ").formatted(DARK_GRAY))
                    .append(literal(pillExpirationDuration.toSeconds() + "s"));
        }

        return text;
    }
}
