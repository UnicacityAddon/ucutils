package de.rettichlp.ucutils.common.gui.widgets.alignment;

import de.rettichlp.ucutils.common.gui.widgets.base.AbstractUCUtilsWidget;

import java.util.ArrayList;
import java.util.List;

public abstract class AlignWidget<T> extends AbstractUCUtilsWidget {

    protected final List<AbstractUCUtilsWidget> ucUtilsWidgets = new ArrayList<>();
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
