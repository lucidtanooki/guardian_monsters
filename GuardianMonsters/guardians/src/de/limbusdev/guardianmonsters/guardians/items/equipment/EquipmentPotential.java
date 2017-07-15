package de.limbusdev.guardianmonsters.guardians.items.equipment;

/**
 * EquipmentPotential
 * <p>
 * Holds positive and negative values to show how much a given {@link Equipment} would improve
 * the various monster status values
 *
 * @author Georg Eckert 2017
 */

public class EquipmentPotential {
    public int hp, mp, speed, exp, pstr, pdef, mstr, mdef;

    public EquipmentPotential(int hp, int mp, int speed, int exp, int pstr, int pdef, int mstr, int mdef) {
        this.hp = hp;
        this.mp = mp;
        this.speed = speed;
        this.exp = exp;
        this.pstr = pstr;
        this.pdef = pdef;
        this.mstr = mstr;
        this.mdef = mdef;
    }
}
