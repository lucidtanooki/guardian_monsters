package de.limbusdev.guardianmonsters.fwmengine.battle.controller;

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.guardians.monsters.ISpeciesDescriptionService;
import de.limbusdev.guardianmonsters.services.Services;

/**
 * @author Georg Eckert 2017
 */
public class BattleStringBuilder
{
    public static String receivedDamage(Guardian victim, int damage)
    {
        ISpeciesDescriptionService species = GuardiansServiceLocator.getSpecies();
        String text = species.getCommonNameById(victim.getSpeciesID()) + " lost " + damage + " HP";
        return text;
    }

    public static String givenDamage(AGuardian attacker, AGuardian victim, de.limbusdev.guardianmonsters.guardians.battle.AttackCalculationReport report)
    {
        ISpeciesDescriptionService species = GuardiansServiceLocator.getSpecies();
        String attName = species.getCommonNameById(attacker.getSpeciesID());
        String defName = species.getCommonNameById(victim.getSpeciesID());

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

        String message = Services.getL18N().Battle().format(
            "batt_message",
            attacker.getNickname(),
            victim.getNickname(),
            Services.getL18N().getLocalizedAbilityName(report.attack.name),
            report.damage, eff);

        return message;
    }

    public static String selfDefense(AGuardian defender)
    {
        String defName = defender.getNickname();
        String message = defName + " " + Services.getL18N().Battle().get("suff_defense");
        return message;
    }
}
