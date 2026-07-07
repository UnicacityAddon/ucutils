package de.rettichlp.ucutils.common.integrations;

import de.rettichlp.therettingtoncompanion.TheRettingtonCompanionApi;
import de.rettichlp.therettingtoncompanion.gui.widgets.base.AbstractTRCWidget;
import de.rettichlp.therettingtoncompanion.models.Notification;

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
        return List.of();
    }
}
