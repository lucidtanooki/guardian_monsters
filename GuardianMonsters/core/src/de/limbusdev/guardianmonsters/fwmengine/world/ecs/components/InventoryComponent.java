package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components;

import com.badlogic.ashley.core.Component;

import de.limbusdev.guardianmonsters.guardians.items.Inventory;

/**
 * Created by Georg Eckert on 17.02.17.
 */

public class InventoryComponent implements Component {
    public Inventory inventory;

    public InventoryComponent(Inventory invetory) {
        this.inventory = invetory;
    }
}
