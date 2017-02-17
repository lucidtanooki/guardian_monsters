package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components;

import com.badlogic.ashley.core.Component;

import de.limbusdev.guardianmonsters.model.Inventory;

/**
 * Created by georg on 17.02.17.
 */

public class InventoryComponent implements Component {
    public Inventory invetory;

    public InventoryComponent(Inventory invetory) {
        this.invetory = invetory;
    }
}
