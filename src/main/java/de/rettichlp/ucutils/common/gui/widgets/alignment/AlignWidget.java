package de.rettichlp.ucutils.common.gui.widgets.alignment;

import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsWidget;
import de.rettichlp.ucutils.common.gui.widgets.base.UCUtilsWidgetConfiguration;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AlignWidget<T> extends AbstractUCUtilsWidget<UCUtilsWidgetConfiguration> {

    protected final Collection<AbstractUCUtilsWidget<UCUtilsWidgetConfiguration>> ucUtilsWidgets = new ArrayList<>();
    protected boolean disableMargin = false;

    public abstract void add(T entry);

    public void clear() {
        this.ucUtilsWidgets.clear();
    }

    public AlignWidget<T> disableMargin() {
        this.disableMargin = true;
        return this;
    }
}
