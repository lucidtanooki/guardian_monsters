package de.limbusdev.guardianmonsters.battle.model;

/**
 * Created by georg on 04.12.16.
 */

public interface ObservableList<T> {
    public void addObserver(ListObserver o);
    public void clearObservers();
    public void notifyObserversRemove(T item);
    public void notifyObserversAdd(T item);
    public void notifyObserversSort();
}
