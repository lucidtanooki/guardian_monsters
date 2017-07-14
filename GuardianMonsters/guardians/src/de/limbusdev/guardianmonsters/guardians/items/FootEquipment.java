package de.limbusdev.guardianmonsters.guardians.items;

import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;

/**
 * FootEquipment
 *
 * @author Georg Eckert 2017
 */



public class FootEquipment extends Equipment {

    public enum Type {
        SHOES, SHINPROTECTION, HORSESHOE, KNEEPADS,
    }

    private Type type;

    public FootEquipment(String name, Type type, int PStr, int PDef, int MStr, int MDef, int Speed, int HP, int MP, int Exp) {
        super(name, BodyPart.FEET, PStr, PDef, MStr, MDef, Speed, HP, MP, Exp);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean applicable(Guardian m) {
        if(super.applicable(m)) {
            return (m.data.getFootType() == type);
        } else {
            return false;
        }
    }
}