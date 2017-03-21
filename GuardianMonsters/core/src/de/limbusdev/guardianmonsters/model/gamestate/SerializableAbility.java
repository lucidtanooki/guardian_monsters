package de.limbusdev.guardianmonsters.model.gamestate;

import de.limbusdev.guardianmonsters.model.AbilityDB;
import de.limbusdev.guardianmonsters.model.abilities.Ability;
import de.limbusdev.guardianmonsters.model.monsters.Element;

/**
 * SerializableAbility
 *
 * @author Georg Eckert 2017
 */

public class SerializableAbility {
    public Element element;
    public int ID;

    @ForSerializationOnly
    public SerializableAbility() {}

    public SerializableAbility(Ability ability) {
        this.element = ability.element;
        this.ID = ability.ID;
    }

    public static Ability deserialize(SerializableAbility sAbility) {
        Ability ability = AbilityDB.getAttack(sAbility.element, sAbility.ID);
        return ability;
    }
}
