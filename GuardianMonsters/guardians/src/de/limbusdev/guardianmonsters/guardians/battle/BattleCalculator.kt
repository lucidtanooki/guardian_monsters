package de.limbusdev.guardianmonsters.guardians.battle


import com.badlogic.gdx.math.MathUtils
import de.limbusdev.guardianmonsters.guardians.Constant

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.Logger
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.items.ChakraCrystalItem
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics.StatusEffect
import de.limbusdev.guardianmonsters.guardians.monsters.Team

/**
 * Handles events of monsters like level up, earning EXP, changing status and so on
 *
 * @author Georg Eckert
 */
object BattleCalculator
{
    private const val TAG: String = "BattleCalculator"

    /**
     * Call this, when a monster decides not to attack and instead defends itself
     * @param defender
     * @return
     */
    fun calcDefense(defender: AGuardian): AttackCalculationReport
    {
        Logger.selfDefense(TAG)
        val report = AttackCalculationReport(defender)
        defender.stats.modifyPDef(5)
        defender.stats.modifyMDef(5)

        return report
    }

    /**
     * Calculates attacks, the report attributes are for information only
     *
     * Damage = Elemental-Multiplier * ((0.5 * Level + 1) * Ability-Damage * (Strength/Defense) + 50) / 50
     *
     * Strength ... PStr / MStr, according to Ability Type
     * Defense  ... PDef / MDef, according to Ability Type
     *
     * @param attacker
     * @param defender
     * @return
     */
    fun calcAttack(attacker: AGuardian, defender: AGuardian, abilityID: Ability.aID): AttackCalculationReport
    {
        // Calculate Attack
        Logger.calcAttackHeader()
        val ability = GuardiansServiceLocator.abilities.getAbility(abilityID)

        val report = AttackCalculationReport(
                attacker,
                defender,
                ability,
                0,
                0f,
                false,
                StatusEffect.HEALTHY,
                false,
                0,
                0,
                0,
                0,
                0,
                false,
                0,
                0)

        // Consider current Status Effect of attacker
        when (attacker.stats.statusEffect)
        {
            StatusEffect.BLIND ->
            {
                // Hit the enemy with a change of 34%
                if (MathUtils.randomBoolean(0.66f)) {
                    report.statusEffectPreventedAttack = true
                    return report
                }
            }
            StatusEffect.SLEEPING ->
            {
                // Stay asleep at a chance of 66%
                if (MathUtils.randomBoolean(0.66f))
                {
                    report.statusEffectPreventedAttack = true
                    return report
                }
                // Wake up
                else
                {
                    attacker.stats.statusEffect = StatusEffect.HEALTHY
                }
            }
            StatusEffect.PETRIFIED ->
            {
                throw IllegalStateException(Logger.illegalStateCalcAttackWhenPetrified())
            }
            else /* HEALTHY, LUNATIC */ -> {}
        }

        // Elemental Efficiency
        val eff = ElementEfficiency.getElemEff(ability.element, defender.elements)


        val typeStrength = when (ability.damageType)
        {
            Ability.DamageType.MAGICAL -> attacker.stats.mStr
            else                       -> attacker.stats.pStr
        }

        val typeDefense = when (ability.damageType)
        {
            Ability.DamageType.MAGICAL -> defender.stats.mDef
            else                       -> attacker.stats.pDef
        }

        val abilityStrength = ability.damage
        val ratioSD = typeStrength.toFloat() / typeDefense.toFloat()

        val level = attacker.stats.level

        // Calculate Damage
        val damage = when(abilityStrength)
        {
            0    -> 0f
            else -> eff * ((0.5f * level + 1f) * abilityStrength * ratioSD + 50f) / 5f
        }

        report.damage = MathUtils.ceil(damage)
        report.efficiency = eff

        // Consider StatusEffect
        if (ability.canChangeStatusEffect && defender.stats.statusEffect == StatusEffect.HEALTHY)
        {
            val willChange = MathUtils.randomBoolean(ability.probabilityToChangeStatusEffect / 100f)
            if (willChange)
            {
                report.newStatusEffect = ability.statusEffect
                report.changedStatusEffect = true
            }
        }
        else
        {
            // Can't change StatusEffect if it is already changed
            report.changedStatusEffect = false
        }

        // Handle Stat changing
        if (ability.changesStats)
        {
            report.modifiedStat  = true
            report.modifiedPStr  = ability.addsPStr
            report.modifiedPDef  = ability.addsPDef
            report.modifiedMStr  = ability.addsMStr
            report.modifiedMDef  = ability.addsMDef
            report.modifiedSpeed = ability.addsSpeed
        }

        // Handle Stat curing
        if (ability.curesStats)
        {
            report.healedStat = true
            report.healedHP = ability.curesHP
            report.healedMP = ability.curesMP
        }

        Logger.attackCalculationAbstract(attacker, defender, ability, damage)

        return report
    }

    fun applyStatusEffect(guardian: AGuardian)
    {
        when (guardian.individualStatistics.statusEffect)
        {
            StatusEffect.POISONED  -> guardian.individualStatistics.decreaseHP(5)
            StatusEffect.PETRIFIED -> {}
            StatusEffect.LUNATIC   -> {}
            StatusEffect.BLIND     -> {}
            else /* HEALTHY */     -> {}
        }
    }

    fun banSucceeds(guardianToBeBanned: AGuardian, crystal: ChakraCrystalItem): Boolean
    {
        return true // TODO
        return MathUtils.randomBoolean(crystal.chance(guardianToBeBanned))
    }

    /**
     * Applies the previously calculated attack
     * @param report
     */
    fun apply(report: AttackCalculationReport)
    {
        Logger.applyAttackHeader()

        if (report.defending == null)           { Logger.applySelfDefense(TAG);          return }
        if (report.statusEffectPreventedAttack) { Logger.applyStatusPreventsAttack(TAG); return }

        val defending = report.defending
        val attacking = report.attacking
        checkNotNull(defending)

        Logger.attackAbstract(attacking, defending, report.attack)

        defending.stats.decreaseHP(report.damage)
        attacking.stats.decreaseMP(report.attack.MPcost)

        // Apply Status Effect
        if (report.changedStatusEffect)
        {
            defending.stats.statusEffect = report.newStatusEffect
        }

        // Apply Stat Change
        if (report.modifiedStat)
        {
            defending.stats.modifyPStr(report.modifiedPStr)
            defending.stats.modifyPDef(report.modifiedPDef)
            defending.stats.modifyMStr(report.modifiedMStr)
            defending.stats.modifyMDef(report.modifiedMDef)
            defending.stats.modifySpeed(report.modifiedSpeed)
        }

        // Apply Stat Cure
        if (report.healedStat)
        {
            defending.stats.healHP(report.healedHP)
            defending.stats.healMP(report.healedMP)
        }
    }

    /** Calculates, if escaping succeeds with the given teams. */
    fun runSucceeds(escapingTeam: Team, attackingTeam: Team): Boolean
    {
        val chanceToRun = when(attackingTeam.meanLevel > escapingTeam.meanLevel)
        {
            true  -> Constant.escapeChanceWeaker
            false -> Constant.escapeChanceStronger
        }

        return MathUtils.randomBoolean(chanceToRun)
    }

    fun calculateEarnedEXP(victoriousG: AGuardian, defeatedG: AGuardian): Int
    {
        val victoriousLevel = victoriousG.stats.level
        val defeatedLevel = defeatedG.stats.level

        return MathUtils.floor(200f * (1.5f * defeatedLevel * defeatedLevel) / (6f * victoriousLevel))
    }
}
