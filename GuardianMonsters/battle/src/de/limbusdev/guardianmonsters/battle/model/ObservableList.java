package de.limbusdev.guardianmonsters.battle.model;

/**
 * Created by georg on 04.12.16.
 */

public interface ObservableList<T> {
    void addObserver(ListObserver o);
    void clearObservers();
    void notifyObserversRemove(T item);
    void notifyObserversAdd(T item);
    void notifyObserversSort();
}
