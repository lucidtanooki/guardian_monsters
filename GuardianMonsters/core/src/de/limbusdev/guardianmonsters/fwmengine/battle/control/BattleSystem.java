package de.limbusdev.guardianmonsters.fwmengine.battle.control;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;
import java.util.Observable;

import de.limbusdev.guardianmonsters.fwmengine.battle.model.AttackCalculationReport;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.MonsterSpeedComparator;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInformation;

/**
 * Created by georg on 21.11.16.
 */

public class BattleSystem extends Observable {
    private Array<Monster> herosTeam;
    private Array<Monster> opponentsTeam;

    private AIPlayer aiPlayer;

    private Array<Monster> currentRound;


    // Status values for GUI


    public BattleSystem(Array<Monster> hero, Array<Monster> opponent) {
        herosTeam = hero;
        opponentsTeam = opponent;
        aiPlayer = new AIPlayer();
        newRound();
    }


    // .............................................................................. battle methods
    public Monster getActiveMonster() {
        return currentRound.get(currentRound.size - 1);
    }

    /**
     * Attacks the given Monster with the currently active monster
     *
     * @param target
     * @param attack
     */
    public void attack(Monster target, int attack) {
        // Calculate Attack
        AttackCalculationReport rep = MonsterManager.calcAttack(
            getActiveMonster(), target, getActiveMonster().attacks.get(attack));

        // Remove active monster
        currentRound.pop();

        checkKO();

        // Sort in case current speed values have changed
        resortCurrentQueue();

        if (currentRound.size == 0) {
            newRound();
        }

        // Check, if next monster is from AI
        if (opponentsTeam.contains(getActiveMonster(), true)) {
            aiPlayer.turn();
        }
    }

    /**
     * Initializes a new round, adding all monsters of a team to the queue and sorting the queue by speed
     */
    private void newRound() {
        currentRound = new Array<Monster>();

        // Get fit monsters from both teams
        for (Monster m : herosTeam) {
            if (m.getHP() > 0) {
                currentRound.add(m);
            }
        }

        for (Monster m : opponentsTeam) {
            if (m.getHP() > 0) {
                currentRound.add(m);
            }
        }

        // Sort monsters by speed
        resortCurrentQueue();

        System.out.println("\n=== NEW ROUND ===" +
            "\n\nQueue: ");
        for (int i = 0; i < currentRound.size; i++) {
            Monster m = currentRound.get(i);
            String name = Services.getL18N().l18n().get(MonsterInformation.getInstance().monsterNames.get(m.ID - 1));
            System.out.print(name + "\t\t(" + m.getSpeed() + "),");
            System.out.println("\tKP: " + m.getHP() + "\tMP: " + m.getMP());
        }
    }

    private void checkKO() {
        Iterator<Monster> it = currentRound.iterator();
        while (it.hasNext()) {
            Monster m = it.next();
            if (m.getHP() == 0) {
                it.remove();
            }
        }
    }

    /**
     * Sorts all left monsters of the round by speed
     */
    private void resortCurrentQueue() {
        currentRound.sort(new MonsterSpeedComparator());
    }

    // INNER CLASS

    private class AIPlayer {

        public AIPlayer() {
            // TODO
        }

        public void turn() {
            System.out.println("\n### AI's turn ###");
            Monster m = getActiveMonster();
            int att = MathUtils.random(0,m.attacks.size-1);
            Array<Monster> targets = new Array<Monster>();
            for(Monster h : herosTeam) {
                if(h.getHP() > 0) {
                    targets.add(h);
                }
            }
            Monster target = targets.get(MathUtils.random(0,targets.size-1));
            attack(target,att);
        }
    }
}
