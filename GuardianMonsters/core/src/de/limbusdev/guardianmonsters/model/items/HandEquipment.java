package de.limbusdev.guardianmonsters.model.items;

import de.limbusdev.guardianmonsters.model.monsters.Monster;

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
    public boolean applicable(Monster m) {
        if(super.applicable(m)) {
            return (m.data.getHandType() == type);
        } else {
            return false;
        }
    }
}