package de.rettichlp.pkutils.common.gui.screens;

import de.rettichlp.pkutils.common.api.response.FactionPlayerDataResponse;
import de.rettichlp.pkutils.common.gui.screens.components.TableHeaderTextWidget;
import de.rettichlp.pkutils.common.gui.screens.components.WeekSelectionWidget;
import de.rettichlp.pkutils.common.models.ActivityEntry;
import de.rettichlp.pkutils.common.models.Faction;
import de.rettichlp.pkutils.common.models.FactionEntry;
import de.rettichlp.pkutils.common.models.FactionMember;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.rettichlp.pkutils.PKUtils.api;
import static de.rettichlp.pkutils.PKUtils.player;
import static de.rettichlp.pkutils.PKUtils.storage;
import static de.rettichlp.pkutils.common.gui.screens.FactionActivityScreen.SortingType.NAME;
import static de.rettichlp.pkutils.common.gui.screens.FactionActivityScreen.SortingType.RANK;
import static de.rettichlp.pkutils.common.gui.screens.components.TableHeaderTextWidget.SortingDirection.NONE;
import static java.lang.String.valueOf;
import static java.util.Arrays.stream;
import static java.util.Collections.emptySet;
import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toCollection;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.horizontal;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.vertical;
import static net.minecraft.text.Text.of;
import static net.minecraft.util.Formatting.GREEN;
import static net.minecraft.util.Formatting.WHITE;

public class FactionActivityScreen extends FactionScreen {

    private static final TextRenderer TEXT_RENDERER = MinecraftClient.getInstance().textRenderer;

    private final List<FactionPlayerDataResponse> factionPlayerDataResponses;
    private SortingType sortingType;
    private TableHeaderTextWidget.SortingDirection sortingDirection;

    public FactionActivityScreen(Faction faction,
                                 LocalDateTime from,
                                 LocalDateTime to,
                                 List<FactionPlayerDataResponse> factionPlayerDataResponses,
                                 SortingType sortingType,
                                 TableHeaderTextWidget.SortingDirection sortingDirection) {
        super(faction, from, to);
        this.sortingType = sortingType;
        this.sortingDirection = sortingDirection;
        this.factionPlayerDataResponses = factionPlayerDataResponses;
    }

    @Override
    public void initBody() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(vertical().spacing(4));

        directionalLayoutWidget.add(getMenuDirectionalLayoutWidget(), Positioner::alignHorizontalCenter);

        WeekSelectionWidget weekSelectionWidget = new WeekSelectionWidget(this.from, this.to, button -> {
            this.from = this.from.minusWeeks(1);
            this.to = this.to.minusWeeks(1);
            reopen(true);
        }, button -> {
            this.from = this.from.plusWeeks(1);
            this.to = this.to.plusWeeks(1);
            reopen(true);
        });

        directionalLayoutWidget.add(weekSelectionWidget, Positioner::alignHorizontalCenter);

