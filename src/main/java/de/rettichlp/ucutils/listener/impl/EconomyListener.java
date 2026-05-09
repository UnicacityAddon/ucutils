package de.rettichlp.ucutils.listener.impl;

import de.rettichlp.ucutils.common.registry.UCUtilsListener;
import de.rettichlp.ucutils.listener.IMessageReceiveListener;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.rettichlp.ucutils.UCUtils.commandService;
import static de.rettichlp.ucutils.UCUtils.configuration;
import static de.rettichlp.ucutils.UCUtils.messageService;
import static de.rettichlp.ucutils.UCUtils.notificationService;
import static de.rettichlp.ucutils.UCUtils.player;
import static de.rettichlp.ucutils.UCUtils.storage;
import static java.lang.Integer.parseInt;
import static java.lang.Math.max;
import static java.lang.System.currentTimeMillis;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;
import static net.minecraft.text.Text.of;
import static net.minecraft.util.Formatting.GRAY;
import static net.minecraft.util.Formatting.UNDERLINE;

@UCUtilsListener
public class EconomyListener implements IMessageReceiveListener {

    // bank
    private static final Pattern BANK_STATEMENT_PATTERN = compile("^Ihr Bankguthaben beträgt: \\+(?<amount>\\d+)\\$$");
    private static final Pattern BANK_NEW_BALANCE_PAYDAY_PATTERN = compile("^Neuer Betrag: (?<amount>\\d+)\\$ \\([+-]\\d+\\$\\)$");
    private static final Pattern BANK_TRANSFER_TO_PATTERN = compile("^Du hast (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) (?<amount>\\d+)\\$ überwiesen!$");
    private static final Pattern BANK_TRANSFER_GET_PATTERN = compile("^(?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat dir (?<amount>\\d+)\\$ überwiesen!$");
    private static final Pattern BANK_TRANSFER_FBANK_PATTERN = compile("^Du hast der Fraktion (?<faction>.+) (?<amount>\\d+)\\$ überwiesen!$");
    private static final Pattern BANK_NEW_BALANCE_BANK_PATTERN = compile("^Neuer Bankkontostand: (?<amount>\\d+)\\$$");
    private static final Pattern BANK_NEW_BALANCE_CASH_PATTERN = compile("^Neuer Bargeldbestand: (?<amount>\\d+)\\$$");

    // cash
    private static final Pattern CASH_GIVE_PATTERN = compile("^Du hast (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) (?<amount>\\d+)\\$ gegeben!$");
    private static final Pattern CASH_TAKE_PATTERN = compile("^(?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat dir (?<amount>\\d+)\\$ gegeben!$");
    private static final Pattern CASH_TO_FBANK_PATTERN = compile("^\\[F-Bank] (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat (?<amount>\\d+)\\$ in die Fraktionsbank eingezahlt\\. Grund: (?<reason>.+)$");
    private static final Pattern CASH_FROM_FBANK_PATTERN = compile("^\\[F-Bank] (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) hat (?<amount>\\d+)\\$ aus der Fraktionsbank ausgezahlt\\.$");
    private static final Pattern CASH_TO_BANK_PATTERN = compile("^Eingezahlt: \\+(?<amount>\\d+)\\$$");
    private static final Pattern CASH_FROM_BANK_PATTERN = compile("^Auszahlung: -(?<amount>\\d+)\\$$");
    private static final Pattern CASH_GET_PATTERN = compile("^\\+(?<amount>\\d+)\\$$");
    private static final Pattern CASH_GET_COMBO_PATTERN = compile("^\\[Combo] x\\d+ Fang-Combo! \\+(?<amount>\\d+)\\$$");
    private static final Pattern CASH_REMOVE_PATTERN = compile("^-(?<amount>\\d+)\\$$");
    private static final Pattern CASH_STATS_PATTERN = compile("^- Geld: (?<amount>\\d+)\\$$");

