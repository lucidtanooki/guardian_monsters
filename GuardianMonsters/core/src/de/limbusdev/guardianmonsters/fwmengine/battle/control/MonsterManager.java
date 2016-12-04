package de.limbusdev.guardianmonsters.fwmengine.battle.control;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.I18NBundle;

import de.limbusdev.guardianmonsters.enums.AttackType;
import de.limbusdev.guardianmonsters.fwmengine.managers.L18N;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.ElemEff;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInformation;

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
            if (MonsterInformation.getInstance().statusInfos.get(m.ID)
                    .learnableAttacks.containsKey(m.level))
                m.attacks.add(MonsterInformation.getInstance().statusInfos.get(m.ID)
                        .learnableAttacks.get(m.level));
            if(m.level >= MonsterInformation.getInstance().statusInfos.get(m.ID).evolvingAtLevel)
                m.evolution = MonsterInformation.getInstance().statusInfos.get(m.ID).evolution;
        }

        return m;
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
            defenseRatio = (att.physStrength*1f) / (def.physDef*1f);
        } else {
            defenseRatio = (att.magicStrength*1f) / (def.magicDef*1f);
        }

        /* Calculate Damage */
        float damage = (effectiveness * attack.damage)*defenseRatio;

        report.damage = MathUtils.round(damage);
        report.effectiveness = effectiveness;

        I18NBundle l18n = Services.getL18N().l18n();
        System.out.println(
            l18n.get(MonsterInformation.getInstance().monsterNames.get(att.ID - 1)) + ": "
             + l18n.get(attack.name)
                + " causes " + damage + " damage on "
                + l18n.get(MonsterInformation.getInstance().monsterNames.get(def.ID - 1)));

        return report;
    }

    /**
     * Applies the previously calculated attack
     * @param rep
     */
    public static void apply(AttackCalculationReport rep) {
        if (rep.defender.getHP() - rep.damage < 0) {
            rep.defender.setHP(0);
        } else {
            rep.defender.setHP(rep.defender.getHP() - MathUtils.round(rep.damage));
        }
    }

    public static boolean tryToRun(Array<Monster> escapingTeam, Array<Monster> attackingTeam) {
        float meanEscapingTeamLevel = 0;
        float meanAttackingTeamLevel = 0;

        for(Monster m : escapingTeam) {
            if(m.getHP() > 0) {
                meanEscapingTeamLevel += m.getLevel();
            }
        }
        meanEscapingTeamLevel /= escapingTeam.size;

        for(Monster m : attackingTeam) {
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
