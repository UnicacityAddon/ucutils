package de.rettichlp.ucutils.common.gui.screens;

import de.rettichlp.ucutils.common.models.BlacklistReason;
import de.rettichlp.ucutils.common.models.Faction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.widget.Widget;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static de.rettichlp.ucutils.UCUtils.api;
import static de.rettichlp.ucutils.UCUtils.renderService;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.horizontal;
import static net.minecraft.client.gui.widget.DirectionalLayoutWidget.vertical;
import static net.minecraft.text.Text.empty;
import static net.minecraft.text.Text.of;
import static net.minecraft.text.Text.translatable;

public class FactionBlacklistReasonScreen extends FactionScreen {

    private static final TextRenderer TEXT_RENDERER = MinecraftClient.getInstance().textRenderer;

    private final List<BlacklistReason> blacklistReasons;
    private final TextFieldWidget newReasonTextFieldWidget;
    private final TextFieldWidget newPriceTextFieldWidget;
    private final TextFieldWidget newKillsTextFieldWidget;

    public FactionBlacklistReasonScreen(Faction faction,
                                        LocalDateTime from,
                                        LocalDateTime to,
                                        List<BlacklistReason> blacklistReasons) {
        super(faction, from, to);
        this.blacklistReasons = blacklistReasons;
        this.newReasonTextFieldWidget = new TextFieldWidget(TEXT_RENDERER, 0, 0, 200, 20, empty());
        this.newPriceTextFieldWidget = new TextFieldWidget(TEXT_RENDERER, 0, 0, 50, 20, empty());
        this.newKillsTextFieldWidget = new TextFieldWidget(TEXT_RENDERER, 0, 0, 50, 20, empty());
    }

    @Override
    public void initBody() {
        DirectionalLayoutWidget directionalLayoutWidget = this.layout.addBody(vertical().spacing(4));

        directionalLayoutWidget.add(getMenuDirectionalLayoutWidget(), Positioner::alignHorizontalCenter);
        directionalLayoutWidget.add(new EmptyWidget(0, 4)); // spacing
        directionalLayoutWidget.add(getTableHeaderDirectionalLayoutWidget(), Positioner::alignHorizontalCenter);

        // create a copy to avoid ConcurrentModificationException
        new ArrayList<>(this.blacklistReasons).forEach(blacklistReason -> {
            Widget blacklistReasonEntryDirectionalLayoutWidget = getBlacklistReasonEntryDirectionalLayoutWidget(blacklistReason);
            directionalLayoutWidget.add(blacklistReasonEntryDirectionalLayoutWidget, Positioner::alignHorizontalCenter);
        });

        directionalLayoutWidget.add(getNewEntryDirectionalLayoutWidget(), Positioner::alignHorizontalCenter);

        directionalLayoutWidget.forEachChild(this::addDrawableChild);
    }

    @Override
    public void onOffsetChange(int newOffset) {

    }

    @Override
    public int entryCount() {
        return this.blacklistReasons.size();
    }

    @Override
    public int pageLimit() {
        int contentHeight = this.layout.getContentHeight();
        return contentHeight / 16;
    }

    @Override
    public void doOnClose() {
        api.postBlacklistReasons(this.faction, this.blacklistReasons);
        super.doOnClose();
    }

    private void reopen(boolean withApiCall) {
        if (withApiCall) {
            api.getBlacklistReasons(this.faction, blacklistReasons -> {
                FactionBlacklistReasonScreen factionBlacklistReasonScreen = new FactionBlacklistReasonScreen(this.faction, this.from, this.to, blacklistReasons);
                this.client.execute(() -> this.client.setScreen(factionBlacklistReasonScreen));
            });

            return;
        }

        FactionBlacklistReasonScreen factionBlacklistReasonScreen = new FactionBlacklistReasonScreen(this.faction, this.from, this.to, this.blacklistReasons);
        this.client.execute(() -> this.client.setScreen(factionBlacklistReasonScreen));
    }

