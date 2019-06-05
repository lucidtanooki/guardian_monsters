/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.guardians.battle

import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics

/**
 * Contains information about the effect of an attack on another monster
 *
 * @author Georg Eckert 2019
 */
class AttackCalculationReport
{
    var attacking : AGuardian
    var defending : AGuardian?
    var attack    : Ability

    var statusEffectPreventedAttack : Boolean = false
    var damage                      : Int = 0
    var efficiency                  : Float = 0.0f
    var changedStatusEffect         : Boolean = false
    var newStatusEffect             : IndividualStatistics.StatusEffect
    var modifiedStat                : Boolean = false
    var modifiedPStr                : Int = 0
    var modifiedPDef                : Int = 0
    var modifiedMStr                : Int = 0
    var modifiedMDef                : Int = 0
    var modifiedSpeed               : Int = 0
    var healedHP                    : Int = 0
    var healedMP                    : Int = 0
    var healedStat                  : Boolean = false

    constructor
    (
            attacking: AGuardian,
            defending: AGuardian,
            attack: Ability,
            damage: Int,
            efficiency: Float,
            changedStatusEffect: Boolean,
            newStatusEffect: IndividualStatistics.StatusEffect,
            modifiedStat: Boolean,
            modifiedPStr: Int,
            modifiedPDef: Int,
            modifiedMStr: Int,
            modifiedMDef: Int,
            modifiedSpeed: Int,
            healedStat: Boolean,
            healedHP: Int,
            healedMP: Int
    ){
        this.attacking = attacking
        this.defending = defending
        this.attack = attack
        this.damage = damage
        this.efficiency = efficiency
        this.changedStatusEffect = changedStatusEffect
        this.newStatusEffect = newStatusEffect
        this.modifiedStat = modifiedStat
        this.modifiedPStr = modifiedPStr
        this.modifiedPDef = modifiedPDef
        this.modifiedMStr = modifiedMStr
        this.modifiedMDef = modifiedMDef
        this.modifiedSpeed = modifiedSpeed
        this.healedStat = healedStat
        this.healedHP = healedHP
        this.healedMP = healedMP
        this.statusEffectPreventedAttack = false
    }

    /**
     * Report for defending monster
     * @param defending
     */
    constructor(defending: AGuardian)
    {
        this.attacking = defending
        this.defending = null
        this.damage = 0
        this.efficiency = 0f
        this.attack = Ability(0, Ability.DamageType.PHYSICAL, Element.NONE, 0, "")
        this.changedStatusEffect = false
        this.newStatusEffect = IndividualStatistics.StatusEffect.HEALTHY
    }
}
