package de.limbusdev.guardianmonsters.guardians.items.equipment;


import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;

/**
 * HandEquipment
 *
 * @author Georg Eckert 2017
 */
public class HandEquipment extends Equipment {

    public enum Type {
        SWORD, PATA, BRACELET, CLAWS,
    }

    private Type type;

    public HandEquipment(String name, Type type, int PStr, int PDef, int MStr, int MDef, int Speed, int HP, int MP, int Exp) {
        super(name, BodyPart.HANDS, PStr, PDef, MStr, MDef, Speed, HP, MP, Exp);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equipable(AGuardian m) {
        if(super.equipable(m)) {
            return (m.getSpeciesData().getHandType() == type);
        } else {
            return false;
        }
    }
}