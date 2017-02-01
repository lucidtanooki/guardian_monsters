package de.limbusdev.guardianmonsters.fwmengine.world.model;

import de.limbusdev.guardianmonsters.geometry.IntVector2;

/**
 * Created by georg on 01.12.15.
 */
public class MapPersonInformation {
    /* ............................................................................ ATTRIBUTES .. */
    public String path;
    public IntVector2 startPosition;
    public boolean moves= false;
    public String name;
    public String conversation;
    public boolean male=false;
    public int spriteIndex=0;
    /* ........................................................................... CONSTRUCTOR .. */
    public MapPersonInformation(String path, IntVector2 startPosition, boolean moves,
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
