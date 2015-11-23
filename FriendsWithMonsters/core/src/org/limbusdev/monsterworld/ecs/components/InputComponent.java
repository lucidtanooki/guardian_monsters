package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.enums.SkyDirection;

/**
 * Created by georg on 22.11.15.
 */
public class InputComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public SkyDirection skyDir;
    public boolean moving;
    public Vector3 touchPos;
    public long startedMoving;
    public boolean startMoving;
    /* ........................................................................... CONSTRUCTOR .. */
    public InputComponent() {
        skyDir = SkyDirection.S;
        moving = false;
        this.touchPos = new Vector3(0,0,0);
        this.startedMoving = 0;
        this.startMoving = false;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
