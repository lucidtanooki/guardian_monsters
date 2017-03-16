package de.limbusdev.guardianmonsters.fwmengine.battle.control;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.model.abilities.DamageType;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.abilities.Ability;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.ElemEff;
import de.limbusdev.guardianmonsters.model.monsters.Monster;
import de.limbusdev.guardianmonsters.model.MonsterDB;

/**
 * Handles events of monsters like level up, earning EXP, changing status and so on
 * Created by georg on 24.01.16.
 */
public class MonsterManager {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */


    /**
     * Call this, when a monster decides not to attack and instead defends itself
     * @param defensiveMonster
     * @return
     */
    public static AttackCalculationReport calcDefense(Monster defensiveMonster) {
        System.out.println("Monster defends itself");
        AttackCalculationReport report = new AttackCalculationReport(defensiveMonster);
        defensiveMonster.stat.increasePDef(5);
        defensiveMonster.stat.increaseMDef(5);

        return report;
    }

    /**
     * Calculates attacks, the report attributes are for information only
     *
     *  Damage = Elemental-Multiplier * (((2*Level)/5 + 2) * Power * (Attack/Defense))/50 + 2)
     *
     * @param att
     * @param def
     * @return
     */
    public static AttackCalculationReport calcAttack(Monster att, Monster def, Ability ability) {
        System.out.println("\n--- new ability ---");
        AttackCalculationReport report = new AttackCalculationReport(att, def, 0, 0, ability);
        float efficiency = ElemEff.singelton().getElemEff(ability.element, def.data.elements);

        float defenseRatio;

        if(ability.damageType == DamageType.PHYSICAL) {
            defenseRatio = (att.stat.getPStr() * 1f) / (def.stat.getPDef() *1f);
        } else {
            defenseRatio = (att.stat.getMStr() *1f) / (def.stat.getMDef() *1f);
        }

        /* Calculate Damage */
        float damage = efficiency * ((((2*att.stat.getLevel()/5 + 2) * ability.damage * defenseRatio) / 3) + 2);

        report.damage = MathUtils.round(damage);
        report.effectiveness = efficiency;

        // Print Battle Debug Message
        String attackerName = Services.getL18N().l18n(BundleAssets.MONSTERS).get(MonsterDB.getInstance().getNameById(att.ID));
        String attackName   = Services.getL18N().l18n(BundleAssets.ATTACKS).get(ability.name);
        String victimName   = Services.getL18N().l18n(BundleAssets.MONSTERS).get(MonsterDB.getInstance().getNameById(def.ID));
        System.out.println(attackerName + ": " + attackName + " causes " + damage + " damage on " + victimName);

        return report;
    }

    /**
     * Applies the previously calculated attack
     * @param report
     */
    public static void apply(AttackCalculationReport report) {
        if(report.defender == null) {
            System.out.println("Only self defending");
            return;
        }
        report.defender.stat.decreaseHP(report.damage);
        report.attacker.stat.decreaseMP(report.attack.MPcost);
    }

    public static boolean tryToRun(ArrayMap<Integer,Monster> escapingTeam, ArrayMap<Integer,Monster> attackingTeam) {
        float meanEscapingTeamLevel = 0;
        float meanAttackingTeamLevel = 0;

        for(Monster m : escapingTeam.values()) {
            if(m.stat.isFit()) {
                meanEscapingTeamLevel += m.stat.getLevel();
            }
        }
        meanEscapingTeamLevel /= escapingTeam.size;

        for(Monster m : attackingTeam.values()) {
            meanAttackingTeamLevel += m.stat.getLevel();
        }
        meanAttackingTeamLevel /= escapingTeam.size;

        if(meanAttackingTeamLevel > meanEscapingTeamLevel) {
            return MathUtils.randomBoolean(.2f);
        } else {
            return MathUtils.randomBoolean(.9f);
        }
    }

    /* ..................................................................... GETTERS & SETTERS .. */
}
