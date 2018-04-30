/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

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
    public AGuardian attacking;
    public AGuardian defending;
    public Ability attack;

    public boolean statusEffectPreventedAttack;
    public int damage;
    public float efficiency;
    public boolean changedStatusEffect;
    public IndividualStatistics.StatusEffect newStatusEffect;
    public boolean modifiedStat;
    public int modifiedPStr, modifiedPDef, modifiedMStr, modifiedMDef, modifiedSpeed;
    public boolean healedStat;
    public int healedHP, healedMP;

    public AttackCalculationReport(
        AGuardian attacking,
        AGuardian defending,
        Ability attack,
        int damage,
        float efficiency,
        boolean changedStatusEffect,
        IndividualStatistics.StatusEffect newStatusEffect,
        boolean modifiedStat,
        int modifiedPStr,
        int modifiedPDef,
        int modifiedMStr,
        int modifiedMDef,
        int modifiedSpeed,
        boolean healedStat,
        int healedHP,
        int healedMP)
    {
        this.attacking = attacking;
        this.defending = defending;
        this.attack = attack;
        this.damage = damage;
        this.efficiency = efficiency;
        this.changedStatusEffect = changedStatusEffect;
        this.newStatusEffect = newStatusEffect;
        this.modifiedStat = modifiedStat;
        this.modifiedPStr = modifiedPStr;
        this.modifiedPDef = modifiedPDef;
        this.modifiedMStr = modifiedMStr;
        this.modifiedMDef = modifiedMDef;
        this.modifiedSpeed = modifiedSpeed;
        this.healedStat = healedStat;
        this.healedHP = healedHP;
        this.healedMP = healedMP;
        this.statusEffectPreventedAttack = false;
    }

    /**
     * Report for defending monster
     * @param defending
     */
    public AttackCalculationReport(AGuardian defending)
    {
        this.attacking = defending;
        this.defending = null;
        this.damage = 0;
        this.efficiency = 0;
        this.attack = new Ability(0, Ability.DamageType.PHYSICAL, Element.NONE, 0, "");
        this.changedStatusEffect = false;
        this.newStatusEffect = IndividualStatistics.StatusEffect.HEALTHY;
    }
}
