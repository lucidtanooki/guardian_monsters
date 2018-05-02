package de.limbusdev.guardianmonsters.battle.utils;

import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.battle.AttackCalculationReport;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.guardians.monsters.ISpeciesDescriptionService;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;
import de.limbusdev.guardianmonsters.services.Services;

/**
 * @author Georg Eckert 2017
 */
public class BattleStringBuilder
{
    public static String receivedDamage(Guardian victim, int damage)
    {
        ISpeciesDescriptionService species = GuardiansServiceLocator.getSpecies();
        String text = species.getCommonNameById(victim.getSpeciesID(),0) + " lost " + damage + " HP"; // TODO currentForm
        return text;
    }

    public static String givenDamage(AGuardian attacker, AGuardian victim, AttackCalculationReport report)
    {
        ISpeciesDescriptionService species = GuardiansServiceLocator.getSpecies();
        String attName = species.getCommonNameById(attacker.getSpeciesID(),0);
        String defName = species.getCommonNameById(victim.getSpeciesID(),0); // TODO currentForm

        String eff;
        if(report.efficiency > 1.1) {
            eff = Services.getL18N().Battle().get("suff_severe");
        } else if (report.efficiency < .9 && report.efficiency > 0.1) {
            eff = Services.getL18N().Battle().get("suff_less");
        } else if(report.efficiency < 0) {
            eff = Services.getL18N().Battle().get("suff_healed");
        } else {
            eff = Services.getL18N().Battle().get("suff_normal");
        }

        String message;

        if(attacker.getIndividualStatistics().getStatusEffect() == IndividualStatistics.StatusEffect.LUNATIC) {
            message = Services.getL18N().Battle().format(
                "batt_message_lunatic",
                Services.getL18N().getGuardianNicknameIfAvailable(attacker),
                Services.getL18N().getGuardianNicknameIfAvailable(victim),
                Services.getL18N().getLocalizedAbilityName(report.attack.name),
                report.damage, eff,
                Services.getL18N().Battle().get("batt_lunatic"));
        } else {
            message = Services.getL18N().Battle().format(
                "batt_message",
                Services.getL18N().getGuardianNicknameIfAvailable(attacker),
                Services.getL18N().getGuardianNicknameIfAvailable(victim),
                Services.getL18N().getLocalizedAbilityName(report.attack.name),
                report.damage, eff);
        }

        if(report.changedStatusEffect) {
            message += (" " +  Services.getL18N().Battle().format("batt_message_status_effect_change",
                Services.getL18N().getGuardianNicknameIfAvailable(victim),
                Services.getL18N().Battle().get("batt_change_" + report.newStatusEffect.toString().toLowerCase())));
        }

        if(report.statusEffectPreventedAttack) {
            message = Services.getL18N().Battle().format(
                "batt_message_failed",
                Services.getL18N().Battle().get("batt_" + attacker.getIndividualStatistics().getStatusEffect().toString().toLowerCase()),
                Services.getL18N().getGuardianNicknameIfAvailable(attacker)
            );
        }

        return message;
    }

    /**
     * For area attacks
     * @param attacker
     * @param reports
     * @return
     */
    public static String givenDamage(AGuardian attacker, Array<AttackCalculationReport> reports)
    {
        ISpeciesDescriptionService species = GuardiansServiceLocator.getSpecies();
        String attName = species.getCommonNameById(attacker.getSpeciesID(),0);

        String message;
        if(attacker.getIndividualStatistics().getStatusEffect() == IndividualStatistics.StatusEffect.LUNATIC) {
            message = Services.getL18N().Battle().format(
                "batt_area_message_lunatic",
                Services.getL18N().getGuardianNicknameIfAvailable(attacker),
                Services.getL18N().getLocalizedAbilityName(reports.first().attack.name),
                Services.getL18N().Battle().get("batt_lunatic")
            );
        } else {
            message = Services.getL18N().Battle().format(
                "batt_area_message",
                Services.getL18N().getGuardianNicknameIfAvailable(attacker),
                Services.getL18N().getLocalizedAbilityName(reports.first().attack.name)
            );
        }
        message += " ";

        String defName1, defName2, defName3;
        String damage1, damage2, damage3;
        AttackCalculationReport report1, report2, report3;

        switch(reports.size)
        {
            case 2:
                report1 = reports.get(0);
                report2 = reports.get(1);
                defName1 = Services.getL18N().getGuardianNicknameIfAvailable(report1.defending);
                defName2 = Services.getL18N().getGuardianNicknameIfAvailable(report2.defending);
                damage1 = Integer.toString(report1.damage);
                damage2 = Integer.toString(report2.damage);

                if(report1.changedStatusEffect) {
                    damage1 += (", " + Services.getL18N().Battle().get(
                        "batt_change_area_" + report1.newStatusEffect.toString().toLowerCase()));
                }

                if(report2.changedStatusEffect) {
                    damage2 += (", " + Services.getL18N().Battle().get(
                        "batt_change_area_" + report2.newStatusEffect.toString().toLowerCase()));
                }

                message += Services.getL18N().Battle().format(
                    "batt_area_message_2", defName1, damage1, defName2, damage2);
                break;

            case 3:
                report1 = reports.get(0);
                report2 = reports.get(1);
                report3 = reports.get(2);
                defName1 = Services.getL18N().getGuardianNicknameIfAvailable(report1.defending);
                defName2 = Services.getL18N().getGuardianNicknameIfAvailable(report2.defending);
                defName3 = Services.getL18N().getGuardianNicknameIfAvailable(report3.defending);
                damage1 = Integer.toString(report1.damage);
                damage2 = Integer.toString(report2.damage);
                damage3 = Integer.toString(report3.damage);

                if(report1.changedStatusEffect) {
                    damage1 += (", " + Services.getL18N().Battle().get(
                        "batt_change_area_" + report1.newStatusEffect.toString().toLowerCase()));
                }

                if(report2.changedStatusEffect) {
                    damage2 += (", " + Services.getL18N().Battle().get(
                        "batt_change_area_" + report2.newStatusEffect.toString().toLowerCase()));
                }

                if(report3.changedStatusEffect) {
                    damage3 += (", " + Services.getL18N().Battle().get(
                        "batt_change_area_" + report3.newStatusEffect.toString().toLowerCase()));
                }

                message += Services.getL18N().Battle().format(
                    "batt_area_message_3", defName1, damage1, defName2, damage2, defName3, damage3);
                System.out.println(message);
                break;

            default: // case 0:
                report1 = reports.get(0);
                defName1 = Services.getL18N().getGuardianNicknameIfAvailable(report1.defending);
                damage1 = Integer.toString(report1.damage);

                if(report1.changedStatusEffect) {
                    damage1 += (", " + Services.getL18N().Battle().get(
                        "batt_change_area_" + report1.newStatusEffect.toString().toLowerCase()));
                }

                message += Services.getL18N().Battle().format(
                    "batt_area_message_0", defName1, damage1);
                break;
        }

        return message;
    }

    public static String selfDefense(AGuardian defender)
    {
        String defName = Services.getL18N().getGuardianNicknameIfAvailable(defender);
        String message = defName + " " + Services.getL18N().Battle().get("suff_defense");
        return message;
    }

    public static String substitution(AGuardian substituted, AGuardian substitute)
    {
        String sub1Name = Services.getL18N().getGuardianNicknameIfAvailable(substituted);
        String sub2Name = Services.getL18N().getGuardianNicknameIfAvailable(substitute);
        String message = Services.getL18N().Battle().format(
            "batt_substitution",
            sub1Name,
            sub2Name
        );
        return message;
    }
}
