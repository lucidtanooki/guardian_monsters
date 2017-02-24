package de.limbusdev.guardianmonsters.fwmengine.battle.control;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.I18NBundle;

import de.limbusdev.guardianmonsters.enums.AttackType;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.ElemEff;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInfo;

/**
 * Handles events of monsters like level up, earning EXP, changing status and so on
 * Created by georg on 24.01.16.
 */
public class MonsterManager {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */

    /**
     * Earns a monster EXP, evolves it when necessary and learns it new attacks at specific levels
     * @param m
     * @param exp
     * @return
     */
    public static Monster earnEXP(Monster m, int exp) {
        boolean reachedNextLevel = m.receiveEXP(exp);
        if(reachedNextLevel) {
            if (MonsterInfo.getInstance().getStatusInfos().get(m.ID)
                    .learnableAttacks.containsKey(m.level))
                m.attacks.add(MonsterInfo.getInstance().getStatusInfos().get(m.ID)
                        .learnableAttacks.get(m.level));
            if(m.level >= MonsterInfo.getInstance().getStatusInfos().get(m.ID).evolvingAtLevel)
                m.evolution = MonsterInfo.getInstance().getStatusInfos().get(m.ID).evolution;
        }

        return m;
    }

    /**
     * Call this, when a monster decides not to attack and instead defends itself
     * @param defensiveMonster
     * @return
     */
    public static AttackCalculationReport calcDefense(Monster defensiveMonster) {
        System.out.println("Monster defends itself");
        AttackCalculationReport report = new AttackCalculationReport(defensiveMonster);
        defensiveMonster.setpDef(MathUtils.round(defensiveMonster.pDef *1.05f));
        defensiveMonster.setmDef(MathUtils.round(defensiveMonster.mDef *1.05f));

        return report;
    }

    /**
     * Calculates attacks, the report attributes are for information only
     * @param att
     * @param def
     * @return
     */
    public static AttackCalculationReport calcAttack(Monster att, Monster def, Attack attack) {
        System.out.println("\n--- new attack ---");
        AttackCalculationReport report = new AttackCalculationReport(att, def, 0, 0, attack);
        float effectiveness = ElemEff.singelton().getElemEff(attack.element, def.elements);

        float defenseRatio;

        if(attack.attackType == AttackType.PHYSICAL) {
            defenseRatio = (att.pStr *1f) / (def.pDef *1f);
        } else {
            defenseRatio = (att.mStr *1f) / (def.mDef *1f);
        }

        /* Calculate Damage */
        float damage = (effectiveness * attack.damage)*defenseRatio;

        report.damage = MathUtils.round(damage);
        report.effectiveness = effectiveness;

        I18NBundle l18n = Services.getL18N().l18n();
        System.out.println(
            l18n.get(MonsterInfo.getInstance().getNameById(att.ID)) + ": "
             + l18n.get(attack.name)
                + " causes " + damage + " damage on "
                + l18n.get(MonsterInfo.getInstance().getNameById(def.ID)));

        return report;
    }

    /**
     * Applies the previously calculated attack
     * @param rep
     */
    public static void apply(AttackCalculationReport rep) {
        if(rep.defender == null) {
            System.out.println("Only self defending");
            return;
        }
        rep.defender.setHP(rep.defender.getHP() - MathUtils.round(rep.damage));
        rep.attacker.consumeMP(rep.attack.MPcost);
    }

    public static boolean tryToRun(ArrayMap<Integer,Monster> escapingTeam, ArrayMap<Integer,Monster> attackingTeam) {
        float meanEscapingTeamLevel = 0;
        float meanAttackingTeamLevel = 0;

        for(Monster m : escapingTeam.values()) {
            if(m.getHP() > 0) {
                meanEscapingTeamLevel += m.getLevel();
            }
        }
        meanEscapingTeamLevel /= escapingTeam.size;

        for(Monster m : attackingTeam.values()) {
            meanAttackingTeamLevel += m.getLevel();
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
