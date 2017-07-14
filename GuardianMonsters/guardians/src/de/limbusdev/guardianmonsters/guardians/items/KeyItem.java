package de.limbusdev.guardianmonsters.guardians.items;


import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;

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
    public void apply(Guardian m) {
        // Do nothing
    }

    @Override
    public boolean applicable(Guardian m) {
        return false;
    }
}