    // payday
    private static final Pattern PAYDAY_TIME_PATTERN = compile("^- Zeit seit PayDay: (?<minutes>\\d+)/60 Minuten$");
    private static final Pattern PAYDAY_SALARY_PATTERN = compile("^\\[PayDay] Du bekommst dein Gehalt von (?<money>\\d+)\\$ am PayDay ausgezahlt\\.$");
    private static final Pattern PAYDAY_MINE_SALARY_PATTERN = compile("^\\[PayDay] Du bekommst deine Mine Einnahmen von (?<money>\\d+)\\$ am PayDay ausgezahlt\\.$");
    private static final Pattern PAYDAY_COUNTDOWN_PATTERN = compile("^Info: Du hast in 3 Minuten deinen PayDay$");

    // other
    private static final Pattern ATM_MONEY_AMOUNT_PATTERN = compile("ATM \\d+: (?<moneyAtmAmount>\\d+)\\$/100000\\$");
    private static final Pattern BUSINESS_CASH_PATTERN = compile("^Kasse: (\\d+)\\$$");
    private static final Pattern EXP_PATTERN = compile("(?<amount>[+-]\\d+) Exp!( \\(x(?<multiplier>\\d)\\))?");
    private static final Pattern LOTTO_WIN_PATTERN = compile("^\\[Lotto] Du hast im Lotto gewonnen! \\((?<amount>\\d+)\\$\\)$");
    private static final Pattern MEDIC_DESPAWNED_PATTERN = compile("^Verdammt\\.\\.\\. mein Kopf dröhnt so\\.\\.\\.$");
    private static final Pattern MEDIC_REVIVE_PATTERN = compile("^Du wirst von (?:\\[UC])?(?<playerName>[a-zA-Z0-9_]+) wiederbelebt\\.$");
    private static final Pattern REVIVE_ADMIN_PATTERN = compile("^Du wurdest von \\[UC](?<playerName>[a-zA-Z0-9_]+) wiederbelebt\\.$");
    private static final Pattern BACK_IN_LIFE_PATTERN = compile("^\\[Friedhof] Du lebst nun wieder\\.$");

    private long lastMedicReviveAction = 0;

