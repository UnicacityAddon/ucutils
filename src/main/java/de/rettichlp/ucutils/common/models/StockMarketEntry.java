package de.rettichlp.ucutils.common.models;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.common.models.Company.fromDisplayName;
import static java.awt.Color.BLUE;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;
import static net.minecraft.ChatFormatting.DARK_GREEN;
import static net.minecraft.ChatFormatting.DARK_RED;
import static net.minecraft.ChatFormatting.GOLD;
import static net.minecraft.ChatFormatting.GREEN;
import static net.minecraft.ChatFormatting.RED;
import static net.minecraft.ChatFormatting.YELLOW;
import static net.minecraft.ChatFormatting.stripFormatting;
import static net.minecraft.core.component.DataComponents.LORE;

public record StockMarketEntry(Company company, double price, double changeMoney, double changePercentage, int ownership, double buyPrice) {

    private static final Pattern PRICE_PATTERN = compile("Kurs: (?<price>\\d+\\.\\d+)\\$");
    private static final Pattern CHANGE_PATTERN = compile("Änderung: (?<money>-?\\d+\\.\\d+)\\$ \\((?<percentage>-?\\d+\\.\\d+)%\\) [▼▲]");
    private static final Pattern OWNERSHIP_PATTERN = compile("Besitz: (?<ownership>\\d+)/\\d+");
    private static final Pattern BUY_PRICE_PATTERN = compile("EK-Preis: (?<price>-?\\d+\\.\\d+)\\$");

    public @Nullable Color getColor() {
        // check if price is below or equal to min value
        if (this.price <= this.company.getMinValue()) {
            return BLUE;
        }

        // check if user has bought any stock
        if (this.ownership == 0) {
            return null;
        }

        // get color value
        Integer colorValue = getColorValue();
        if (colorValue == null) {
            return null;
        }

        return new Color(colorValue);
    }

    private @Nullable Integer getColorValue() {
        Integer colorValue;
        double diff = this.price - this.buyPrice;

        if (diff >= 75) {
            colorValue = DARK_GREEN.getColor();
        } else if (diff >= 60) {
            colorValue = GREEN.getColor();
        } else if (diff >= 45) {
            colorValue = YELLOW.getColor();
        } else if (diff >= 30) {
            colorValue = GOLD.getColor();
        } else if (diff >= 15) {
            colorValue = RED.getColor();
        } else {
            colorValue = DARK_RED.getColor();
        }

        return colorValue;
    }

    public static @Nullable StockMarketEntry fromItemStack(@NotNull ItemStack itemStack) {
        if (itemStack.getCustomName() == null) {
            return null;
        }

        String companyName = stripFormatting(itemStack.getCustomName().getString());

        Company company = fromDisplayName(companyName);
        if (company == null) {
            return null;
        }

        ItemLore loreComponent = itemStack.get(LORE);
        if (loreComponent == null) {
            return null;
        }

        double price = 0;
        double changeMoney = 0;
        double changePercentage = 0;
        int ownership = 0;
        double buyPrice = 0;

        for (Component line : loreComponent.lines()) {
            String textString = stripFormatting(line.getString());

            Matcher priceMatcher = PRICE_PATTERN.matcher(textString);
            if (priceMatcher.matches()) {
                String priceMatcherPriceString = priceMatcher.group("price");
                price = parseDouble(priceMatcherPriceString);
                continue;
            }

            Matcher changeMatcher = CHANGE_PATTERN.matcher(textString);
            if (changeMatcher.matches()) {
                String changeMatcherMoneyString = changeMatcher.group("money");
                String changeMatcherPercentageString = changeMatcher.group("percentage");
                changeMoney = parseDouble(changeMatcherMoneyString);
                changePercentage = parseDouble(changeMatcherPercentageString);
                continue;
            }

            Matcher ownershipMatcher = OWNERSHIP_PATTERN.matcher(textString);
            if (ownershipMatcher.matches()) {
                String ownershipMatcherOwnershipString = ownershipMatcher.group("ownership");
                ownership = parseInt(ownershipMatcherOwnershipString);
                continue;
            }

            Matcher buyPriceMatcher = BUY_PRICE_PATTERN.matcher(textString);
            if (buyPriceMatcher.matches()) {
                String buyPriceMatcherPriceString = buyPriceMatcher.group("price");
                buyPrice = parseDouble(buyPriceMatcherPriceString);
            }
        }

        return new StockMarketEntry(company, price, changeMoney, changePercentage, ownership, buyPrice);
    }
}
