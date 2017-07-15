package de.limbusdev.guardianmonsters.guardians.items.medicine;

import com.badlogic.gdx.math.MathUtils;

import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;

/**
 * Medicine
 *
 * @author Georg Eckert 2017
 */

public class MedicalItem extends AMedicalItem
{
    public enum Type
    {
        REVIVE, HP_CURE, MP_CURE, STATUS_CURE,
    }

    private int value;
    private Type type;

    /**
     * Implementation of @link{AMedicalItem}, do not use this outside of this module, always use
     * AMedicalItem.
     * @param name
     * @param value
     * @param type
     */
    public MedicalItem(String name, int value, Type type)
    {
        super(name);
        this.value = value;
        this.type = type;
    }

    @Override
    public void apply(Guardian m)
    {
        switch(type)
        {
            case REVIVE:
                m.stat.healHP(MathUtils.round(m.stat.getHPmax()*value/100f));
                break;
            case HP_CURE:
                m.stat.healHP(value);
                break;
            case MP_CURE:
                m.stat.healMP(value);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean applicable(Guardian m)
    {
        switch(type) {
            case REVIVE:
                return m.stat.isKO();
            case HP_CURE:
                return (m.stat.isFit() && m.stat.getHP() < m.stat.getHPmax());
            case MP_CURE:
                return m.stat.getMP() < m.stat.getMPmax();
            default:
                return false;
        }
    }
}