package de.limbusdev.guardianmonsters.model;

/**
 * Contains information about the effect of an attack on another monster
 * Created by georg on 07.09.16.
 */
public class AttackCalculationReport {
    public Monster attacker;
    public Monster defender;

    public int damage;

    public AttackCalculationReport(Monster att, Monster def, int damage) {
        this.attacker = att;
        this.defender = def;
        this.damage = damage;
    }
}
