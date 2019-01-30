package de.limbusdev.guardianmonsters.battle.model

/**
 * Created by georg on 04.12.16.
 */

interface ObservableList<T>
{
    fun addObserver(o: ListObserver<*>)
    fun clearObservers()
    fun notifyObserversRemove(item: T)
    fun notifyObserversAdd(item: T)
    fun notifyObserversSort()
}
