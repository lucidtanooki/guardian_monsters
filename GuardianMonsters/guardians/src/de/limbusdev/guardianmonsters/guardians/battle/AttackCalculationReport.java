package de.limbusdev.guardianmonsters.guardians.battle;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;

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
    public boolean changedStatusEffect;
    public IndividualStatistics.StatusEffect newStatusEffect;

    public AttackCalculationReport(
        AGuardian attacker, AGuardian defender,
        int damage, float efficiency, Ability ability,
        boolean changedStatusEffect, IndividualStatistics.StatusEffect newStatusEffect)
    {
        this.attacker = attacker;
        this.defender = defender;
        this.damage = damage;
        this.efficiency = efficiency;
        this.attack = ability;
        this.changedStatusEffect = changedStatusEffect;
        this.newStatusEffect = newStatusEffect;
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
        this.changedStatusEffect = false;
        this.newStatusEffect = IndividualStatistics.StatusEffect.HEALTHY;
    }
}
