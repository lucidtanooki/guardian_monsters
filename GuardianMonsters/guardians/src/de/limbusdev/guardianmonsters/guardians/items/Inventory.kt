package de.limbusdev.guardianmonsters.guardians.items

import com.badlogic.ashley.signals.Signal
import com.badlogic.gdx.utils.OrderedMap

import java.util.Comparator

/**
 * @author Georg Eckert 2019
 */

class Inventory : Signal<ItemSignal>()
{
    // .................................................................................. Properties
    val items: OrderedMap<Item, Int> = OrderedMap()


    // ..................................................................................... Methods

    /** Adds the given item to the inventory. Raises it's amount, if it is already there. */
    fun putIntoInventory(item: Item)
    {
        when
        {
            containsItem(item) -> items.put(item, getAmountOf(item) + 1)
            else               -> items.put(item, 1)
        }

        dispatch(ItemSignal(item, ItemSignal.Message.ADDED))
    }

    /**
     * Takes one entity of the given item from the inventory. Decreases it's amount
     * and returns 0 if there is none in the inventory
     */
    fun takeFromInventory(item: Item): Item?
    {
        return when
        {
            containsItem(item) ->
            {
                items.put(item, getAmountOf(item) - 1)
                if(getAmountOf(item) < 1) { items.remove(item) }
                dispatch(ItemSignal(item, ItemSignal.Message.DELETED))
                item
            }
            else -> null
        }
    }

    fun containsItem(item: Item): Boolean = (getAmountOf(item) != 0)

    fun getAmountOf(item: Item): Int
    {
        return when
        {
            items.containsKey(item) -> items[item]
            else                    -> 0
        }
    }

    fun sortItemsByID()
    {
        items.orderedKeys().sort(IDComparator())

        dispatch(null)
    }


    // ............................................................................... Inner Classes
    private inner class IDComparator : Comparator<Item>
    {
        override fun compare(o1: Item, o2: Item): Int
        {
            return when
            {
                o1.ID < o2.ID -> -1
                o1.ID > o2.ID -> +1
                else          ->  0
            }
        }
    }
}