        directionalLayoutWidget.add(new EmptyWidget(0, 4)); // spacing
        directionalLayoutWidget.add(getHeaderDirectionalLayoutWidget(), Positioner::alignHorizontalCenter);
        directionalLayoutWidget.add(getMemberDirectionalLayoutWidget(), Positioner::alignHorizontalCenter);
        directionalLayoutWidget.forEachChild(this::addDrawableChild);
    }

    @Override
    public void onOffsetChange(int newOffset) {
        reopen(false);
    }

    @Override
    public int entryCount() {
        return this.factionPlayerDataResponses.size();
    }

    @Override
    public int pageLimit() {
        int contentHeight = this.layout.getContentHeight();
        return contentHeight / 25;
    }

    private void reopen(boolean withApiCall) {
        if (withApiCall) {
            List<String> playerNames = this.faction.getMembers().stream().map(FactionMember::playerName).toList();
            api.getFactionPlayerData(this.from, this.to, playerNames, factionPlayerDataResponses -> {
                FactionActivityScreen factionActivityScreen = new FactionActivityScreen(this.faction, this.from, this.to, factionPlayerDataResponses, this.sortingType, this.sortingDirection);
                this.client.execute(() -> this.client.setScreen(factionActivityScreen));
            });

            return;
        }

        FactionActivityScreen factionActivityScreen = new FactionActivityScreen(this.faction, this.from, this.to, this.factionPlayerDataResponses, this.sortingType, this.sortingDirection);
        this.client.execute(() -> this.client.setScreen(factionActivityScreen));
    }

    private @NotNull DirectionalLayoutWidget getHeaderDirectionalLayoutWidget() {
        List<ActivityEntry.Type> activityTypes = getActivityTypes();

        DirectionalLayoutWidget directionalLayoutWidget = vertical().spacing(4);

        DirectionalLayoutWidget directionalLayoutWidget1 = directionalLayoutWidget.add(horizontal().spacing(8));

        TextWidget userTextWidget = new TextWidget(of("User"), TEXT_RENDERER);
        userTextWidget.setWidth(168);
        directionalLayoutWidget1.add(userTextWidget);

        TextWidget activityTextWidget = new TextWidget(of("Aktivitäten"), TEXT_RENDERER);
        activityTextWidget.setWidth(activityTypes.size() * 80 + (activityTypes.size() - 1) * 8);
        directionalLayoutWidget1.add(activityTextWidget);

        DirectionalLayoutWidget directionalLayoutWidget2 = directionalLayoutWidget.add(horizontal().spacing(8), positioner -> positioner.marginBottom(16));

        TableHeaderTextWidget nameTableHeaderTextWidget = new TableHeaderTextWidget(of("Name"), sortingDirection -> {
            this.sortingType = NAME;
            this.sortingDirection = sortingDirection;
            reopen(false);
        }, this.sortingType == NAME ? this.sortingDirection : NONE);
        nameTableHeaderTextWidget.setWidth(80);
        directionalLayoutWidget2.add(nameTableHeaderTextWidget);

        TableHeaderTextWidget rangTableHeaderTextWidget = new TableHeaderTextWidget(of("Rang"), sortingDirection -> {
            this.sortingType = RANK;
            this.sortingDirection = sortingDirection;
            reopen(false);
        }, this.sortingType == RANK ? this.sortingDirection : NONE);
        rangTableHeaderTextWidget.setWidth(80);
        directionalLayoutWidget2.add(rangTableHeaderTextWidget);

        activityTypes.forEach(type -> {
            SortingType sortingType = SortingType.valueOf(type.name());
            TableHeaderTextWidget activityTableHeaderTextWidget = new TableHeaderTextWidget(of(type.getDisplayName()), sortingDirection -> {
                this.sortingType = sortingType;
                this.sortingDirection = sortingDirection;
                reopen(false);
            }, this.sortingType == sortingType ? this.sortingDirection : NONE);
            activityTableHeaderTextWidget.setWidth(80);
            directionalLayoutWidget2.add(activityTableHeaderTextWidget, Positioner::alignHorizontalCenter);
        });

        return directionalLayoutWidget;
    }

    private DirectionalLayoutWidget getMemberDirectionalLayoutWidget() {
        DirectionalLayoutWidget directionalLayoutWidget = vertical().spacing(4);

        getSortedFactionMembers().stream().skip(this.offset).limit(pageLimit()).forEach(factionMember -> {
            String factionMemberMinecraftName = factionMember.playerName();
            Formatting color = player.getGameProfile().getName().equals(factionMemberMinecraftName) ? GREEN : WHITE;

            Map<String, Long> playerActivities = this.factionPlayerDataResponses.stream()
                    .filter(factionPlayerDataResponse -> factionPlayerDataResponse.getMinecraftName().equals(factionMemberMinecraftName))
                    .findFirst()
                    .map(FactionPlayerDataResponse::getActivityCount)
                    .orElse(new HashMap<>());

            DirectionalLayoutWidget memberDirectionalLayoutWidget = directionalLayoutWidget.add(horizontal().spacing(8), positioner -> positioner.marginTop(4));

            TextWidget nameTextWidget = new TextWidget(of(factionMemberMinecraftName).copy().formatted(color), TEXT_RENDERER);
            nameTextWidget.setWidth(80);
            memberDirectionalLayoutWidget.add(nameTextWidget);

            TextWidget rangTextWidget = new TextWidget(of(valueOf(factionMember.rank())).copy().formatted(color), TEXT_RENDERER);
            rangTextWidget.setWidth(80);
            memberDirectionalLayoutWidget.add(rangTextWidget);

            getActivityTypes().forEach(type -> {
                long activityAmount = playerActivities.getOrDefault(type.name(), 0L);
                TextWidget activityEntryTextWidget = new TextWidget(of(valueOf(activityAmount)).copy().formatted(color), TEXT_RENDERER);
                activityEntryTextWidget.setWidth(80);
                memberDirectionalLayoutWidget.add(activityEntryTextWidget);
            });
        });

        return directionalLayoutWidget;
    }

    private @NotNull Set<FactionMember> getSortedFactionMembers() {
        Set<FactionMember> factionMembers = storage.getFactionEntries().stream()
                .filter(factionEntry -> factionEntry.faction() == this.faction)
                .findFirst()
                .map(FactionEntry::members)
                .orElse(emptySet());

        return this.sortingType.apply(factionMembers, this.factionPlayerDataResponses, this.sortingDirection);
    }

    private @NotNull @Unmodifiable List<ActivityEntry.Type> getActivityTypes() {
        return stream(ActivityEntry.Type.values())
                .filter(type -> type.isAllowedForFaction(this.faction))
                .toList();
    }

    public enum SortingType {

        NAME,
        RANK,
        ARREST,
        ARREST_KILL,
        EMERGENCY_SERVICE,
        MAJOR_EVENT,
        PARK_TICKET,
        REINFORCEMENT,
        REVIVE;

        @Contract("_, _, _ -> new")
        public @NotNull Set<FactionMember> apply(Collection<FactionMember> factionMembers,
                                                 List<FactionPlayerDataResponse> factionPlayerDataResponses,
                                                 TableHeaderTextWidget.SortingDirection sortingDirection) {
            Comparator<FactionMember> factionMemberComparator = switch (this) {
                case NAME -> comparing(FactionMember::playerName);
                case RANK -> comparingInt(FactionMember::rank);
                default -> comparingLong(factionMember -> factionPlayerDataResponses.stream()
                        .filter(factionPlayerDataResponse -> factionPlayerDataResponse.getMinecraftName().equals(factionMember.playerName()))
                        .findFirst()
                        .map(FactionPlayerDataResponse::getActivityCount)
                        .orElse(new HashMap<>()).getOrDefault(name(), 0L));
            };

            factionMemberComparator = sortingDirection.apply(factionMemberComparator);

            return factionMembers.stream().sorted(factionMemberComparator).collect(toCollection(LinkedHashSet::new));
        }
    }
}
