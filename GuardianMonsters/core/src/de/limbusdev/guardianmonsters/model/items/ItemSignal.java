package de.limbusdev.guardianmonsters.model.items;

import com.badlogic.ashley.signals.Signal;

/**
 * ItemSignal
 *
 * @author Georg Eckert 2017
 */

public class ItemSignal extends Signal<Item> {

    public final Item item;
    public final Message message;

    public enum Message {
        DELETED, ADDED,
    }

    public ItemSignal(Item item, Message message) {
        this.item = item;
        this.message = message;
    }
}
