package de.limbusdev.guardianmonsters.fwmengine.world.model;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;

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

    public MapObjectInformation(MapObject mo) {
        if(!mo.getProperties().containsKey("type") || !mo.getProperties().get("type").equals("objectDescription")) {
            throw new IllegalArgumentException("Given MapObject is not of type \"descriptionObject\"");
        }

        construct(mo.getProperties().get("title", String.class),
            mo.getProperties().get("text", String.class),
            MathUtils.round(mo.getProperties().get("x", Float.class)),
            MathUtils.round(mo.getProperties().get("y", Float.class)));


    }

    public MapObjectInformation(String title, String content, int x, int y) {
        construct(title, content, x, y);
    }

    private void construct(String title, String content, int x, int y) {
        this.title = title;
        this.content = content;
        this.x = x;
        this.y = y;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
