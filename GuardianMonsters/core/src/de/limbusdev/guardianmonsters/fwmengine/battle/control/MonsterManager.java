package de.limbusdev.guardianmonsters.fwmengine.battle.control;


import com.badlogic.gdx.math.MathUtils;

import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.ElemEff;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;
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
    public static AttackCalculationReport calcDefense(AGuardian defender) {
        System.out.println("Monster defends itself");
        AttackCalculationReport report = new AttackCalculationReport(defender);
        defender.getStatistics().increasePDef(5);
        defender.getStatistics().increaseMDef(5);

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
    public static AttackCalculationReport calcAttack(AGuardian attacker, AGuardian defender, Ability ability)
    {
        System.out.println("\n--- new ability ---");
        AttackCalculationReport report = new AttackCalculationReport(attacker, defender, 0, 0, ability);
        float efficiency = ElemEff.singelton().getElemEff(ability.element, defender.getSpeciesData().getElements());

        float defenseRatio;

        if(ability.damageType == Ability.DamageType.PHYSICAL) {
            defenseRatio = (attacker.getStatistics().getPStr() * 1f) / (defender.getStatistics().getPDef() *1f);
        } else {
            defenseRatio = (attacker.getStatistics().getMStr() *1f) / (defender.getStatistics().getMDef() *1f);
        }

        /* Calculate Damage */
        float damage = efficiency * ((((2*attacker.getStatistics().getLevel()/5 + 2) * ability.damage * defenseRatio) / 3) + 2);

        report.damage = MathUtils.round(damage);
        report.efficiency = efficiency;

        // Print Battle Debug Message
        String attackerName = attacker.getNickname();
        String attackName   = Services.getL18N().getLocalizedAbilityName(ability.name);
        String victimName   = defender.getNickname();
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
        report.defender.getStatistics().decreaseHP(report.damage);
        report.attacker.getStatistics().decreaseMP(report.attack.MPcost);
    }

    public static boolean tryToRun(Team escapingTeam, Team attackingTeam)
    {
        float meanEscapingTeamLevel = 0;
        float meanAttackingTeamLevel = 0;

        for(AGuardian m : escapingTeam.values())
        {
            if(m.getStatistics().isFit()) {
                meanEscapingTeamLevel += m.getStatistics().getLevel();
            }
        }
        meanEscapingTeamLevel /= escapingTeam.size;

        for(AGuardian m : attackingTeam.values())
        {
            meanAttackingTeamLevel += m.getStatistics().getLevel();
        }
        meanAttackingTeamLevel /= escapingTeam.size;

        if(meanAttackingTeamLevel > meanEscapingTeamLevel) {
            return MathUtils.randomBoolean(.2f);
        } else {
            return MathUtils.randomBoolean(.9f);
        }
    }
}
