package de.rettichlp.ucutils.common;

import de.rettichlp.ucutils.common.models.BlackMarket;
import de.rettichlp.ucutils.common.models.Countdown;
import de.rettichlp.ucutils.common.models.Dealer;
import de.rettichlp.ucutils.common.models.Faction;
import de.rettichlp.ucutils.common.models.FactionEntry;
import de.rettichlp.ucutils.common.models.FactionMember;
import de.rettichlp.ucutils.common.models.Ingredient;
import de.rettichlp.ucutils.common.models.Job;
import de.rettichlp.ucutils.common.models.PlantEntry;
import de.rettichlp.ucutils.common.models.Purity;
import de.rettichlp.ucutils.common.models.ShutdownReason;
import de.rettichlp.ucutils.common.models.TeamResponse;
import de.rettichlp.ucutils.common.models.WantedEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static de.rettichlp.ucutils.UCUtils.LOGGER;
import static de.rettichlp.ucutils.UCUtils.storage;
import static de.rettichlp.ucutils.common.Storage.ToggledChat.NONE;
import static de.rettichlp.ucutils.common.models.Faction.NULL;
import static java.util.Arrays.stream;
import static java.util.Optional.ofNullable;
import static net.minecraft.text.Text.translatable;

public class Storage {

    @Getter
    private final List<ShutdownReason> activeShutdowns = new ArrayList<>();

    @Getter
    private final List<BlackMarket> blackMarkets = new ArrayList<>();

    @Getter
    private final List<Countdown> countdowns = new ArrayList<>();

    @Getter
    private final List<Dealer> dealers = new ArrayList<>();

    @Getter
    private final Set<FactionEntry> factionEntries = new HashSet<>();

    @Getter
    private final Map<Ingredient, Map<Purity, Integer>> inventory = new HashMap<>();

    @Getter
    private final Map<String, LocalDateTime> medicBandageCooldowns = new HashMap<>();

    @Getter
    private final Map<String, LocalDateTime> medicPillCooldowns = new HashMap<>();

    @Getter
    private final List<PlantEntry> plantEntries = new ArrayList<>();

    @Getter
    private final Map<String, Faction> playerFactionCache = new HashMap<>();

    @Getter
    private final Map<String, Integer> retrievedNumbers = new HashMap<>();

    @Getter
    private final List<WantedEntry> wantedEntries = new ArrayList<>();

    @Getter
    @Setter
    private int activeServices = 0;

    @Getter
    @Setter
    private boolean carLocked = true;

    @Getter
    @Setter
    @Nullable
    private Job currentJob;

    @Getter
    @Setter
    private boolean dead = false;

    @Getter
    @Setter
    private String fBankDepositReason = "";

    @Getter
    @Setter
    private double hydration = -1.0;

    @Getter
    @Setter
    private long joinTimestamp = 0;

    @Getter
    @Setter
    private int lastReceivedSmsNumber = -1;

    @Getter
    @Setter
    private MinecartEntity minecartEntityToHighlight;

    @Getter
    @Setter
    private int moneyAtmAmount = 0;

    @Getter
    @Setter
    private boolean premium = false;

    @Getter
    @Setter
    private TeamResponse team;

    @Getter
    @Setter
    private ToggledChat toggledChat = NONE;

    @Getter
    @Setter
    private boolean unicaCity = false;

    {
        this.blackMarkets.addAll(stream(BlackMarket.Type.values())
                .map(type -> new BlackMarket(type, null, false))
                .toList());

        this.dealers.addAll(stream(Dealer.Type.values())
                .map(type -> new Dealer(type, null, false))
                .toList());
    }

