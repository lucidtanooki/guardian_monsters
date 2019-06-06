package de.limbusdev.guardianmonsters.guardians.items

import com.badlogic.ashley.signals.Signal

/**
 * ItemSignal
 *
 * @author Georg Eckert 2019
 */

class ItemSignal
(
        val item: Item,
        val message: Message
)
    : Signal<Item>()
{
    enum class Message { DELETED, ADDED }
}
