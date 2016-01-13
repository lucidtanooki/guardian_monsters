package org.limbusdev.monsterworld.utils;

import org.limbusdev.monsterworld.model.Attack;
import org.limbusdev.monsterworld.model.Monster;
import org.limbusdev.monsterworld.model.MonsterInformation;

/**
 * Created by georg on 11.01.16.
 */
public class DebugOutput {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public static void printAttack(Monster attacker, int attPos,
                                   Monster defender, int defPos,
                                   Attack attack, int damage) {
        System.out.println("Attacker: "
                + MonsterInformation.getInstance().monsterNames.get(attacker.ID) +
                " at Position " + attPos + "\n" +
                "Defender: " +
                MonsterInformation.getInstance().monsterNames.get(defender.ID) +
                " at Position " + defPos + "\n" +
                "Attack: " + attack.name + "\n" +
                "Damage: " + damage);
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}