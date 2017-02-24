package de.limbusdev.guardianmonsters.fwmengine.world.model;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.MathUtils;

import de.limbusdev.guardianmonsters.geometry.IntVec2;

/**
 * Created by georg on 01.12.15.
 */
public class MapPersonInformation {
    /* ............................................................................ ATTRIBUTES .. */
    public String path;
    public IntVec2 startPosition;
    public boolean moves= false;
    public String name;
    public String conversation;
    public boolean male=false;
    public int spriteIndex=0;
    /* ........................................................................... CONSTRUCTOR .. */

    public MapPersonInformation(MapObject mo) {
        if(!mo.getProperties().containsKey("type") || !mo.getProperties().get("type").equals("person")) {
            throw new IllegalArgumentException("Given MapObject is not of type \"person\"");
        }

        construct(
            mo.getProperties().get("path", String.class),
            new IntVec2(
                MathUtils.round((((RectangleMapObject)mo).getRectangle().getX())),
                    MathUtils.round((((RectangleMapObject)mo).getRectangle().getY()))),
            Boolean.valueOf(mo.getProperties().get("static", String.class)),
            mo.getProperties().get("text", String.class),
            mo.getProperties().get("nameID", String.class),
            Boolean.parseBoolean(mo.getProperties().get("male", String.class)),
            Integer.parseInt(mo.getProperties().get("spriteIndex", String.class)));
    }

    public MapPersonInformation(String path, IntVec2 startPosition, boolean moves,
                                String conv, String name, boolean male, int spriteIndex) {
        construct(path, startPosition, moves, conv, name, male, spriteIndex);
    }

    private void construct(String path, IntVec2 startPosition, boolean moves,
                           String conv, String name, boolean male, int spriteIndex) {
        this.path = path;
        this.startPosition = startPosition;
        this.moves = moves;
        this.conversation = conv;
        this.male = male;
        this.spriteIndex = spriteIndex;
        if(name != null) this.name = name;
        else this.name = "";
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
