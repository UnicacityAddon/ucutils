package de.rettichlp.ucutils.common.integrations;

import de.rettichlp.therettingtoncompanion.TheRettingtonCompanionApi;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidget;
import de.rettichlp.therettingtoncompanion.models.Notification;
import de.rettichlp.ucutils.common.gui.widgets.CarLockedWidget;
import de.rettichlp.ucutils.common.gui.widgets.MoneyWidget;
import de.rettichlp.ucutils.common.gui.widgets.PayDayWidget;
import de.rettichlp.ucutils.common.gui.widgets.ServiceCountWidget;

import java.util.List;
import java.util.Set;

import static de.rettichlp.ucutils.UCUtils.notificationService;

public class TheRettingtonCompanionIntegration implements TheRettingtonCompanionApi {

    @Override
    public Set<Notification> getNotifications() {
        return notificationService.getNotifications();
    }

    @Override
    public List<AbstractTRCWidget<?>> getWidgets() {
        return List.of(
                new CarLockedWidget(),
                new MoneyWidget(),
                new PayDayWidget(),
                new ServiceCountWidget()
        );
    }
}
