package de.limbusdev.guardianmonsters.guardians.battle;


import com.badlogic.gdx.math.MathUtils;

import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

/**
 * Handles events of monsters like level up, earning EXP, changing status and so on
 *
 * @author Georg Eckert
 */
public class BattleCalculator
{
    /**
     * Call this, when a monster decides not to attack and instead defends itself
     * @param defender
     * @return
     */
    public static AttackCalculationReport calcDefense(AGuardian defender)
    {
        System.out.println("Monster defends itself");
        AttackCalculationReport report = new AttackCalculationReport(defender);
        defender.getIndividualStatistics().increasePDef(5);
        defender.getIndividualStatistics().increaseMDef(5);

        return report;
    }

    /**
     * Calculates attacks, the report attributes are for information only
     *
     *  Damage = Elemental-Multiplier * ((0.5 * Level + 1) * Ability-Damage * (Strength/Defense) + 50) / 50
     *
     *  Strength ... PStr / MStr, according to Ability Type
     *  Defense  ... PDef / MDef, according to Ability Type
     *
     * @param attacker
     * @param defender
     * @return
     */
    public static AttackCalculationReport calcAttack(AGuardian attacker, AGuardian defender, Ability ability)
    {
        System.out.println("\n--- new ability ---");
        AttackCalculationReport report = new AttackCalculationReport(attacker, defender, 0, 0, ability);

        // Elemental Efficiency
        float eff = ElemEff.singelton().getElemEff(ability.element, defender.getSpeciesDescription().getElements(0));   // TODO elements currentForm

        IndividualStatistics statAtt = attacker.getIndividualStatistics();
        IndividualStatistics statDef = defender.getIndividualStatistics();

        int typeStrength;
        switch(ability.damageType)
        {
            case MAGICAL: typeStrength = statAtt.getMStr(); break;
            default:      typeStrength = statAtt.getPStr(); break;
        }

        int typeDefense;
        switch(ability.damageType)
        {
            case MAGICAL: typeDefense = statDef.getMDef(); break;
            default:      typeDefense = statAtt.getPDef(); break;
        }

        int abilityStrength = ability.damage;

        int level = statAtt.getLevel();

        /* Calculate Damage */
        float damage = (((2f/5f * level + 2f) * abilityStrength * (typeStrength/typeDefense) / 48f) + 2f) * eff;

        report.damage = MathUtils.ceil(damage);
        report.efficiency = eff;

        // Print Battle Debug Message
        String attackerName = attacker.getNickname();
        String attackName   = ability.name;
        String victimName   = defender.getNickname();
        System.out.println(attackerName + ": " + attackName + " causes " + damage + " damage on " + victimName);

        return report;
    }

    /**
     * Applies the previously calculated attack
     * @param report
     */
    public static void apply(AttackCalculationReport report)
    {
        if(report.defender == null) {
            System.out.println("Only self defending");
            return;
        }
        System.out.println(report.attacker.getUUID() + " attacks " + report.defender.getUUID() + " with " + report.attack.name);
        report.defender.getIndividualStatistics().decreaseHP(report.damage);
        report.attacker.getIndividualStatistics().decreaseMP(report.attack.MPcost);
    }

    public static boolean tryToRun(Team escapingTeam, Team attackingTeam)
    {
        float meanEscapingTeamLevel = 0;
        float meanAttackingTeamLevel = 0;

        for(AGuardian m : escapingTeam.values())
        {
            if(m.getIndividualStatistics().isFit()) {
                meanEscapingTeamLevel += m.getIndividualStatistics().getLevel();
            }
        }
        meanEscapingTeamLevel /= escapingTeam.size;

        for(AGuardian m : attackingTeam.values())
        {
            meanAttackingTeamLevel += m.getIndividualStatistics().getLevel();
        }
        meanAttackingTeamLevel /= escapingTeam.size;

        if(meanAttackingTeamLevel > meanEscapingTeamLevel) {
            return MathUtils.randomBoolean(.2f);
        } else {
            return MathUtils.randomBoolean(.9f);
        }
    }

    public static int calculateEarnedEXP(AGuardian victoriousG, AGuardian defeatedG)
    {
        int victoriousLevel = victoriousG.getIndividualStatistics().getLevel();
        int defeatedLevel = defeatedG.getIndividualStatistics().getLevel();

        int EXP = MathUtils.floor(
            200f * (1.5f * defeatedLevel*defeatedLevel) / (6f*victoriousLevel)
        );

        return EXP;
    }
}
