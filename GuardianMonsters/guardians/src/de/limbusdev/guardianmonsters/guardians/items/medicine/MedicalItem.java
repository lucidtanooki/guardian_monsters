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
                m.getIndividualStatistics().healHP(MathUtils.round(m.getIndividualStatistics().getHPmax()*value/100f));
                break;
            case HP_CURE:
                m.getIndividualStatistics().healHP(value);
                break;
            case MP_CURE:
                m.getIndividualStatistics().healMP(value);
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
                return m.getIndividualStatistics().isKO();
            case HP_CURE:
                return (m.getIndividualStatistics().isFit() && m.getIndividualStatistics().getHP() < m.getIndividualStatistics().getHPmax());
            case MP_CURE:
                return m.getIndividualStatistics().getMP() < m.getIndividualStatistics().getMPmax();
            default:
                return false;
        }
    }
}