package de.limbusdev.guardianmonsters.guardians.battle;


import com.badlogic.gdx.math.MathUtils;

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.items.ChakraCrystalItem;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics.StatusEffect;
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
        defender.getIndividualStatistics().modifyPDef(5);
        defender.getIndividualStatistics().modifyMDef(5);

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
    public static AttackCalculationReport calcAttack(AGuardian attacker, AGuardian defender, Ability.aID abilityID)
    {
        // Calculate Attack
        System.out.println("\n--- new ability ---");
        Ability ability = GuardiansServiceLocator.INSTANCE.getAbilities().getAbility(abilityID);
        AttackCalculationReport report = new AttackCalculationReport(
            attacker, defender, ability, 0, 0, false, StatusEffect.HEALTHY, false, 0, 0, 0, 0, 0, false, 0, 0);

        // Consider current Status Effect of attacker
        switch (attacker.getIndividualStatistics().getStatusEffect())
        {
            case BLIND:
                if(MathUtils.randomBoolean(0.66f)) {
                    report.statusEffectPreventedAttack = true;
                    return report;
                };
                break;
            case SLEEPING:
                if(MathUtils.randomBoolean(0.66f)) {
                    report.statusEffectPreventedAttack = true;
                    return report;
                } else {
                    attacker.getIndividualStatistics().setStatusEffect(StatusEffect.HEALTHY);
                }
                break;
            case PETRIFIED:
                throw new IllegalStateException("calcAttack() can't be called, when Guardian is petrified.");

            default: /*case HEALTHY || LUNATIC:*/ break;
        }

        // Elemental Efficiency
        float eff = ElemEff.singelton().getElemEff(ability.getElement(), defender.getSpeciesDescription().getElements(0));   // TODO elements currentForm

        IndividualStatistics statAtt = attacker.getIndividualStatistics();
        IndividualStatistics statDef = defender.getIndividualStatistics();

        int typeStrength;
        switch(ability.getDamageType())
        {
            case MAGICAL: typeStrength = statAtt.getMStr(); break;
            default:      typeStrength = statAtt.getPStr(); break;
        }

        int typeDefense;
        switch(ability.getDamageType())
        {
            case MAGICAL: typeDefense = statDef.getMDef(); break;
            default:      typeDefense = statAtt.getPDef(); break;
        }

        int abilityStrength = ability.getDamage();
        float ratioSD = ((float) typeStrength) / ((float) typeDefense);

        int level = statAtt.getLevel();

        /* Calculate Damage */
        float damage;
        if(abilityStrength == 0) {
            damage = 0;
        } else {
            damage = eff * ((0.5f * level + 1) * abilityStrength * ratioSD + 50) / 5f;
        }

        report.damage = MathUtils.ceil(damage);
        report.efficiency = eff;

        // Consider StatusEffect
        if(ability.getCanChangeStatusEffect() && statDef.getStatusEffect().equals(StatusEffect.HEALTHY)) {

            boolean willChange = MathUtils.randomBoolean(ability.getProbabilityToChangeStatusEffect()/100f);
            if(willChange) {
                report.newStatusEffect = ability.getStatusEffect();
                report.changedStatusEffect = true;
            }

        } else {

            // Can't change StatusEffect if it is already changed
            report.changedStatusEffect = false;

        }

        // Handle Stat changing
        if(ability.getChangesStats())
        {
            report.modifiedStat = true;
            report.modifiedPStr = ability.getAddsPStr();
            report.modifiedPDef = ability.getAddsPDef();
            report.modifiedMStr = ability.getAddsMStr();
            report.modifiedMDef = ability.getAddsMDef();
            report.modifiedSpeed = ability.getAddsSpeed();
        }

        // Handle Stat curing
        if(ability.getCuresStats())
        {
            report.healedStat = true;
            report.healedHP = ability.getCuresHP();
            report.healedMP = ability.getCuresMP();
        }

        // Print Battle Debug Message
        String attackerName = attacker.getUuid();
        String attackName   = ability.getName();
        String victimName   = defender.getUuid();
        System.out.println(attackerName + ": " + attackName + " causes " + damage + " damage on " + victimName);

        return report;
    }

    public static void applyStatusEffect(AGuardian guardian)
    {
        AGuardian attacking = guardian;

        switch(attacking.getIndividualStatistics().getStatusEffect())
        {
            case POISONED:
                attacking.getIndividualStatistics().decreaseHP(5);
                break;
            case PETRIFIED:
            case LUNATIC:
            case BLIND:
            default: /*case HEALTHY:*/
                break;
        }
    }

    public static boolean banSucceeds(AGuardian guardianToBeBanned, ChakraCrystalItem crystal)
    {
        return MathUtils.randomBoolean(crystal.chance(guardianToBeBanned));
    }

    /**
     * Applies the previously calculated attack
     * @param report
     */
    public static void apply(AttackCalculationReport report)
    {
        if(report.defending == null) {

            System.out.println("Only self defending");
            return;
        }

        if(report.statusEffectPreventedAttack) {
            System.out.println("Status Effect prevented attack.");
            return;
        }

        AGuardian defending = report.defending;
        AGuardian attacking = report.attacking;

        System.out.println(report.attacking.getUuid() + " attacks " + defending.getUuid() + " with " + report.attack.getName());
        defending.getIndividualStatistics().decreaseHP(report.damage);
        attacking.getIndividualStatistics().decreaseMP(report.attack.getMPcost());

        // Apply Status Effect
        if(report.changedStatusEffect) {

            defending.getIndividualStatistics().setStatusEffect(report.newStatusEffect);
        }

        // Apply Stat Change
        if(report.modifiedStat) {

            defending.getIndividualStatistics().modifyPStr(report.modifiedPStr);
            defending.getIndividualStatistics().modifyPDef(report.modifiedPDef);
            defending.getIndividualStatistics().modifyMStr(report.modifiedMStr);
            defending.getIndividualStatistics().modifyMDef(report.modifiedMDef);
            defending.getIndividualStatistics().modifySpeed(report.modifiedSpeed);
        }

        // Apply Stat Cure
        if(report.healedStat) {

            defending.getIndividualStatistics().healHP(report.healedHP);
            defending.getIndividualStatistics().healMP(report.healedMP);
        }
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
        meanEscapingTeamLevel /= escapingTeam.getSize();

        for(AGuardian m : attackingTeam.values())
        {
            meanAttackingTeamLevel += m.getIndividualStatistics().getLevel();
        }
        meanAttackingTeamLevel /= escapingTeam.getSize();

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
