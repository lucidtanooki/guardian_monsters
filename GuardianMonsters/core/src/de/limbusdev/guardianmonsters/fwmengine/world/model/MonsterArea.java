package de.limbusdev.guardianmonsters.fwmengine.world.model;

import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.geometry.IntRectangle;


/**
 * Created by georg on 17.12.15.
 */
public class MonsterArea extends IntRectangle {
    /* ............................................................................ ATTRIBUTES .. */
    public Array<Integer> monsters;
    public Array<Float> monsterProbabilities;
    public Array<Float> attackProbabilities; // for 1, 2 or 3 monsters
    /* ........................................................................... CONSTRUCTOR .. */


    public MonsterArea(int x, int y, int width, int height, String monsterProperties, Array<Float>
            attProb) {
        super(x, y, width, height);
        this.monsters = new Array<Integer>();
        this.monsterProbabilities = new Array<Float>();
        this.attackProbabilities = attProb;
        String[] atts = monsterProperties.split(";");
        for(int i=0; i<atts.length; i+=2) {
            monsters.add(Integer.parseInt(atts[i]));
            monsterProbabilities.add(Float.parseFloat(atts[i+1]));
        }
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
