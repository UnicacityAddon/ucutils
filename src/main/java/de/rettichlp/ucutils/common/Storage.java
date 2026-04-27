package de.rettichlp.ucutils.common;

import de.rettichlp.ucutils.common.models.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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
    private final List<BlacklistEntry> blacklistEntries = new ArrayList<>();

    @Getter
    private final Map<Faction, List<BlacklistReason>> blacklistReasons = new HashMap<>();

    @Getter
    private final List<BlackMarket> blackMarkets = new ArrayList<>();

    @Getter
    private final List<ContractEntry> contractEntries = new ArrayList<>();

    @Getter
    private final List<Countdown> countdowns = new ArrayList<>();

    @Getter
    private final Set<FactionEntry> factionEntries = new HashSet<>();

    @Getter
    private final List<HousebanEntry> housebanEntries = new ArrayList<>();

    @Getter
    private final List<PlantEntry> plantEntries = new ArrayList<>();

    @Getter
    private final Map<String, Faction> playerFactionCache = new HashMap<>();

    @Getter
    private final List<Reinforcement> reinforcements = new ArrayList<>();

    @Getter
    private final Map<String, Integer> retrievedNumbers = new HashMap<>();

    @Getter
    private final List<WantedEntry> wantedEntries = new ArrayList<>();

    @Getter
    @Setter
    private int activeServices = 0;

    @Getter
    @Setter
    private boolean afk = false;

    @Getter
    @Setter
    private boolean carLocked = true;

    @Getter
    @Setter
    @Nullable
    private Job currentJob;

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
    private boolean unicaCity = false;

    @Getter
    @Setter
    private ToggledChat toggledChat = NONE;

    @Getter
    @Setter
    private double thirst = -1.0;

    {
        this.blackMarkets.addAll(stream(BlackMarket.Type.values())
                .map(type -> new BlackMarket(type, null, false))
                .toList());
    }

    public void print() {
        //activeShutdowns
        LOGGER.info("activeShutdowns[{}]: {}", this.activeShutdowns.size(), this.activeShutdowns);
        // blacklistEntries
        LOGGER.info("blacklistEntries[{}]: {}", this.blacklistEntries.size(), this.blacklistEntries);
        // blacklistReasons
        this.blacklistReasons.forEach((faction, blacklistReasons) -> LOGGER.info("blacklistReasons[{}:{}]: {}", faction, blacklistReasons.size(), blacklistReasons));
        // blackMarkets
        LOGGER.info("blackMarkets[{}]: {}", this.blackMarkets.size(), this.blackMarkets);
        // contractEntries
        LOGGER.info("contractEntries[{}]: {}", this.contractEntries.size(), this.contractEntries);
        // countdowns
        LOGGER.info("countdowns[{}]: {}", this.countdowns.size(), this.countdowns);
        // factionEntries
        this.factionEntries.forEach(factionEntry -> LOGGER.info("factionEntries[{}:{}]: {}", factionEntry.faction(), factionEntry.members().size(), factionEntry.members()));
        // housebanEntries
        LOGGER.info("housebanEntries[{}]: {}", this.housebanEntries.size(), this.housebanEntries);
        // playerFactionCache
        LOGGER.info("playerFactionCache[{}]: {}", this.playerFactionCache.size(), this.playerFactionCache);
        // reinforcements
        LOGGER.info("reinforcements[{}]: {}", this.reinforcements.size(), this.reinforcements);
        // retrievedNumbers
        LOGGER.info("retrievedNumbers[{}]: {}", this.retrievedNumbers.size(), this.retrievedNumbers);
        // wantedEntries
        LOGGER.info("wantedEntries[{}]: {}", this.wantedEntries.size(), this.wantedEntries);
        // activeServices
        LOGGER.info("activeServices: {}", this.activeServices);
        // afk
        LOGGER.info("afk: {}", this.afk);
        // carLocked
        LOGGER.info("carLocked: {}", this.carLocked);
        // currentJob
        LOGGER.info("currentJob: {}", this.currentJob);
        // lastReceivedSmsNumber
        LOGGER.info("lastReceivedSmsNumber: {}", this.lastReceivedSmsNumber);
        // minecartEntityToHighlight
        LOGGER.info("minecartEntityToHighlight: {}", this.minecartEntityToHighlight);
        // moneyAtmAmount
        LOGGER.info("moneyAtmAmount: {}", this.moneyAtmAmount);
        // unicaCity
        LOGGER.info("unicaCity: {}", this.unicaCity);
        // toggledChat
        LOGGER.info("toggledChat: {}", this.toggledChat);
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

    public void trackReinforcement(Reinforcement reinforcement) {
        // remove all previous reinforcements of the same sender
        this.reinforcements.removeIf(r -> r.getSenderPlayerName().equals(reinforcement.getSenderPlayerName()));
        // add new reinforcement
        this.reinforcements.add(reinforcement);
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
