package de.limbusdev.guardianmonsters.utils;


import java.text.MessageFormat;

import de.limbusdev.guardianmonsters.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInBattle;
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
            eff = "suffered severe damage:";
        } else if (report.effectiveness < .9 && report.effectiveness > 0.1) {
            eff = "suffered lower damage:";
        } else if(report.effectiveness < 0) {
            eff = "got healed:";
        } else {
            eff = "suffered";
        }

        String message = MessageFormat.format("{0} attacks {1} with {2}.\n{1} {4} {3}.",
            attName, defName, report.attack.name, report.damage, eff);

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
