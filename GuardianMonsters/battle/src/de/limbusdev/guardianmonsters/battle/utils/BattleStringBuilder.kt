package de.limbusdev.guardianmonsters.battle.utils

import com.badlogic.gdx.utils.Array

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.battle.AttackCalculationReport
import de.limbusdev.guardianmonsters.guardians.items.Item
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian
import de.limbusdev.guardianmonsters.guardians.monsters.ISpeciesDescriptionService
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics
import de.limbusdev.guardianmonsters.services.Services

/**
 * @author Georg Eckert 2017
 */
object BattleStringBuilder
{
    private fun species()                           = GuardiansServiceLocator.species
    private fun commonName(id: Int, form: Int)      = species().getCommonNameById(id, form)
    private fun tryGetNickName(guardian: AGuardian) = Services.getL18N().getGuardianNicknameIfAvailable(guardian)
    private fun itemName(id: String)                = Services.getL18N().Inventory().get(id)
    private fun abilityName(id: String)             = Services.getL18N().getLocalizedAbilityName(id)
    private fun l18nBattle()                        = Services.getL18N().Battle()

    fun tryingToBanGuardian(guardian: AGuardian, item: Item): String
    {
        return l18nBattle().format(
                "batt_message_ban_trial",
                tryGetNickName(guardian),
                itemName(item.name)
        )
    }

    fun banGuardianFailure(guardian: AGuardian, item: Item): String
    {
        return l18nBattle().format(
                "batt_message_ban_failure",
                tryGetNickName(guardian)
        )
    }

    fun banGuardianSuccess(guardian: AGuardian, item: Item): String
    {
        return l18nBattle().format(
                "batt_message_ban_success",
                tryGetNickName(guardian)
        )
    }

    fun receivedDamage(victim: Guardian, damage: Int): String
    {
        return "${species().getCommonNameById(victim.speciesID, 0)} lost $damage HP"
    }

    fun givenDamage(attacker: AGuardian, victim: AGuardian, report: AttackCalculationReport): String
    {
        val attName = commonName(attacker.speciesID, 0)
        val defName = commonName(victim.speciesID, 0) // TODO currentForm

        val eff: String
        if(report.efficiency > 1.1) {
            eff = l18nBattle().get("suff_severe")
        }
        else if(report.efficiency < .9 && report.efficiency > 0.1) {
            eff = l18nBattle().get("suff_less")
        }
        else if(report.efficiency < 0) {
            eff = l18nBattle().get("suff_healed")
        }
        else {
            eff = l18nBattle().get("suff_normal")
        }

        var message: String

        if(attacker.individualStatistics.statusEffect === IndividualStatistics.StatusEffect.LUNATIC)
        {
            message = l18nBattle().format(
                    "batt_message_lunatic",
                    tryGetNickName(attacker),
                    tryGetNickName(victim),
                    abilityName(report.attack.name),
                    report.damage, eff,
                    l18nBattle().get("batt_lunatic"))
        }
        else
        {
            message = l18nBattle().format(
                    "batt_message",
                    tryGetNickName(attacker),
                    tryGetNickName(victim),
                    abilityName(report.attack.name),
                    report.damage, eff)
        }

        if(report.changedStatusEffect)
        {
            message += " " + l18nBattle().format("batt_message_status_effect_change",
                    tryGetNickName(victim),
                    l18nBattle().get("batt_change_" + report.newStatusEffect.toString().toLowerCase()))
        }

        if(report.statusEffectPreventedAttack)
        {
            message = l18nBattle().format(
                    "batt_message_failed",
                    l18nBattle().get("batt_" + attacker.individualStatistics.statusEffect.toString().toLowerCase()),
                    tryGetNickName(attacker)
            )
        }

        return message
    }

