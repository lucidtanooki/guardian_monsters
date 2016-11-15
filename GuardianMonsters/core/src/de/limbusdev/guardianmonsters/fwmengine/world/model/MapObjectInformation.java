package de.limbusdev.guardianmonsters.fwmengine.world.model;

import com.badlogic.ashley.core.Component;

/**
 * Holds Information for simple text objects.
 *
 * Created by georg on 03.12.15.
 */
public class MapObjectInformation implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public String title;
    public String content;
    public int x,y;
    /* ........................................................................... CONSTRUCTOR .. */
    public MapObjectInformation(String title, String content, int x, int y) {
        this.title = title;
        this.content = content;
        this.x = x;
        this.y = y;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
