package de.limbusdev.guardianmonsters.fwmengine.battle.model;

import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.model.AttackInfo;
import de.limbusdev.guardianmonsters.model.Monster;

/**
 * Contains information about the effect of an attack on another monster
 * Created by georg on 07.09.16.
 */
public class AttackCalculationReport {
    public Monster attacker;
    public Monster defender;
    public Attack attack;

    public int damage;
    public float effectiveness;

    public AttackCalculationReport(Monster att, Monster def, int damage, float effectiveness, Attack attack) {
        this.attacker = att;
        this.defender = def;
        this.damage = damage;
        this.effectiveness = effectiveness;
        this.attack = attack;
    }

    /**
     * Report for defending monster
     * @param defensiveMonster
     */
    public AttackCalculationReport(Monster defensiveMonster) {
        this.attacker = defensiveMonster;
        this.defender = null;
        this.damage = 0;
        this.effectiveness = 0;
        this.attack = AttackInfo.selfDefense;
    }
}
