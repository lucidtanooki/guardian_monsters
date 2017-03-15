package de.limbusdev.guardianmonsters.model.items;

import de.limbusdev.guardianmonsters.model.monsters.Monster;

/**
 * KeyItem
 *
 * @author Georg Eckert 2017
 */

public class KeyItem extends Item {

    public KeyItem(String name) {
        super(name, Category.KEY);
    }

    @Override
    public void apply(Monster m) {
        // Do nothing
    }

    @Override
    public boolean applicable(Monster m) {
        return false;
    }
}
