package de.limbusdev.guardianmonsters.guardians.battle;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;

/**
 * Contains information about the effect of an attack on another monster
 *
 * @author Georg Eckert 2017
 */
public class AttackCalculationReport
{
    public AGuardian attacker;
    public AGuardian defender;
    public Ability attack;

    public int damage;
    public float efficiency;

    public AttackCalculationReport(AGuardian attacker, AGuardian defender, int damage, float efficiency, Ability ability)
    {
        this.attacker = attacker;
        this.defender = defender;
        this.damage = damage;
        this.efficiency = efficiency;
        this.attack = ability;
    }

    /**
     * Report for defending monster
     * @param defender
     */
    public AttackCalculationReport(AGuardian defender)
    {
        this.attacker = defender;
        this.defender = null;
        this.damage = 0;
        this.efficiency = 0;
        this.attack = new Ability(0, Ability.DamageType.PHYSICAL, Element.NONE, 0, "");
    }
}
