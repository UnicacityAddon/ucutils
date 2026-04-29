package de.rettichlp.ucutils.listener.impl.job;

import de.rettichlp.ucutils.common.models.Countdown;
import de.rettichlp.ucutils.common.models.Job;
import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import de.rettichlp.ucutils.listener.IMoveListener;
import de.rettichlp.ucutils.listener.INaviSpotReachedListener;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.UCUtils.utilService;
import static de.rettichlp.ucutils.common.models.Job.PIZZA_DELIVERY;
import static de.rettichlp.ucutils.common.models.Job.TOBACCO_PLANTATION;
import static de.rettichlp.ucutils.common.models.Job.URANIUM_TRANSPORT;
import static java.lang.Integer.parseInt;
import static java.time.Duration.between;
import static java.time.LocalDateTime.now;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.regex.Pattern.compile;

@UCUtilsListener
public class JobListener implements IMessageReceiveListener, IMoveListener, INaviSpotReachedListener {

    private static final String MINING_BOOSTER_COUNTDOWN_TITLE = "Mining XP-Booster";
    private static final Pattern TRANSPORT_DELIVER_PATTERN = compile("^\\[Transport] Du hast (eine Holz Lieferung|eine Kiste|eine Waffenkiste|ein Weizen Paket|eine Schwarzpulverkiste) abgeliefert( bei .+)?\\.$");
    private static final Pattern DRINK_TRANSPORT_DELIVER_PATTERN = compile("^\\[Bar] Du hast eine Flasche abgegeben!$");
    private static final Pattern PIZZA_JOB_TRANSPORT_GET_PIZZA_PATTERN = compile("^\\[Pizzalieferant] Sobald du 10 Pizzen dabei hast, wird dir deine erste Route angezeigt\\.$");
    private static final Pattern MINING_BOOSTER_PATTERN = compile("^ᴍɪɴɪɴɢ ┃ XP-Booster aktiviert: \\+(?<percent>\\d+)% für (?<seconds>\\d+)s$");

    private LocalDateTime miningBoosterExpirationTime;

    @Override
    public boolean onMessageReceive(Text text, String message) {
        Matcher transportDeliverMatcher = TRANSPORT_DELIVER_PATTERN.matcher(message);
        if (transportDeliverMatcher.find()) {
            utilService.delayedAction(() -> commandService.sendCommand("droptransport"), SECONDS.toMillis(10));
            return true;
        }

        Matcher drinkTransportDeliverMatcher = DRINK_TRANSPORT_DELIVER_PATTERN.matcher(message);
        if (drinkTransportDeliverMatcher.find()) {
            utilService.delayedAction(() -> commandService.sendCommand("dropdrink"), 2500);
            return true;
        }

        Matcher pizzaJobTransportGetPizzaMatcher = PIZZA_JOB_TRANSPORT_GET_PIZZA_PATTERN.matcher(message);
        if (pizzaJobTransportGetPizzaMatcher.find()) {
            utilService.delayedAction(() -> commandService.sendCommand("getpizza"), 2500);
            return true;
        }

        Matcher miningBoosterMatcher = MINING_BOOSTER_PATTERN.matcher(message);
        if (miningBoosterMatcher.find()) {
            int seconds = parseInt(miningBoosterMatcher.group("seconds"));
            LocalDateTime now = now();

            this.miningBoosterExpirationTime = this.miningBoosterExpirationTime != null && this.miningBoosterExpirationTime.isAfter(now)
                    ? this.miningBoosterExpirationTime.plusSeconds(seconds)
                    : now.plusSeconds(seconds);

            storage.getCountdowns().removeIf(countdown -> countdown.getTitle().equals(MINING_BOOSTER_COUNTDOWN_TITLE));
            storage.getCountdowns().add(new Countdown(MINING_BOOSTER_COUNTDOWN_TITLE, between(now, this.miningBoosterExpirationTime), () -> {}));
            return true;
        }

        // refresh job cooldowns
        Optional<Job> optionalJob = stream(Job.values())
                .filter(job -> job.getJobStartPattern().matcher(message).find())
                .findFirst();

        if (optionalJob.isPresent()) {
            Job job = optionalJob.get();
            storage.setCurrentJob(job);
            job.startCountdown();
            return true;
        }

        return true;
    }

    @Override
    public void onMove(BlockPos blockPos) {
        if (isNull(storage.getCurrentJob())) {
            return;
        }

        if (storage.getCurrentJob() == URANIUM_TRANSPORT && player.getBlockPos().isWithinDistance(new BlockPos(1132, 68, 396), 2)) {
            commandService.sendCommand("dropuran");
        }
    }

    @Override
    public void onNaviSpotReached() {
        if (isNull(storage.getCurrentJob())) {
            return;
        }

        if (storage.getCurrentJob() == PIZZA_DELIVERY && player.getBlockPos().isWithinDistance(new BlockPos(266, 69, 54), 2)) {
            commandService.sendCommand("getpizza");
            return;
        }

        if (storage.getCurrentJob() == TOBACCO_PLANTATION && player.getBlockPos().isWithinDistance(new BlockPos(-133, 69, -78), 3)) {
            commandService.sendCommand("droptabak");
        }
    }
}
