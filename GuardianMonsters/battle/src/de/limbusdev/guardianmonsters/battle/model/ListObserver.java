package de.limbusdev.guardianmonsters.battle.model;

/**
 * Created by georg on 04.12.16.
 */

public interface ListObserver<T> {
    void getInformedAboutRemoval(T item);
}
