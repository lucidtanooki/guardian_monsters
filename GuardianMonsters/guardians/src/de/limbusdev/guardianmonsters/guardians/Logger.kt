package de.limbusdev.guardianmonsters.guardians

import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.utils.log
import de.limbusdev.utils.logInfo

object Logger
{
    fun selfDefense(TAG: String)
    {
        logInfo(TAG) { "Guardian defends itself." }
    }

    /** Prints: == Calculating Attack: == */
    fun calcAttackHeader()
    {
        log() { "\n== Calculating Attack: ==" }
    }

    fun applyAttackHeader()
    {
        log() { "\n== Applying Attack: ==" }
    }

    fun applySelfDefense(TAG: String)
    {
        logInfo(TAG) { "Only self defending" }
    }

    fun applyStatusPreventsAttack(TAG: String)
    {
        logInfo(TAG) { "Status Effect prevented attack." }
    }


    fun attackCalculationAbstract(attacker: AGuardian, defender: AGuardian, usedAbility: Ability, damage: Float)
    {
        val attackerName = attacker.species.getSimpleName(attacker.currentForm).toUpperCase()
        val defenderName = defender.species.getSimpleName(defender.currentForm).toUpperCase()

       log() { "$attackerName will cause $damage damage on $defenderName with ${usedAbility.simpleName.toUpperCase()}" }
    }

    fun attackAbstract(attacker: AGuardian, defender: AGuardian, ability: Ability)
    {
        val attackerName = attacker.species.getSimpleName(attacker.currentForm).toUpperCase()
        val defenderName = defender.species.getSimpleName(defender.currentForm).toUpperCase()

        log() { "$attackerName attacks $defenderName with ${ability.simpleName.toUpperCase()}" }
    }

    fun illegalStateCalcAttackWhenPetrified(): String
    {
        return "calcAttack() can't be called, when Guardian is petrified."
    }
}