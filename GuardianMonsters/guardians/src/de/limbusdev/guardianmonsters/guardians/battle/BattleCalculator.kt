package de.limbusdev.guardianmonsters.guardians.battle


import com.badlogic.gdx.math.MathUtils

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.items.ChakraCrystalItem
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics
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
        println("$TAG: Monster defends itself")
        val report = AttackCalculationReport(defender)
        defender.individualStatistics.modifyPDef(5)
        defender.individualStatistics.modifyMDef(5)

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
        println("\n--- new ability ---")
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
        when (attacker.individualStatistics.statusEffect)
        {
            StatusEffect.BLIND ->
                if (MathUtils.randomBoolean(0.66f))
                {
                    report.statusEffectPreventedAttack = true
                    return report
                }
            StatusEffect.SLEEPING ->
                if (MathUtils.randomBoolean(0.66f))
                {
                    report.statusEffectPreventedAttack = true
                    return report
                }
                else
                {
                    attacker.individualStatistics.statusEffect = StatusEffect.HEALTHY
                }
            StatusEffect.PETRIFIED ->
                throw IllegalStateException("calcAttack() can't be called, when Guardian is petrified.")
            else /*case HEALTHY || LUNATIC:*/ -> {}
        }

        // Elemental Efficiency
        val eff = ElemEff.getElemEff(ability.element, defender.speciesDescription.getElements(0))   // TODO elements currentForm

        val statAtt = attacker.individualStatistics
        val statDef = defender.individualStatistics

        val typeStrength = when (ability.damageType)
        {
            Ability.DamageType.MAGICAL -> statAtt.mStr
            else                       -> statAtt.pStr
        }

        val typeDefense = when (ability.damageType)
        {
            Ability.DamageType.MAGICAL -> statDef.mDef
            else                       -> statAtt.pDef
        }

        val abilityStrength = ability.damage
        val ratioSD = typeStrength.toFloat() / typeDefense.toFloat()

        val level = statAtt.level

        /* Calculate Damage */
        val damage =
                if (abilityStrength == 0) 0f
                else eff * ((0.5f * level + 1) * abilityStrength.toFloat() * ratioSD + 50) / 5f

        report.damage = MathUtils.ceil(damage)
        report.efficiency = eff

        // Consider StatusEffect
        if (ability.canChangeStatusEffect && statDef.statusEffect == StatusEffect.HEALTHY)
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

        // Print Battle Debug Message
        println("${attacker.uuid}: ${ability.name} causes $damage damage on ${defender.uuid}")

        return report
    }

    fun applyStatusEffect(guardian: AGuardian)
    {
        when (guardian.individualStatistics.statusEffect)
        {
            StatusEffect.POISONED  -> guardian.individualStatistics.decreaseHP(5)
            StatusEffect.PETRIFIED,
            StatusEffect.LUNATIC,
            StatusEffect.BLIND     -> {}
            else /*case HEALTHY:*/ -> {}
        }
    }

    fun banSucceeds(guardianToBeBanned: AGuardian, crystal: ChakraCrystalItem): Boolean
    {
        return MathUtils.randomBoolean(crystal.chance(guardianToBeBanned))
    }

    /**
     * Applies the previously calculated attack
     * @param report
     */
    fun apply(report: AttackCalculationReport)
    {
        if (report.defending == null)
        {
            println("$TAG: Only self defending")
            return
        }

        if (report.statusEffectPreventedAttack)
        {
            println("$TAG: Status Effect prevented attack.")
            return
        }

        val defending = report.defending
        val attacking = report.attacking

        checkNotNull(defending)

        println("${report.attacking.uuid} attacks ${defending.uuid} with ${report.attack.name}")
        defending.individualStatistics.decreaseHP(report.damage)
        attacking.individualStatistics.decreaseMP(report.attack.MPcost)

        // Apply Status Effect
        if (report.changedStatusEffect)
        {
            defending.individualStatistics.statusEffect = report.newStatusEffect
        }

        // Apply Stat Change
        if (report.modifiedStat)
        {
            defending.individualStatistics.modifyPStr(report.modifiedPStr)
            defending.individualStatistics.modifyPDef(report.modifiedPDef)
            defending.individualStatistics.modifyMStr(report.modifiedMStr)
            defending.individualStatistics.modifyMDef(report.modifiedMDef)
            defending.individualStatistics.modifySpeed(report.modifiedSpeed)
        }

        // Apply Stat Cure
        if (report.healedStat)
        {
            defending.individualStatistics.healHP(report.healedHP)
            defending.individualStatistics.healMP(report.healedMP)
        }
    }

    fun tryToRun(escapingTeam: Team, attackingTeam: Team): Boolean
    {
        var meanEscapingTeamLevel = 0f
        var meanAttackingTeamLevel = 0f

        for (m in escapingTeam.values())
        {
            if (m.individualStatistics.isFit)
            {
                meanEscapingTeamLevel += m.individualStatistics.level
            }
        }
        meanEscapingTeamLevel /= escapingTeam.size.toFloat()

        for (m in attackingTeam.values())
        {
            meanAttackingTeamLevel += m.individualStatistics.level
        }
        meanAttackingTeamLevel /= escapingTeam.size.toFloat()

        return if (meanAttackingTeamLevel > meanEscapingTeamLevel) { MathUtils.randomBoolean(.2f) }
        else                                                       { MathUtils.randomBoolean(.9f) }
    }

    fun calculateEarnedEXP(victoriousG: AGuardian, defeatedG: AGuardian): Int
    {
        val victoriousLevel = victoriousG.individualStatistics.level
        val defeatedLevel = defeatedG.individualStatistics.level

        return MathUtils.floor(
                200f * (1.5f * defeatedLevel * defeatedLevel) / (6f * victoriousLevel)
        )
    }
}
