package de.limbusdev.guardianmonsters.utils;


import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.monsters.Monster;
import de.limbusdev.guardianmonsters.guardians.MonsterDB;

/**
 * Created by georg on 11.01.16.
 */
public class DebugOutput {
    /* ............................................................................ ATTRIBUTES .. */
    
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    public static void printAttack(Monster attacker, int attPos,
                                   Monster defender, int defPos,
                                   Ability ability, int damage) {
        System.out.println("Attacker: "
                + MonsterDB.getInstance().getNameById(attacker.ID) +
                " at Position " + attPos + "\n" +
                "Defender: " +
                MonsterDB.getInstance().getNameById(defender.ID) +
                " at Position " + defPos + "\n" +
                "Ability: " + ability.name + "\n" +
                "Damage: " + damage);
    }

    public static void printRound(Array<Monster> queue) {
        System.out.println("\nQueue: ");
        for (int i = 0; i < queue.size; i++) {
            Monster m = queue.get(i);
            String name = m.getName();
            System.out.print("[" + i + "] " + name + "\t\t(" + m.stat.getSpeed() + "),");
            System.out.println("\tKP: " + m.stat.getHP() + "\tMP: " + m.stat.getMP());
        }
    }

    /* ..................................................................... GETTERS & SETTERS .. */
}
