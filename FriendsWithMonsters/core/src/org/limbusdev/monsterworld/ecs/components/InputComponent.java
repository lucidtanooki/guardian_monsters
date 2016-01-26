package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

import org.limbusdev.monsterworld.enums.SkyDirection;

/**
 * Created by georg on 22.11.15.
 */
public class InputComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public SkyDirection skyDir, nextInput;
    public boolean moving;
    public Vector3 touchPos;
    public long startedTileStep;
    public boolean startTileStep;
    public boolean talking;
    public boolean inBattle;
    public boolean touchDown;
    /* ........................................................................... CONSTRUCTOR .. */
    public InputComponent() {
        skyDir = nextInput = SkyDirection.S;
        moving = false;
        this.touchPos = new Vector3(0,0,0);
        this.startedTileStep = 0;
        this.startTileStep = false;
        this.talking = false;
        this.inBattle = false;
        this.touchDown = false;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
