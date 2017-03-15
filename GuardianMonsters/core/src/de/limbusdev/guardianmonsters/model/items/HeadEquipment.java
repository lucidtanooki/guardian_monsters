package de.limbusdev.guardianmonsters.model.items;

import de.limbusdev.guardianmonsters.model.monsters.Monster;

/**
 * HeadEquipment
 *
 * @author Georg Eckert 2017
 */

public class HeadEquipment extends Equipment {

    public enum Type {
        HELMET, BRIDLE, MASK, HEADBAND,
    }

    private Type type;

    public HeadEquipment(String name, Type type, int PStr, int PDef, int MStr, int MDef, int Speed, int HP, int MP, int Exp) {
        super(name, BodyPart.HEAD, PStr, PDef, MStr, MDef, Speed, HP, MP, Exp);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean applicable(Monster m) {
        if(super.applicable(m)) {
            return (m.data.getHeadType() == type);
        } else {
            return false;
        }
    }
}