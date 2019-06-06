package de.limbusdev.guardianmonsters.battle.model

import com.badlogic.gdx.utils.Array

import java.util.Comparator

/**
 * @author Georg Eckert 2019
 */

class ObservableQueue<T> : Array<T>(), ObservableList<T>
{
    private var observers: Array<ListObserver<T>> = Array()

    override fun add(value: T)
    {
        super.add(value)
    }

    override fun insert(index: Int, value: T)
    {
        super.insert(index, value)
    }

    override fun removeValue(value: T?, identity: Boolean): Boolean
    {
        return super.removeValue(value, identity)
    }

    override fun removeIndex(index: Int): T
    {
        return super.removeIndex(index)
    }

    override fun removeRange(start: Int, end: Int)
    {
        super.removeRange(start, end)
    }

    override fun pop(): T
    {
        return super.pop()
    }

    override fun clear()
    {
        super.clear()
    }

    override fun sort()
    {
        super.sort()
    }

    override fun sort(comparator: Comparator<in T>)
    {
        super.sort(comparator)
    }

    override fun reverse()
    {
        super.reverse()
    }

    override fun shuffle() {
        super.shuffle()
    }

    override fun addObserver(o: ListObserver<T>)
    {
        observers.add(o)
    }

    override fun clearObservers()
    {
        observers.clear()
    }

    override fun notifyObserversRemove(item: T)
    {
        for (lo in observers)
        {
            lo.getInformedAboutRemoval(item)
        }
    }

    override fun notifyObserversAdd(item: T)
    {

    }

    override fun notifyObserversSort()
    {

    }

}
