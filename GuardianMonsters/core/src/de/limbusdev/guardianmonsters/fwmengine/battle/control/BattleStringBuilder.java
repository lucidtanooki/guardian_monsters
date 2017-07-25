package de.limbusdev.guardianmonsters.fwmengine.battle.control;

import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.guardians.monsters.GuardianDB;
import de.limbusdev.guardianmonsters.services.Services;

/**
 * @author Georg Eckert 2017
 */
public class BattleStringBuilder
{
    public static String receivedDamage(Guardian victim, int damage)
    {
        String text = GuardianDB.getInstance().getNameById(victim.ID) + " lost " + damage + " HP";
        return text;
    }

    public static String givenDamage(Guardian attacker, Guardian victim, AttackCalculationReport report)
    {
        String attName = GuardianDB.getInstance().getNameById(attacker.ID);
        String defName = GuardianDB.getInstance().getNameById(victim.ID);

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
            attacker.getName(),
            victim.getName(),
            Services.getL18N().getLocalizedAbilityName(report.attack.name),
            report.damage, eff);

        return message;
    }

    public static String selfDefense(Guardian defender)
    {
        String defName = defender.getName();
        String message = defName + " " + Services.getL18N().Battle().get("suff_defense");
        return message;
    }
}
