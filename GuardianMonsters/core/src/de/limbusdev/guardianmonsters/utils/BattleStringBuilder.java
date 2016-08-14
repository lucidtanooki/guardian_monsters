package de.limbusdev.guardianmonsters.utils;


import de.limbusdev.guardianmonsters.model.Monster;
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
    /* ..................................................................... GETTERS & SETTERS .. */
}