package org.limbusdev.monsterworld.geometry;

/**
 * Created by georg on 01.12.15.
 */
public class MapPersonInformation {
    /* ............................................................................ ATTRIBUTES .. */
    public String path;
    public IntVector2 startPosition;
    public boolean moves= false;
    /* ........................................................................... CONSTRUCTOR .. */
    public MapPersonInformation(String path, IntVector2 startPosition, boolean moves) {
        this.path = path;
        this.startPosition = startPosition;
        this.moves = moves;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
