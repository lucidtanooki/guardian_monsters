package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.enums.Element;


/**
 * Created by georg on 24.01.16.
 */
public class MonsterStatusInformation {
    /* ............................................................................ ATTRIBUTES .. */
    public int ID;
    public String name;
    public ArrayMap<Integer,Attack> learnableAttacks;
    public boolean canEvolve;
    public int evolvingAtLevel;
    public int evolution;
    public Array<Element> elements;
    public BaseStat baseStat;

    /* ........................................................................... CONSTRUCTOR .. */

    /**
     *
     * @param ID                ID
     * @param name              name
     * @param learnableAttacks  all attacks with the level where they get learned
     * @param canEvolve         whether monster can reach another evolution state
     * @param evolution         next evolution level (2 for 2_2)
     * @param elements          monsters elements
     */
    public MonsterStatusInformation(
        int ID, String name, ArrayMap<Integer, Attack> learnableAttacks,
        boolean canEvolve, int evolution, int evolvingAtLevel, Array<Element> elements,
        BaseStat baseStat) {

        this.ID = ID;
        this.name = name;
        this.learnableAttacks = learnableAttacks;
        this.canEvolve = canEvolve;
        this.evolution = evolution;
        this.evolvingAtLevel = evolvingAtLevel;
        this.elements = elements;
        this.baseStat = baseStat;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