    /**
     * For area attacks
     * @param attacker
     * @param reports
     * @return
     */
    fun givenDamage(attacker: AGuardian, reports: Array<AttackCalculationReport>): String
    {
        val species = GuardiansServiceLocator.species
        val attName = species.getCommonNameById(attacker.speciesID, 0)

        var message: String
        if(attacker.individualStatistics.statusEffect === IndividualStatistics.StatusEffect.LUNATIC)
        {
            message = l18nBattle().format(
                    "batt_area_message_lunatic",
                    tryGetNickName(attacker),
                    abilityName(reports.first().attack.name),
                    l18nBattle().get("batt_lunatic")
            )
        }
        else
        {
            message = l18nBattle().format(
                    "batt_area_message",
                    tryGetNickName(attacker),
                    abilityName(reports.first().attack.name)
            )
        }
        message += " "

        val defName1: String
        val defName2: String
        val defName3: String
        var damage1: String
        var damage2: String
        var damage3: String
        val report1: AttackCalculationReport
        val report2: AttackCalculationReport
        val report3: AttackCalculationReport

        when(reports.size)
        {
            2 -> {
                report1 = reports.get(0)
                report2 = reports.get(1)
                defName1 = if(report1.defending == null) "" else tryGetNickName(report1.defending as AGuardian)
                defName2 = if(report2.defending == null) "" else tryGetNickName(report2.defending as AGuardian)
                damage1 = report1.damage.toString()
                damage2 = report2.damage.toString()

                if(report1.changedStatusEffect)
                    damage1 += ", " + l18nBattle().get("batt_change_area_${report1.newStatusEffect.toString().toLowerCase()}")

                if(report2.changedStatusEffect)
                    damage2 += ", " + l18nBattle().get("batt_change_area_${report2.newStatusEffect.toString().toLowerCase()}")

                message += l18nBattle().format("batt_area_message_2", defName1, damage1, defName2, damage2)
            }

            3 -> {
                report1 = reports.get(0)
                report2 = reports.get(1)
                report3 = reports.get(2)
                defName1 = if(report1.defending == null) "" else tryGetNickName(report1.defending as AGuardian)
                defName2 = if(report2.defending == null) "" else tryGetNickName(report2.defending as AGuardian)
                defName3 = if(report3.defending == null) "" else tryGetNickName(report3.defending as AGuardian)
                damage1 = report1.damage.toString()
                damage2 = report2.damage.toString()
                damage3 = report3.damage.toString()

                if(report1.changedStatusEffect)
                    damage1 += ", " + l18nBattle().get("batt_change_area_${report1.newStatusEffect.toString().toLowerCase()}")

                if(report2.changedStatusEffect)
                    damage2 += ", " + l18nBattle().get("batt_change_area_${report2.newStatusEffect.toString().toLowerCase()}")

                if(report3.changedStatusEffect)
                    damage3 += ", " + l18nBattle().get("batt_change_area_${report3.newStatusEffect.toString().toLowerCase()}")

                message += l18nBattle().format("batt_area_message_3", defName1, damage1, defName2, damage2, defName3, damage3)
                println(message)
            }

            else -> {
                report1 = reports.get(0)
                defName1 = if(report1.defending == null) "" else tryGetNickName(report1.defending as AGuardian)
                damage1 = report1.damage.toString()

                if(report1.changedStatusEffect)
                    damage1 += ", " + l18nBattle().get("batt_change_area_${report1.newStatusEffect.toString().toLowerCase()}")

                message += l18nBattle().format("batt_area_message_1", defName1, damage1)
            }
        }

        return message
    }

    fun selfDefense(defender: AGuardian): String
    {
        return tryGetNickName(defender) + " " + l18nBattle().get("suff_defense")
    }

    fun substitution(substituted: AGuardian, substitute: AGuardian): String
    {
        return l18nBattle().format(
                "batt_substitution",
                tryGetNickName(substituted),
                tryGetNickName(substitute)
        )
    }

    fun replacingDefeated(substituted: AGuardian, substitute: AGuardian): String
    {
        return l18nBattle().format(
                "batt_replace_defeated",
                tryGetNickName(substituted),
                tryGetNickName(substitute)
        )
    }
}
