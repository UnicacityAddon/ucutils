package de.rettichlp.ucutils.listener.impl.job;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import de.rettichlp.ucutils.listener.INaviSpotReachedListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.player;
import static java.lang.Double.compare;
import static java.util.Arrays.stream;
import static java.util.regex.Pattern.compile;

@UCUtilsListener
public class DeepSeaFisherListener implements IMessageReceiveListener, INaviSpotReachedListener {

    private static final Pattern DEEP_SEA_FISHER_START_PATTERN = compile("^\\[Fischer] Mit /findschwarm kannst du dir den nächsten Fischschwarm anzeigen lassen\\.$");
    private static final Pattern DEEP_SEA_FISHER_SPOT_FOUND_PATTERN = compile("^\\[Fischer] Du hast einen Fischschwarm gefunden!$");
    private static final Pattern DEEP_SEA_FISHER_SPOT_LOST_PATTERN = compile("^\\[Fischer] Du hast dich dem Fischschwarm zu weit entfernt\\.$");
    private static final Pattern DEEP_SEA_FISHER_CATCH_SUCCESS_PATTERN = compile("^\\[Fischer] Du hast \\d+kg frischen Fisch gefangen! \\(\\d+kg\\)$");
    private static final Pattern DEEP_SEA_FISHER_CATCH_FAILURE_PATTERN = compile("^\\[Fischer] Du hast das Fischernetz verloren\\.\\.\\.$");
    private static final int NET_AMOUNT = 5;

    private Collection<DeepSeaFisherJobSpot> visitedDeepSeaFisherJobSpots = new ArrayList<>();
    private boolean onDeepSeaFisherSpot = false;
    private boolean canCatchFish = true;

    @Override
    public boolean onMessageReceive(Text text, String message) {
        Matcher deepSeaFisherStartMatcher = DEEP_SEA_FISHER_START_PATTERN.matcher(message);
        if (deepSeaFisherStartMatcher.find()) {
            this.visitedDeepSeaFisherJobSpots = new ArrayList<>();
            DeepSeaFisherJobSpot.SPOT_1.startNavigation();
            return true;
        }

        Matcher deepSeaFisherSpotFoundMatcher = DEEP_SEA_FISHER_SPOT_FOUND_PATTERN.matcher(message);
        if (deepSeaFisherSpotFoundMatcher.find()) {
            commandService.sendCommand("stoproute");
            this.onDeepSeaFisherSpot = true;

            if (this.canCatchFish) {
                startFishing();
            }

            return true;
        }

        Matcher deepSeaFisherSpotLostMatcher = DEEP_SEA_FISHER_SPOT_LOST_PATTERN.matcher(message);
        if (deepSeaFisherSpotLostMatcher.find()) {
            this.onDeepSeaFisherSpot = false;
            return true;
        }

        Matcher deepSeaFisherCatchSuccessMatcher = DEEP_SEA_FISHER_CATCH_SUCCESS_PATTERN.matcher(message);
        Matcher deepSeaFisherCatchFailureMatcher = DEEP_SEA_FISHER_CATCH_FAILURE_PATTERN.matcher(message);
        if (deepSeaFisherCatchSuccessMatcher.find() || deepSeaFisherCatchFailureMatcher.find()) {
            this.canCatchFish = true;

            // if already on the next fishing spot, start fishing again
            if (this.onDeepSeaFisherSpot) {
                startFishing();
            }
        }

        return true;
    }

    @Override
    public void onNaviSpotReached() {
        if (this.visitedDeepSeaFisherJobSpots.size() == NET_AMOUNT) {
            this.visitedDeepSeaFisherJobSpots = new ArrayList<>();
            commandService.sendCommand("dropfish");
        }
    }

    private void startFishing() {
        this.onDeepSeaFisherSpot = false;
        this.canCatchFish = false;
        commandService.sendCommand("catchfish");

        // add the current spot to visited spots
        getNearestFisherJobSpot(getNotVisitedFisherJobSpots()).ifPresent(this.visitedDeepSeaFisherJobSpots::add);

        if (this.visitedDeepSeaFisherJobSpots.size() == NET_AMOUNT) {
            return;
        }

        // get nearest next spot and start navigation
        Optional<DeepSeaFisherJobSpot> nearestNextFisherJobSpot = getNearestFisherJobSpot(getNotVisitedFisherJobSpots());
        nearestNextFisherJobSpot.ifPresent(DeepSeaFisherJobSpot::startNavigation);
    }

    private @NotNull Optional<DeepSeaFisherJobSpot> getNearestFisherJobSpot(@NotNull Collection<DeepSeaFisherJobSpot> deepSeaFisherJobSpots) {
        return deepSeaFisherJobSpots.stream()
                .min((spot1, spot2) -> {
                    double distance1 = player.squaredDistanceTo(spot1.getPosition().getX(), spot1.getPosition().getY(), spot1.getPosition().getZ());
                    double distance2 = player.squaredDistanceTo(spot2.getPosition().getX(), spot2.getPosition().getY(), spot2.getPosition().getZ());
                    return compare(distance1, distance2);
                });
    }

    private @NotNull @Unmodifiable List<DeepSeaFisherJobSpot> getNotVisitedFisherJobSpots() {
        return stream(DeepSeaFisherJobSpot.values())
                .filter(deepSeaFisherJobSpot -> !this.visitedDeepSeaFisherJobSpots.contains(deepSeaFisherJobSpot))
                .toList();
    }

    @Getter
    @AllArgsConstructor
    private enum DeepSeaFisherJobSpot {

        SPOT_1(new BlockPos(-571, 63, 160)),
        SPOT_2(new BlockPos(-554, 63, 107)),
        SPOT_3(new BlockPos(-568, 63, 50)),
        SPOT_4(new BlockPos(-522, 63, 10)),
        SPOT_5(new BlockPos(-521, 63, 78));

        private final BlockPos position;

        public void startNavigation() {
            String naviCommandString = "navi " + this.position.getX() + "/" + this.position.getY() + "/" + this.position.getZ();
            commandService.sendCommand(naviCommandString);
        }
    }
}
