package de.limbusdev.guardianmonsters.ui;

/**
 * When implementing ObservableWidget, prevent observers registering multiple times
 * Created by georg on 17.08.16.
 */
public interface ObservableWidget {
    public void addWidgetObserver(WidgetObserver wo);
    public void notifyWidgetObservers();
}
