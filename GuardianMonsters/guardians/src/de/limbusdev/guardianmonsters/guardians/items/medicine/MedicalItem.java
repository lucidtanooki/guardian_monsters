package de.limbusdev.guardianmonsters.guardians.items.medicine;

import com.badlogic.gdx.math.MathUtils;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;

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
    public void apply(AGuardian m)
    {
        switch(type)
        {
            case REVIVE:
                m.getStatistics().healHP(MathUtils.round(m.getStatistics().getHPmax()*value/100f));
                break;
            case HP_CURE:
                m.getStatistics().healHP(value);
                break;
            case MP_CURE:
                m.getStatistics().healMP(value);
                break;
            default:
                break;
        }
    }

    @Override
    public boolean applicable(AGuardian m)
    {
        switch(type) {
            case REVIVE:
                return m.getStatistics().isKO();
            case HP_CURE:
                return (m.getStatistics().isFit() && m.getStatistics().getHP() < m.getStatistics().getHPmax());
            case MP_CURE:
                return m.getStatistics().getMP() < m.getStatistics().getMPmax();
            default:
                return false;
        }
    }
}