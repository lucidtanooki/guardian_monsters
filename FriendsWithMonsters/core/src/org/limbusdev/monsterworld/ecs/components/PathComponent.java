package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.enums.SkyDirection;

/**
 * Defines a path for AI to walk on. It also contains to boolean attributes to indicate whether an
 * {@link com.badlogic.ashley.core.Entity} is about to move or already moving and the current
 * direction.
 * Created by georg on 30.11.15.
 */
public class PathComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public Array<SkyDirection> path;
    public boolean startMoving = true;
    public boolean moving = false;
    public int currentDir = 0;
    public int stopCounter=0;
    public boolean staticEntity;
    /* ........................................................................... CONSTRUCTOR .. */

    /**
     * Creates a component holding a path consisting of {@link SkyDirection}s for an
     * {@link com.badlogic.ashley.core.Entity} to follow.
     * Try to close the path into a circle so it can be followed again and again. There is no
     * collision detection for AI with level colliders.
     *
     * USAGE:
     *  SkyDirection[] dirs = {
     *      SkyDirection.N,
     *      SkyDirection.N,
     *      SkyDirection.ESTOP,
     *      SkyDirection.WSTOP,
     *      SkyDirection.S,
     *      SkyDirection.S}
     *  PathComponent path = new PathComponent((new Array<SkyDirection>()).addAll(dirs));
     *
     * @param path
     */
    public PathComponent(Array<SkyDirection> path, boolean staticEntity) {
        this.path = path;
        this.staticEntity = staticEntity;
    }
    /* ............................................................................... METHODS .. */

    /**
     * Moves on to the next direction in the contained path. If it reaches the end of the path it
     * will start from the beginning.
     */
    public void next() {
        currentDir++;
        if(currentDir >= path.size) currentDir = 0;
    }

    /**
     * Counts the duration of being in the stop status. When it reaches 32 the counter gets reset
     * @return  whether the path is still in stop mode
     */
    public boolean stop() {
        stopCounter++;
        if(stopCounter == 32) {
            stopCounter = 0;
            return false;
        }else {
            return true;
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
