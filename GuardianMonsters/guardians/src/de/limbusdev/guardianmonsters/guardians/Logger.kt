package de.limbusdev.guardianmonsters.guardians

import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian

object Logger
{
    private const val ON = true

    fun selfDefense(TAG: String)
    {
        if(ON) println("$TAG: Guardian defends itself.")
    }

    /** Prints: == Calculating Attack: == */
    fun calcAttackHeader()
    {
        if(!ON) return

        println("\n== Calculating Attack: ==")
    }

    fun applyAttackHeader()
    {
        if(!ON) return

        println("\n== Applying Attack: ==")
    }

    fun applySelfDefense(TAG: String)
    {
        if(!ON) return

        println("$TAG: Only self defending")
    }

    fun applyStatusPreventsAttack(TAG: String)
    {
        if(!ON) return

        println("$TAG: Status Effect prevented attack.")
    }


    fun attackCalculationAbstract(attacker: AGuardian, defender: AGuardian, usedAbility: Ability, damage: Float)
    {
        if(!ON) return

        val attackerName = attacker.species.getSimpleName(attacker.currentForm).toUpperCase()
        val defenderName = defender.species.getSimpleName(defender.currentForm).toUpperCase()
        println("$attackerName will cause $damage damage on $defenderName with ${usedAbility.simpleName.toUpperCase()}")
    }

    fun attackAbstract(attacker: AGuardian, defender: AGuardian, ability: Ability)
    {
        if(!ON) return

        val attackerName = attacker.species.getSimpleName(attacker.currentForm).toUpperCase()
        val defenderName = defender.species.getSimpleName(defender.currentForm).toUpperCase()
        println("$attackerName attacks $defenderName with ${ability.simpleName.toUpperCase()}")
    }

    fun illegalStateCalcAttackWhenPetrified(): String
    {
        return "calcAttack() can't be called, when Guardian is petrified."
    }
}