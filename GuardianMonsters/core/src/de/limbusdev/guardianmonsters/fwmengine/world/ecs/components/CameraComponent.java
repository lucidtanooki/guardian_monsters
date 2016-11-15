package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components;

import com.badlogic.ashley.core.Component;

import de.limbusdev.guardianmonsters.geometry.IntVector2;


/**
 * Simple {@link Component} to store the camera position. Only {@link com.badlogic.ashley.core
 * .Entity}s which should be followed by the camera should get one.
 * Created by georg on 25.11.15.
 */
public class CameraComponent implements Component{
    /* ............................................................................ ATTRIBUTES .. */
    public IntVector2 position = new IntVector2(0,0);
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
