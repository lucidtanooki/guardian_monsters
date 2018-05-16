package de.limbusdev.guardianmonsters.battle.ui.widgets;

/**
 * When implementing ObservableWidget, prevent observers registering multiple times
 * Created by georg on 17.08.16.
 */
public interface ObservableWidget {
    void addWidgetObserver(WidgetObserver wo);
    void notifyWidgetObservers();
}
