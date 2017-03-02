package de.limbusdev.guardianmonsters.fwmengine.battle.control;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.MonsterInBattle;
import de.limbusdev.guardianmonsters.model.MonsterInfo;

/**
 * Created by georg on 12.01.16.
 */
public class BattleStringBuilder {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public static String receivedDamage(Monster victim, int damage) {
        String text = MonsterInfo.getInstance().getNameById(victim.ID)
            + " lost " + damage + " HP";
        return text;
    }

    public static String givenDamage(Monster attacker, Monster victim, AttackCalculationReport report) {
        String attName = MonsterInfo.getInstance().getNameById(attacker.ID);
        String defName = MonsterInfo.getInstance().getNameById(victim.ID);

        String eff;
        if(report.effectiveness > 1.1) {
            eff = Services.getL18N().l18n(BundleAssets.BATTLE).get("suff_severe");
        } else if (report.effectiveness < .9 && report.effectiveness > 0.1) {
            eff = Services.getL18N().l18n(BundleAssets.BATTLE).get("suff_less");
        } else if(report.effectiveness < 0) {
            eff = Services.getL18N().l18n(BundleAssets.BATTLE).get("suff_healed");
        } else {
            eff = Services.getL18N().l18n(BundleAssets.BATTLE).get("suff_normal");
        }

        String message = Services.getL18N().l18n(BundleAssets.BATTLE).format(
            "batt_message",
            Services.getL18N().l18n(BundleAssets.MONSTERS).get(attName),
            Services.getL18N().l18n(BundleAssets.MONSTERS).get(defName),
            Services.getL18N().l18n(BundleAssets.ATTACKS).get(report.attack.name),
            report.damage, eff);

        return message;
    }

    public static String selfDefense(Monster defensiveMonster) {
        String defName = Services.getL18N().l18n(BundleAssets.MONSTERS).get(MonsterInfo.getInstance().getNameById(defensiveMonster.ID));
        String message = defName + " " + Services.getL18N().l18n(BundleAssets.BATTLE).get("suff_defense");
        return message;
    }

    public static void printEnqueuedMonster(MonsterInBattle m, int chosenTarget, int attack) {
        System.out.println("\n----- Monster Indicator Widget -----");
        System.out.println("Team: " + (m.battleFieldSide ? "Hero" : "Opponent"));
        System.out.println("Chosen Member: " + m.battleFieldPosition);
        System.out.println("Chosen Opponent: " + chosenTarget);
        System.out.println("----- lineUpForAttack()        -----");
        System.out.println("Position: " + m.battleFieldPosition);
        System.out.println("Ability: " + m.monster.attacks.get(attack).name + " | Target: " + chosenTarget
            + " | Ability chosen: " + m.attackChosen);
        System.out.println();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
