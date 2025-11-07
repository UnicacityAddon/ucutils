package de.rettichlp.pkutils.common.gui.screens;

import de.rettichlp.pkutils.common.models.Faction;
import de.rettichlp.pkutils.common.models.FactionMember;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;

import java.time.LocalDateTime;

import static de.rettichlp.pkutils.PKUtils.api;
import static de.rettichlp.pkutils.PKUtils.commandService;
import static de.rettichlp.pkutils.PKUtils.renderService;
import static de.rettichlp.pkutils.common.gui.screens.FactionActivityScreen.SortingType.RANK;
import static de.rettichlp.pkutils.common.gui.screens.components.TableHeaderTextWidget.SortingDirection.DESCENDING;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.horizontal;
import static net.minecraft.text.Text.of;
import static net.minecraft.text.Text.translatable;

public abstract class FactionScreen extends OptionsScreen {

    protected final Faction faction;

    protected LocalDateTime from;
    protected LocalDateTime to;
    protected int offset;

    public FactionScreen(Faction faction, LocalDateTime from, LocalDateTime to) {
        super(new GameMenuScreen(true), of(faction.getDisplayName()));
        this.faction = faction;
        this.from = from;
        this.to = to;
    }

    public abstract void onOffsetChange(int newOffset);

    public abstract int entryCount();

    public abstract int pageLimit();

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        boolean mouseScroll = super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);

        int operant = 0;
        if (verticalAmount < 0) {
            operant = 1;
        } else if (verticalAmount > 0) {
            operant = -1;
        }

        this.offset = max(0, min(entryCount() - pageLimit(), this.offset + operant));
        onOffsetChange(this.offset);

        return mouseScroll;
    }

    public DirectionalLayoutWidget getMenuDirectionalLayoutWidget() {
        DirectionalLayoutWidget directionalLayoutWidget = horizontal().spacing(8);

        renderService.addButton(directionalLayoutWidget, translatable("pkutils.screen.faction.button.activity.name"), button -> {
            close();
            api.getFactionPlayerData(this.from, this.to, this.faction.getMembers().stream().map(FactionMember::playerName).toList(), factionPlayerDataResponse -> this.client.execute(() -> {
                FactionActivityScreen factionActivityScreen = new FactionActivityScreen(this.faction, this.from, this.to, factionPlayerDataResponse, RANK, DESCENDING);
                this.client.setScreen(factionActivityScreen);
            }));
        }, 100);

        directionalLayoutWidget.add(new EmptyWidget(132, 0));

        ButtonWidget buttonWidget = directionalLayoutWidget.add(new ButtonWidget.Builder(translatable("pkutils.screen.faction.button.blacklist.name"), button -> {
            close();
            api.getBlacklistReasons(this.faction, blacklistReasons -> this.client.execute(() -> {
                FactionBlacklistReasonScreen factionScreen = new FactionBlacklistReasonScreen(this.faction, this.from, this.to, blacklistReasons);
                this.client.setScreen(factionScreen);
            }));
        }).width(100).build());
        buttonWidget.active = this.faction.isBadFaction() || commandService.isSuperUser();

        return directionalLayoutWidget;
    }
}
