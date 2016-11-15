package de.limbusdev.guardianmonsters.fwmengine.battle.control;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.MonsterInBattle;
import de.limbusdev.guardianmonsters.model.MonsterInformation;

/**
 * Created by georg on 12.01.16.
 */
public class BattleStringBuilder {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public static String receivedDamage(Monster victim, int damage) {
        String text = MonsterInformation.getInstance().monsterNames.get(victim.ID)
            + " lost " + damage + " HP";
        return text;
    }

    public static String givenDamage(Monster attacker, Monster victim, AttackCalculationReport report) {
        String attName = MonsterInformation.getInstance().monsterNames.get(attacker.ID-1);
        String defName = MonsterInformation.getInstance().monsterNames.get(victim.ID-1);

        String eff;
        if(report.effectiveness > 1.1) {
            eff = Services.getL18N().l18n().get("suff_severe");
        } else if (report.effectiveness < .9 && report.effectiveness > 0.1) {
            eff = Services.getL18N().l18n().get("suff_less");
        } else if(report.effectiveness < 0) {
            eff = Services.getL18N().l18n().get("suff_healed");
        } else {
            eff = Services.getL18N().l18n().get("suff_normal");
        }

        String message = Services.getL18N().l18n().format(
            "batt_message",
            Services.getL18N().l18n().get(attName),
            Services.getL18N().l18n().get(defName),
            Services.getL18N().l18n().get(report.attack.name),
            report.damage, eff);

        return message;
    }

    public static void printEnqueuedMonster(MonsterInBattle m, int chosenTarget, int attack) {
        System.out.println("\n----- Monster Indicator Widget -----");
        System.out.println("Team: " + (m.battleFieldSide ? "Hero" : "Opponent"));
        System.out.println("Chosen Member: " + m.battleFieldPosition);
        System.out.println("Chosen Opponent: " + chosenTarget);
        System.out.println("----- lineUpForAttack()        -----");
        System.out.println("Position: " + m.battleFieldPosition);
        System.out.println("Attack: " + m.monster.attacks.get(attack).name + " | Target: " + chosenTarget
            + " | Attack chosen: " + m.attackChosen);
        System.out.println();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
