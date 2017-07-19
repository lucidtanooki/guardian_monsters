package de.limbusdev.guardianmonsters.fwmengine.battle.control;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.ElemEff;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.services.Services;

/**
 * Handles events of monsters like level up, earning EXP, changing status and so on
 *
 * @author Georg Eckert
 */
public class MonsterManager
{
    /**
     * Call this, when a monster decides not to attack and instead defends itself
     * @param defender
     * @return
     */
    public static AttackCalculationReport calcDefense(Guardian defender) {
        System.out.println("Monster defends itself");
        AttackCalculationReport report = new AttackCalculationReport(defender);
        defender.stat.increasePDef(5);
        defender.stat.increaseMDef(5);

        return report;
    }

    /**
     * Calculates attacks, the report attributes are for information only
     *
     *  Damage = Elemental-Multiplier * (((2*Level)/5 + 2) * Power * (Attack/Defense))/50 + 2)
     *
     * @param attacker
     * @param defender
     * @return
     */
    public static AttackCalculationReport calcAttack(Guardian attacker, Guardian defender, Ability ability)
    {
        System.out.println("\n--- new ability ---");
        AttackCalculationReport report = new AttackCalculationReport(attacker, defender, 0, 0, ability);
        float efficiency = ElemEff.singelton().getElemEff(ability.element, defender.data.getElements());

        float defenseRatio;

        if(ability.damageType == Ability.DamageType.PHYSICAL) {
            defenseRatio = (attacker.stat.getPStr() * 1f) / (defender.stat.getPDef() *1f);
        } else {
            defenseRatio = (attacker.stat.getMStr() *1f) / (defender.stat.getMDef() *1f);
        }

        /* Calculate Damage */
        float damage = efficiency * ((((2*attacker.stat.getLevel()/5 + 2) * ability.damage * defenseRatio) / 3) + 2);

        report.damage = MathUtils.round(damage);
        report.efficiency = efficiency;

        // Print Battle Debug Message
        String attackerName = attacker.getName();
        String attackName   = Services.getL18N().getLocalizedAbilityName(ability.name);
        String victimName   = defender.getName();
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

    public static boolean tryToRun(ArrayMap<Integer,Guardian> escapingTeam, ArrayMap<Integer,Guardian> attackingTeam)
    {
        float meanEscapingTeamLevel = 0;
        float meanAttackingTeamLevel = 0;

        for(Guardian m : escapingTeam.values())
        {
            if(m.stat.isFit()) {
                meanEscapingTeamLevel += m.stat.getLevel();
            }
        }
        meanEscapingTeamLevel /= escapingTeam.size;

        for(Guardian m : attackingTeam.values())
        {
            meanAttackingTeamLevel += m.stat.getLevel();
        }
        meanAttackingTeamLevel /= escapingTeam.size;

        if(meanAttackingTeamLevel > meanEscapingTeamLevel) {
            return MathUtils.randomBoolean(.2f);
        } else {
            return MathUtils.randomBoolean(.9f);
        }
    }
}
