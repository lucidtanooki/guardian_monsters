package de.limbusdev.guardianmonsters.model.gamestate;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;

/**
 * SerializableAbility
 *
 * @author Georg Eckert 2017
 */

public class SerializableAbility
{
    public Element element;
    public int ID;

    @ForSerializationOnly
    public SerializableAbility() {}

    public SerializableAbility(Ability.aID ability)
    {
        this.element = ability.element;
        this.ID = ability.ID;
    }

    public static Ability.aID deserialize(SerializableAbility sAbility)
    {
        Ability.aID abilityID = new Ability.aID(sAbility.ID, sAbility.element);
        return abilityID;
    }
}
