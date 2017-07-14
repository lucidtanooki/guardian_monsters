package de.limbusdev.guardianmonsters.fwmengine.battle.control;

import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.guardians.monsters.Monster;
import de.limbusdev.guardianmonsters.guardians.MonsterDB;

/**
 * Created by georg on 12.01.16.
 */
public class BattleStringBuilder {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public static String receivedDamage(Monster victim, int damage) {
        String text = MonsterDB.getInstance().getNameById(victim.ID)
            + " lost " + damage + " HP";
        return text;
    }

    public static String givenDamage(Monster attacker, Monster victim, AttackCalculationReport report) {
        String attName = MonsterDB.getInstance().getNameById(attacker.ID);
        String defName = MonsterDB.getInstance().getNameById(victim.ID);

        String eff;
        if(report.effectiveness > 1.1) {
            eff = Services.getL18N().Battle().get("suff_severe");
        } else if (report.effectiveness < .9 && report.effectiveness > 0.1) {
            eff = Services.getL18N().Battle().get("suff_less");
        } else if(report.effectiveness < 0) {
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

    public static String selfDefense(Monster defensiveMonster) {
        String defName = defensiveMonster.getName();
        String message = defName + " " + Services.getL18N().Battle().get("suff_defense");
        return message;
    }

    /* ..................................................................... GETTERS & SETTERS .. */
}