    public void print() {
        //activeShutdowns
        LOGGER.info("activeShutdowns[{}]: {}", this.activeShutdowns.size(), this.activeShutdowns);
        // blackMarkets
        LOGGER.info("blackMarkets[{}]: {}", this.blackMarkets.size(), this.blackMarkets);
        // countdowns
        LOGGER.info("countdowns[{}]: {}", this.countdowns.size(), this.countdowns);
        // dealers
        LOGGER.info("dealers[{}]: {}", this.dealers.size(), this.dealers);
        // factionEntries
        this.factionEntries.forEach(factionEntry -> LOGGER.info("factionEntries[{}:{}]: {}", factionEntry.faction(), factionEntry.members().size(), factionEntry.members()));
        // inventory
        this.inventory.forEach((ingredient, ingredientMap) -> LOGGER.info("inventory[{}:{}]: {}", ingredient, ingredientMap.size(), ingredientMap));
        // medicBandageCooldowns
        LOGGER.info("medicBandageCooldowns[{}]: {}", this.medicBandageCooldowns.size(), this.medicBandageCooldowns);
        // medicPillCooldowns
        LOGGER.info("medicPillCooldowns[{}]: {}", this.medicPillCooldowns.size(), this.medicPillCooldowns);
        // playerFactionCache
        LOGGER.info("playerFactionCache[{}]: {}", this.playerFactionCache.size(), this.playerFactionCache);
        // retrievedNumbers
        LOGGER.info("retrievedNumbers[{}]: {}", this.retrievedNumbers.size(), this.retrievedNumbers);
        // wantedEntries
        LOGGER.info("wantedEntries[{}]: {}", this.wantedEntries.size(), this.wantedEntries);
        // activeServices
        LOGGER.info("activeServices: {}", this.activeServices);
        // carLocked
        LOGGER.info("carLocked: {}", this.carLocked);
        // currentJob
        LOGGER.info("currentJob: {}", this.currentJob);
        // dead
        LOGGER.info("dead: {}", this.dead);
        // fBankDepositReason
        LOGGER.info("fBankDepositReason: {}", this.fBankDepositReason);
        // hydration
        LOGGER.info("hydration: {}", this.hydration);
        // joinTimestamp
        LOGGER.info("joinTimestamp: {}", this.joinTimestamp);
        // lastReceivedSmsNumber
        LOGGER.info("lastReceivedSmsNumber: {}", this.lastReceivedSmsNumber);
        // minecartEntityToHighlight
        LOGGER.info("minecartEntityToHighlight: {}", this.minecartEntityToHighlight);
        // moneyAtmAmount
        LOGGER.info("moneyAtmAmount: {}", this.moneyAtmAmount);
        // premium
        LOGGER.info("premium: {}", this.premium);
        // team
        LOGGER.info("team: {}", this.team);
        // toggledChat
        LOGGER.info("toggledChat: {}", this.toggledChat);
        // unicaCity
        LOGGER.info("unicaCity: {}", this.unicaCity);
    }

    public Faction getCachedFaction(String playerName) {
        return ofNullable(this.playerFactionCache.get(playerName)).orElseGet(() -> storage.getFaction(playerName));
    }

    public Faction getFaction(String playerName) {
        Faction faction = this.factionEntries.stream()
                .filter(factionEntry -> factionEntry.members().stream()
                        .anyMatch(factionMember -> factionMember.username().equalsIgnoreCase(playerName)))
                .findFirst()
                .map(FactionEntry::faction)
                .orElse(NULL);

        this.playerFactionCache.put(playerName, faction);
        return faction;
    }

    public Optional<FactionMember> getFactionMember(String playerName) {
        return this.factionEntries.stream()
                .flatMap(factionEntry -> factionEntry.members().stream())
                .filter(factionMember -> factionMember.username().equals(playerName))
                .findFirst();
    }

    @Getter
    @AllArgsConstructor
    public enum ToggledChat {

        NONE("", translatable("ucutils.notification.toggled_chat.none")),
        D_CHAT("d", translatable("ucutils.notification.toggled_chat.d")),
        F_CHAT("f", translatable("ucutils.notification.toggled_chat.f")),
        W_CHAT("w", translatable("ucutils.notification.toggled_chat.w"));

        private final String command;
        private final Text toggleMessage;
    }
}
