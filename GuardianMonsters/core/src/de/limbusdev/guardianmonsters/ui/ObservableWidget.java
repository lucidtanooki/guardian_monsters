package de.limbusdev.guardianmonsters.ui;

/**
 * Created by georg on 17.08.16.
 */
public interface ObservableWidget {
    public void addWidgetObserver(WidgetObserver wo);
    public void notifyWidgetObservers();
}