    @Override
    public boolean onMessageReceive(Text text, String message) {
        Matcher bankStatementMatcher = BANK_STATEMENT_PATTERN.matcher(message);
        if (bankStatementMatcher.find()) {
            int amount = parseInt(bankStatementMatcher.group("amount"));
            configuration.setMoneyBankAmount(amount);

            List<String> commands = switch (configuration.getOptions().atmInformationType()) {
                case NONE -> emptyList();
                case F_BANK -> List.of("fbank info");
                case G_BANK -> List.of("gruppierungkasse");
                case BOTH -> List.of("fbank info", "gruppierungkasse");
            };

            commandService.sendCommands(commands);

            return true;
        }

        Matcher bankNewBalancePaydayMatcher = BANK_NEW_BALANCE_PAYDAY_PATTERN.matcher(message);
        if (bankNewBalancePaydayMatcher.find()) {
            int amount = parseInt(bankNewBalancePaydayMatcher.group("amount"));
            configuration.setMoneyBankAmount(amount);
            configuration.setMinutesSinceLastPayDay(0);
            configuration.setPredictedPayDaySalary(0);
            configuration.setPredictedPayDayExp(0);
            return true;
        }

        Matcher bankTransferToMatcher = BANK_TRANSFER_TO_PATTERN.matcher(message);
        if (bankTransferToMatcher.find()) {
            int amount = parseInt(bankTransferToMatcher.group("amount"));
            configuration.setMoneyBankAmount(configuration.getMoneyBankAmount() - amount);
            return true;
        }

        Matcher bankTransferGetMatcher = BANK_TRANSFER_GET_PATTERN.matcher(message);
        if (bankTransferGetMatcher.find()) {
            int amount = parseInt(bankTransferGetMatcher.group("amount"));
            configuration.setMoneyBankAmount(configuration.getMoneyBankAmount() + amount);
            return true;
        }

        Matcher bankTransferFbankMatcher = BANK_TRANSFER_FBANK_PATTERN.matcher(message);
        if (bankTransferFbankMatcher.find()) {
            int amount = parseInt(bankTransferFbankMatcher.group("amount"));
            configuration.setMoneyBankAmount(configuration.getMoneyBankAmount() - amount);
            return true;
        }

        Matcher bankNewBalanceBankMatcher = BANK_NEW_BALANCE_BANK_PATTERN.matcher(message);
        if (bankNewBalanceBankMatcher.find()) {
            int amount = parseInt(bankNewBalanceBankMatcher.group("amount"));
            configuration.setMoneyBankAmount(amount);
            return true;
        }

        Matcher bankNewBalanceCashMatcher = BANK_NEW_BALANCE_CASH_PATTERN.matcher(message);
        if (bankNewBalanceCashMatcher.find()) {
            int amount = parseInt(bankNewBalanceCashMatcher.group("amount"));
            configuration.setMoneyCashAmount(amount);
            return true;
        }

        Matcher cashGiveMatcher = CASH_GIVE_PATTERN.matcher(message);
        if (cashGiveMatcher.find()) {
            int amount = parseInt(cashGiveMatcher.group("amount"));
            configuration.setMoneyCashAmount(configuration.getMoneyCashAmount() - amount);
            return true;
        }

        Matcher cashTakeMatcher = CASH_TAKE_PATTERN.matcher(message);
        if (cashTakeMatcher.find()) {
            int amount = parseInt(cashTakeMatcher.group("amount"));
            configuration.setMoneyCashAmount(configuration.getMoneyCashAmount() + amount);
            return true;
        }

        Matcher cashToFbankMatcher = CASH_TO_FBANK_PATTERN.matcher(message);
        if (cashToFbankMatcher.find()) {
            String playerName = cashToFbankMatcher.group("playerName");

            if (playerName.equals(player.getGameProfile().name())) {
                int amount = parseInt(cashToFbankMatcher.group("amount"));
                configuration.setMoneyCashAmount(configuration.getMoneyCashAmount() - amount);
            }

            return true;
        }

        Matcher cashFromFbankMatcher = CASH_FROM_FBANK_PATTERN.matcher(message);
        if (cashFromFbankMatcher.find()) {
            String playerName = cashFromFbankMatcher.group("playerName");

            if (playerName.equals(player.getGameProfile().name())) {
                int amount = parseInt(cashFromFbankMatcher.group("amount"));
                configuration.setMoneyCashAmount(configuration.getMoneyCashAmount() + amount);
            }

            return true;
        }

        Matcher cashToBankMatcher = CASH_TO_BANK_PATTERN.matcher(message);
        if (cashToBankMatcher.find()) {
            int amount = parseInt(cashToBankMatcher.group("amount"));
            configuration.setMoneyCashAmount(configuration.getMoneyCashAmount() - amount);
            configuration.setMoneyBankAmount(configuration.getMoneyBankAmount() + amount);
            return true;
        }

        Matcher cashFromBankMatcher = CASH_FROM_BANK_PATTERN.matcher(message);
        if (cashFromBankMatcher.find()) {
            int amount = parseInt(cashFromBankMatcher.group("amount"));
            configuration.setMoneyCashAmount(configuration.getMoneyCashAmount() + amount);
            configuration.setMoneyBankAmount(configuration.getMoneyBankAmount() - amount);
            return true;
        }

        Matcher cashGetMatcher = CASH_GET_PATTERN.matcher(message);
        if (cashGetMatcher.find()) {
            int amount = parseInt(cashGetMatcher.group("amount"));
            configuration.setMoneyCashAmount(configuration.getMoneyCashAmount() + amount);
            return true;
        }

        Matcher cashGetComboMatcher = CASH_GET_COMBO_PATTERN.matcher(message);
        if (cashGetComboMatcher.find()) {
            int amount = parseInt(cashGetComboMatcher.group("amount"));
            configuration.setMoneyCashAmount(configuration.getMoneyCashAmount() + amount);
            return true;
        }

        Matcher cashRemoveMatcher = CASH_REMOVE_PATTERN.matcher(message);
        if (cashRemoveMatcher.find()) {
            int amount = parseInt(cashRemoveMatcher.group("amount"));
            configuration.setMoneyCashAmount(configuration.getMoneyCashAmount() - amount);
            return true;
        }

        Matcher cashStatsMatcher = CASH_STATS_PATTERN.matcher(message);
        if (cashStatsMatcher.find()) {
            int amount = parseInt(cashStatsMatcher.group("amount"));
            configuration.setMoneyCashAmount(amount);
            return true;
        }

        Matcher paydayTimeMatcher = PAYDAY_TIME_PATTERN.matcher(message);
        if (paydayTimeMatcher.find()) {
            int minutesSinceLastPayDay = parseInt(paydayTimeMatcher.group("minutes"));
            configuration.setMinutesSinceLastPayDay(minutesSinceLastPayDay);
            return true;
        }

        Matcher paydaySalaryMatcher = PAYDAY_SALARY_PATTERN.matcher(message);
        if (paydaySalaryMatcher.find()) {
            int money = parseInt(paydaySalaryMatcher.group("money"));
            configuration.addPredictedPayDaySalary(money);
            storage.setCurrentJob(null);
            return true;
        }

        Matcher paydayMineSalaryMatcher = PAYDAY_MINE_SALARY_PATTERN.matcher(message);
        if (paydayMineSalaryMatcher.find()) {
            int money = parseInt(paydayMineSalaryMatcher.group("money"));
            configuration.addPredictedPayDaySalary(money);
            return true;
        }

        Matcher paydayCountdownMatcher = PAYDAY_COUNTDOWN_PATTERN.matcher(message);
        if (paydayCountdownMatcher.find()) {
            configuration.setMinutesSinceLastPayDay(57);

            if (configuration.getMoneyBankAmount() > 100000) {
                messageService.sendModMessage("Du hast mehr als 100000$ auf der Bank!", false);
                notificationService.notificationSound(3);
            }

            return true;
        }

        Matcher moneyAtmAmountMatcher = ATM_MONEY_AMOUNT_PATTERN.matcher(message);
        if (moneyAtmAmountMatcher.find()) {
            int moneyAtmAmount = parseInt(moneyAtmAmountMatcher.group("moneyAtmAmount"));
            storage.setMoneyAtmAmount(moneyAtmAmount);
            return true;
        }

        Matcher businessCashMatcher = BUSINESS_CASH_PATTERN.matcher(message);
        if (businessCashMatcher.find()) {
            String amountString = businessCashMatcher.group(1);

            MutableText appendedText = text.copy().append(" ")
                    .append(of("Geld entnehmen").copy().formatted(GRAY, UNDERLINE))
                    .styled(style -> style
                            .withClickEvent(new ClickEvent.RunCommand("/biz kasse get " + amountString))
                            .withHoverEvent(new HoverEvent.ShowText(of("Klicke, um " + amountString + "$ aus der Kasse zu nehmen.")))
                    );

            player.sendMessage(appendedText, false);
            return false;
        }

        Matcher expMatcher = EXP_PATTERN.matcher(message);
        if (expMatcher.find()) {
            int amount = parseInt(expMatcher.group("amount"));
            String multiplierString = expMatcher.group("multiplier");
            int multiplier = ofNullable(multiplierString).map(Integer::parseInt).orElse(1);

            configuration.addPredictedPayDayExp(amount * multiplier);
            return true;
        }

        Matcher lottoWinMatcher = LOTTO_WIN_PATTERN.matcher(message);
        if (lottoWinMatcher.find()) {
            int amount = parseInt(lottoWinMatcher.group("amount"));
            configuration.setMoneyBankAmount(configuration.getMoneyBankAmount() + amount);
            return true;
        }

        Matcher medicDespawnedMatcher = MEDIC_DESPAWNED_PATTERN.matcher(message);
        if (medicDespawnedMatcher.find()) {
            configuration.setMoneyCashAmount(0);
            return true;
        }

        Matcher medicReviveMatcher = MEDIC_REVIVE_PATTERN.matcher(message);
        if (medicReviveMatcher.find()) {
            this.lastMedicReviveAction = currentTimeMillis();
            return true;
        }

        Matcher backInLifeMatcher = BACK_IN_LIFE_PATTERN.matcher(message);
        if (backInLifeMatcher.find()) {
            long timeSingeLastMedicReviveAction = currentTimeMillis() - this.lastMedicReviveAction;
            if (timeSingeLastMedicReviveAction > 6000 && timeSingeLastMedicReviveAction < 10000) {
                configuration.setMoneyBankAmount(max(0, configuration.getMoneyBankAmount() - 50));
            }
        }

        return true;
    }
}
