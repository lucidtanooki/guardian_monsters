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
    public String nameID;
    public ArrayMap<Integer,Attack> learnableAttacks;
    public boolean canEvolve;
    public int evolvingAtLevel;
    public int evolution;
    public Array<Element> elements;
    public BaseStat baseStat;
    private Equipment.HeadEquipment compatibleHeadEquip;
    private Equipment.BodyEquipment compatibleBodyEquip;
    private Equipment.HandEquipment compatibleHandEquip;
    private Equipment.FootEquipment compatibleFootEquip;

    /* ........................................................................... CONSTRUCTOR .. */

    /**
     *
     * @param ID                ID
     * @param nameID            nameID
     * @param learnableAttacks  all attacks with the level where they get learned
     * @param canEvolve         whether monster can reach another evolution state
     * @param evolution         next evolution level (2 for 2_2)
     * @param elements          monsters elements
     */
    public MonsterStatusInformation(
        int ID, String nameID, ArrayMap<Integer, Attack> learnableAttacks,
        boolean canEvolve, int evolution, int evolvingAtLevel, Array<Element> elements,
        BaseStat baseStat,
        Equipment.HeadEquipment head,
        Equipment.BodyEquipment body,
        Equipment.HandEquipment hands,
        Equipment.FootEquipment feet) {

        this.ID = ID;
        this.nameID = nameID;
        this.learnableAttacks = learnableAttacks;
        this.canEvolve = canEvolve;
        this.evolution = evolution;
        this.evolvingAtLevel = evolvingAtLevel;
        this.elements = elements;
        this.baseStat = baseStat;
        this.compatibleHandEquip = hands;
        this.compatibleBodyEquip = body;
        this.compatibleHeadEquip = head;
        this.compatibleFootEquip = feet;
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */

    public Equipment.HeadEquipment getCompatibleHeadEquip() {
        return compatibleHeadEquip;
    }

    public Equipment.BodyEquipment getCompatibleBodyEquip() {
        return compatibleBodyEquip;
    }

    public Equipment.HandEquipment getCompatibleHandEquip() {
        return compatibleHandEquip;
    }

    public Equipment.FootEquipment getCompatibleFootEquip() {
        return compatibleFootEquip;
    }
}
