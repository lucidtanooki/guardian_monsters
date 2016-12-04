package de.limbusdev.guardianmonsters.fwmengine.battle.model;

/**
 * Created by georg on 04.12.16.
 */

public interface ListObserver<T> {
    public void getInformedAboutRemoval(T item);
}
