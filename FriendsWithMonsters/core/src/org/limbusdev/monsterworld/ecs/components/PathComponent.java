package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.enums.SkyDirection;

/**
 * Defines a path for AI to walk on
 * Created by georg on 30.11.15.
 */
public class PathComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Array<SkyDirection> path;
    public boolean startMoving = true;
    public boolean moving = false;
    public int currentDir = 0;
    /* ........................................................................... CONSTRUCTOR .. */
    public PathComponent(Array<SkyDirection> path) {
        this.path = path;
    }
    /* ............................................................................... METHODS .. */
    public void next() {
        currentDir++;
        if(currentDir == path.size) currentDir = 0;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
