package de.limbusdev.guardianmonsters.fwmengine.battle.control;


import com.badlogic.gdx.math.MathUtils;

import de.limbusdev.guardianmonsters.enums.AttackType;
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
     * Calculates and applies attacks, the report attributes are for information only
     * @param att
     * @param def
     * @return
     */
    public static AttackCalculationReport calcAttack(Monster att, Monster def, Attack attack) {
        AttackCalculationReport report = new AttackCalculationReport(att, def, 0, 0, attack);
        float effectiveness = ElemEff.singelton().getElemEff(attack.element, def.elements);

        float defenseRatio;

        if(attack.attackType == AttackType.PHYSICAL) {
            defenseRatio = (att.physStrength*1f) / (def.physDef*1f);
        } else {
            defenseRatio = (att.magicStrength*1f) / (def.magicDef*1f);
        }

        float damage = (effectiveness * attack.damage)*defenseRatio;

        /* Calculate Damage */
        if (def.getHP() - damage < 0) {
            def.setHP(0);
        } else {
            def.setHP(def.getHP() - MathUtils.round(damage));
        }

        report.damage = MathUtils.round(damage);
        report.effectiveness = effectiveness;
        System.out.println(attack.name + " causes " + damage + " damage on " + MonsterInformation.getInstance().monsterNames.get(def.ID - 1));

        return report;
    }

    /* ..................................................................... GETTERS & SETTERS .. */
}
