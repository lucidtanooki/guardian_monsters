package de.limbusdev.guardianmonsters.guardians.items.equipment;


import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;

/**
 * BodyEquipment
 *
 * @author Georg Eckert 2017
 */

public class BodyEquipment extends Equipment {

    public enum Type {
        ARMOR, BARDING, SHIELD, BREASTPLATE,
    }

    private Type type;

    public BodyEquipment(String name, Type type, int PStr, int PDef, int MStr, int MDef, int Speed, int HP, int MP, int Exp) {
        super(name, BodyPart.BODY, PStr, PDef, MStr, MDef, Speed, HP, MP, Exp);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equipable(AGuardian m) {
        if(super.equipable(m)) {
            return (m.getSpeciesDescription().getBodyType() == type);
        } else {
            return false;
        }
    }
}