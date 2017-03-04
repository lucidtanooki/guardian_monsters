package de.limbusdev.guardianmonsters.fwmengine.battle.control;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterDB;

/**
 * Created by georg on 12.01.16.
 */
public class BattleStringBuilder {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public static String receivedDamage(Monster victim, int damage) {
        String text = MonsterDB.singleton().getNameById(victim.ID)
            + " lost " + damage + " HP";
        return text;
    }

    public static String givenDamage(Monster attacker, Monster victim, AttackCalculationReport report) {
        String attName = MonsterDB.singleton().getNameById(attacker.ID);
        String defName = MonsterDB.singleton().getNameById(victim.ID);

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
        String defName = Services.getL18N().l18n(BundleAssets.MONSTERS).get(MonsterDB.singleton().getNameById(defensiveMonster.ID));
        String message = defName + " " + Services.getL18N().l18n(BundleAssets.BATTLE).get("suff_defense");
        return message;
    }

    /* ..................................................................... GETTERS & SETTERS .. */
}
