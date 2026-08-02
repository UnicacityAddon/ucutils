package de.rettichlp.ucutils.common.gui.widgets;

import de.rettichlp.therettingtoncompanion.gui.options.list.TRCOptionsList;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCTextWidget;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.WidgetConfiguration;
import de.rettichlp.ucutils.common.models.Faction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Unmodifiable;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;

import static de.rettichlp.therettingtoncompanion.gui.OnOffCycleButtonEntry.ON;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.common.models.Faction.NULL;
import static de.rettichlp.ucutils.common.services.RenderService.keyValue;
import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static net.minecraft.client.gui.components.Tooltip.create;
import static net.minecraft.network.chat.CommonComponents.SPACE;
import static net.minecraft.network.chat.Component.empty;
import static net.minecraft.network.chat.Component.literal;
import static net.minecraft.network.chat.Component.translatable;
import static net.minecraft.network.chat.TextColor.DARK_GRAY;
import static net.minecraft.network.chat.TextColor.GRAY;
import static net.minecraft.world.item.Items.SKELETON_SKULL;
import static net.minecraft.world.item.Items.WITHER_SKELETON_SKULL;

public class CorpseCountWidget extends AbstractTRCTextWidget<CorpseCountWidget.Configuration> {

    private LinkedHashMap<Faction, Long> factionCorpseCountMap = new LinkedHashMap<>();

    @Override
    public Component text() {
        long totalCount = this.factionCorpseCountMap.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        MutableComponent component = empty()
                .append(literal(valueOf(totalCount)))
                .append(literal("x").withColor(GRAY));

        if (getWidgetConfiguration().isShowFactions()) {
            MutableComponent factionComponent = empty();
            this.factionCorpseCountMap.forEach((faction, amount) -> factionComponent
                    .append(literal(amount.toString()).withColor(faction.getColor()))
                    .append(literal("x").withColor(GRAY))
                    .append(SPACE));

            component
                    .append(literal(" (").withColor(DARK_GRAY))
                    .append(factionComponent)
                    .append(literal(")").withColor(DARK_GRAY));
        }

        return keyValue(translatable("ucutils.options.widgets.corpse_count.label"), component);
    }

    @Override
    public @Nullable String getRegistryName() {
        return "corpse_count";
    }

    @Override
    public Component getLabel() {
        return translatable("ucutils.options.widgets.corpse_count.options.name");
    }

    @Override
    public Component getTooltip() {
        return translatable("ucutils.options.widgets.corpse_count.options.tooltip");
    }

    @Override
    public void addOptions(@NonNull TRCOptionsList optionsList) {
        optionsList.addToggleButton(translatable("ucutils.options.widgets.corpse_count.options.show_factions.name"), create(translatable("ucutils.options.widgets.corpse_count.options.show_factions.tooltip")), getWidgetConfiguration().isShowFactions(), (_, value) -> getWidgetConfiguration().setShowFactions(value == ON));
    }

    @Override
    public boolean isVisible() {
        // every second re-scan for nearby corpses
        if (currentTimeMillis() / 1000 % 2 == 0) {
            this.factionCorpseCountMap = getFactionCorpseCountMap();
        }
        // visible if in the position options screen to allow positioning
        return super.isVisible() && (!this.factionCorpseCountMap.isEmpty() || isWidgetPositionScreen());
    }

    private @NonNull LinkedHashMap<Faction, Long> getFactionCorpseCountMap() {
        LinkedHashMap<Faction, Long> factionCorpseCountMap = new LinkedHashMap<>();
        List<Component> corpseNames = getCorpseItemEntities().stream().map(Entity::getCustomName).toList();

        for (Faction faction : Faction.values()) {
            if (faction == NULL) {
                continue;
            }

            long count = corpseNames.stream()
                    .filter(name -> name.getString().contains(faction.getIcon()))
                    .count();

            if (count > 0) {
                factionCorpseCountMap.put(faction, count);
            }
        }

        return factionCorpseCountMap;
    }

    private @NonNull @Unmodifiable List<ItemEntity> getCorpseItemEntities() {
        Level level = player.level();
        Vec3 position = player.position();
        int radius = 30;

        AABB searchBox = new AABB(position.x - radius, position.y - radius, position.z - radius, position.x + radius, position.y + radius, position.z + radius);
        return level.getEntitiesOfClass(ItemEntity.class, searchBox, item -> item.distanceToSqr(position) <= radius * radius).stream()
                .filter(itemEntity -> itemEntity.getItem().is(SKELETON_SKULL) || itemEntity.getItem().is(WITHER_SKELETON_SKULL))
                .filter(itemEntity -> itemEntity.hasCustomName() && itemEntity.getCustomName().getString().contains("✟"))
                .toList();
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Configuration extends WidgetConfiguration {

        private boolean showFactions = true;
    }
}
