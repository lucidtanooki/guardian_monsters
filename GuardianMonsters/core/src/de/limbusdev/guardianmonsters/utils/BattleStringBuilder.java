package de.limbusdev.guardianmonsters.utils;


import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInBattle;
import de.limbusdev.guardianmonsters.model.MonsterInformation;

/**
 * Created by georg on 12.01.16.
 */
public class BattleStringBuilder {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public static String receivedDamage(Monster victim, int damage) {
        String text = MonsterInformation.getInstance().monsterNames.get(victim.ID)
            + " lost " + damage + " HP";
        return text;
    }

    public static String givenDamage(Monster attacker, Monster victim, int damage) {
        String text = MonsterInformation.getInstance().monsterNames.get(attacker.ID)
                + " caused " + damage + " HP damage on "
                + MonsterInformation.getInstance().monsterNames.get(victim.ID);

        return text;
    }

    public static void printEnqueuedMonster(MonsterInBattle m, int chosenTarget, int attack) {
        System.out.println("\n----- Monster Indicator Widget -----");
        System.out.println("Team: " + (m.battleFieldSide ? "Hero" : "Opponent"));
        System.out.println("Chosen Member: " + m.battleFieldPosition);
        System.out.println("Chosen Opponent: " + chosenTarget);
        System.out.println("----- lineUpForAttack()        -----");
        System.out.println("Position: " + m.battleFieldPosition);
        System.out.println("Attack: " + m.monster.attacks.get(attack).name + " | Target: " + chosenTarget
            + " | Attack chosen: " + m.attackChosen);
        System.out.println();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
