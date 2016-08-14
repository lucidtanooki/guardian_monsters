package de.limbusdev.guardianmonsters.model;

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
    public Element element;
    public boolean canEvolve;
    public int evolvingAtLevel;
    public int evolution;

    /* ........................................................................... CONSTRUCTOR .. */

    /**
     *
     * @param ID                ID
     * @param name              name
     * @param learnableAttacks  all attacks with the level where they get learned
     * @param element           main element
     * @param canEvolve         whether monster can reach another evolution state
     * @param evolution         next evolution level (2 for 2_2)
     */
    public MonsterStatusInformation(
            int ID, String name, ArrayMap<Integer, Attack> learnableAttacks,
            Element element, boolean canEvolve, int evolution,
            int evolvingAtLevel) {

        this.ID = ID;
        this.name = name;
        this.learnableAttacks = learnableAttacks;
        this.element = element;
        this.canEvolve = canEvolve;
        this.evolution = evolution;
        this.evolvingAtLevel = evolvingAtLevel;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