    private @NotNull Widget getTableHeaderDirectionalLayoutWidget() {
        DirectionalLayoutWidget directionalLayoutWidget = horizontal().spacing(8);

        TextWidget reasonTextWidget = directionalLayoutWidget.add(new TextWidget(translatable("ucutils.screen.faction.blacklist.header.reason"), TEXT_RENDERER));
        reasonTextWidget.setWidth(200);

        TextWidget priceTextWidget = directionalLayoutWidget.add(new TextWidget(translatable("ucutils.screen.faction.blacklist.header.price"), TEXT_RENDERER));
        priceTextWidget.setWidth(50);

        TextWidget killsTextWidget = directionalLayoutWidget.add(new TextWidget(translatable("ucutils.screen.faction.blacklist.header.kills"), TEXT_RENDERER));
        killsTextWidget.setWidth(50);

        directionalLayoutWidget.add(new EmptyWidget(20, 0));

        return directionalLayoutWidget;
    }

    private @NotNull Widget getBlacklistReasonEntryDirectionalLayoutWidget(@NotNull BlacklistReason blacklistReason) {
        DirectionalLayoutWidget directionalLayoutWidget = horizontal().spacing(8);

        TextWidget reasonTextWidget = directionalLayoutWidget.add(new TextWidget(of(blacklistReason.getReason()), TEXT_RENDERER), Positioner::alignVerticalCenter);
        reasonTextWidget.alignLeft();
        reasonTextWidget.setWidth(200);

        TextFieldWidget priceTextFieldWidget = directionalLayoutWidget.add(new TextFieldWidget(TEXT_RENDERER, 0, 0, 50, 20, empty()));
        priceTextFieldWidget.setEditable(true);
        priceTextFieldWidget.setText(valueOf(blacklistReason.getPrice()));
        priceTextFieldWidget.setChangedListener(newPriceString -> {
            int newPrice = parseIntegerOrDefault(newPriceString, blacklistReason.getPrice());
            this.blacklistReasons.stream()
                    .filter(br -> br.equals(blacklistReason))
                    .findFirst()
                    .ifPresent(br -> br.setPrice(newPrice));
        });

        TextFieldWidget killsTextFieldWidget = directionalLayoutWidget.add(new TextFieldWidget(TEXT_RENDERER, 0, 0, 50, 20, empty()));
        killsTextFieldWidget.setEditable(true);
        killsTextFieldWidget.setText(valueOf(blacklistReason.getKills()));
        killsTextFieldWidget.setChangedListener(newKillsString -> {
            int newKills = parseIntegerOrDefault(newKillsString, blacklistReason.getKills());
            this.blacklistReasons.stream()
                    .filter(br -> br.equals(blacklistReason))
                    .findFirst()
                    .ifPresent(br -> br.setKills(newKills));
        });

        renderService.addButton(directionalLayoutWidget, of("X"), button -> {
            this.blacklistReasons.remove(blacklistReason);
            reopen(false);
        }, 20);

        return directionalLayoutWidget;
    }

    private @NotNull Widget getNewEntryDirectionalLayoutWidget() {
        DirectionalLayoutWidget directionalLayoutWidget = horizontal().spacing(8);

        TextFieldWidget reasonTextFieldWidget = directionalLayoutWidget.add(this.newReasonTextFieldWidget);
        reasonTextFieldWidget.setEditable(true);

        TextFieldWidget priceTextFieldWidget = directionalLayoutWidget.add(this.newPriceTextFieldWidget);
        priceTextFieldWidget.setEditable(true);

        TextFieldWidget killsTextFieldWidget = directionalLayoutWidget.add(this.newKillsTextFieldWidget);
        killsTextFieldWidget.setEditable(true);

        renderService.addButton(directionalLayoutWidget, of("+"), button -> {
            String reason = this.newReasonTextFieldWidget.getText();
            int price = parseIntegerOrDefault(this.newPriceTextFieldWidget.getText(), 0);
            int kills = parseIntegerOrDefault(this.newKillsTextFieldWidget.getText(), 0);

            BlacklistReason blacklistReason = new BlacklistReason(reason, false, kills, price);
            this.blacklistReasons.add(blacklistReason);

            reopen(false);
        }, 20);

        return directionalLayoutWidget;
    }

    private int parseIntegerOrDefault(String value, int defaultValue) {
        try {
            return parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
