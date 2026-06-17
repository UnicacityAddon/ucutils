package de.rettichlp.ucutils.listener.impl.job;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import de.rettichlp.ucutils.listener.ITickListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.Scoreboard;

import java.util.Collection;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.messageService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static java.lang.Double.compare;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static java.util.regex.Pattern.compile;
import static net.minecraft.world.scores.DisplaySlot.SIDEBAR;

@UCUtilsListener
public class GarbageManListener implements IMessageReceiveListener, ITickListener {

    private static final String GARBAGE_MAN_TEXT = "JobWaste";
    private static final Pattern GARBAGE_MAN_DROP_START = compile("^\\[Müllmann] Du hast genug Mülltonnen entleert\\.$");
    private static final Pattern GARBAGE_MAN_FINISHED = compile("^\\[Müllmann] Du hast den Job beendet\\.$");

    private boolean isDropStep = false;
    private long lastCommandExecution = 0;

    @Override
    public boolean onMessageReceive(Component text, String message) {
        Matcher garbageManDropStartMatcher = GARBAGE_MAN_DROP_START.matcher(message);
        if (garbageManDropStartMatcher.find()) {
            this.isDropStep = true;
            return true;
        }

        Matcher garbageManFinishedMatcher = GARBAGE_MAN_FINISHED.matcher(message);
        if (garbageManFinishedMatcher.find()) {
            this.isDropStep = false;
            return true;
        }

        return true;
    }

    @Override
    public void onTick() {
        // check if drop step
        if (!this.isDropStep) {
            return;
        }

        // check if in drop spot range
        WasteDropSpot nearestWasteDropSpot = getNearestWasteDropSpot();
        if (!player.getOnPos().closerToCenterThan(nearestWasteDropSpot.getDropSpot(), 3)) {
            return;
        }

        // check if waste left
        int wasteLeft = getWasteLeft(nearestWasteDropSpot);
        if (wasteLeft <= 0) {
            return;
        }

        // check dropwaste command cooldown
        long now = currentTimeMillis();
        if (now - this.lastCommandExecution < 5200) {
            return;
        }

        this.lastCommandExecution = now;
        commandService.sendCommand("dropwaste");

        utilService.delayedAction(() -> messageService.sendModMessage("5", true), 200);
        utilService.delayedAction(() -> messageService.sendModMessage("4", true), 1200);
        utilService.delayedAction(() -> messageService.sendModMessage("3", true), 2200);
        utilService.delayedAction(() -> messageService.sendModMessage("2", true), 3200);
        utilService.delayedAction(() -> messageService.sendModMessage("1", true), 4200);
    }

    private WasteDropSpot getNearestWasteDropSpot() {
        return stream(WasteDropSpot.values()).min((o1, o2) -> {
            Vec3 position = player.position();
            double distance1 = position.distanceTo(o1.getDropSpot());
            double distance2 = position.distanceTo(o2.getDropSpot());
            return compare(distance1, distance2);
        }).orElseThrow(() -> new IllegalStateException("This should never happen"));
    }

    private int getWasteLeft(WasteDropSpot wasteDropSpot) {
        assert Minecraft.getInstance().level != null; // cannot be null at this point
        Scoreboard scoreboard = Minecraft.getInstance().level.getScoreboard();

        Collection<PlayerScoreEntry> scoreboardEntries = getGarbageManScoreboard()
                .map(scoreboard::listPlayerScores)
                .orElse(emptyList());

        return scoreboardEntries.stream()
                .filter(playerScoreEntry -> playerScoreEntry.owner().equals("§e" + wasteDropSpot.getDisplayName() + "§8:"))
                .map(PlayerScoreEntry::value)
                .findFirst()
                .orElse(0);
    }

    private Optional<Objective> getGarbageManScoreboard() {
        assert Minecraft.getInstance().level != null; // cannot be null at this point
        Scoreboard scoreboard = Minecraft.getInstance().level.getScoreboard();
        Objective displayObjective = scoreboard.getDisplayObjective(SIDEBAR);
        return nonNull(displayObjective) && GARBAGE_MAN_TEXT.equals(displayObjective.getName()) ? Optional.of(displayObjective) : empty();
    }

    @Getter
    @AllArgsConstructor
    private enum WasteDropSpot {

        GLASS("Glas", new Vec3(884, 67, 349)),
        METAL("Metall", new Vec3(900, 67, 392)),
        WASTE("Abfall", new Vec3(908, 67, 361)),
        WOOD("Holz", new Vec3(876, 69, 376));

        private final String displayName;
        private final Vec3 dropSpot;
    }
}
