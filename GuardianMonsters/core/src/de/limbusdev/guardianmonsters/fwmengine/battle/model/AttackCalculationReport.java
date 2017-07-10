package de.limbusdev.guardianmonsters.fwmengine.battle.model;

import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AnimationType;
import de.limbusdev.guardianmonsters.media.SFXType;
import de.limbusdev.guardianmonsters.enums.Element;
import de.limbusdev.guardianmonsters.model.abilities.Ability;
import de.limbusdev.guardianmonsters.model.monsters.Monster;

/**
 * Contains information about the effect of an attack on another monster
 * Created by georg on 07.09.16.
 */
public class AttackCalculationReport {
    public Monster attacker;
    public Monster defender;
    public Ability attack;

    public int damage;
    public float effectiveness;

    public AttackCalculationReport(Monster att, Monster def, int damage, float effectiveness, Ability ability) {
        this.attacker = att;
        this.defender = def;
        this.damage = damage;
        this.effectiveness = effectiveness;
        this.attack = ability;
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
        this.attack = new Ability(0, Ability.DamageType.PHYSICAL, Element.NONE, 0, "", SFXType.HIT, 0, AnimationType.NONE);
    }
}
