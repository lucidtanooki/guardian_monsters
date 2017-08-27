package de.limbusdev.guardianmonsters.fwmengine.battle.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.model.MonsterArea;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.GuardianDB;


/**
 * @author Georg Eckert
 */
public class BattleFactory {
    /* ............................................................................ ATTRIBUTES .. */
    private static ArrayMap<Integer,AGuardian> monsters;
    private static BattleFactory instance;
    /* ........................................................................... CONSTRUCTOR .. */

    private BattleFactory() {
        this.monsters = new ArrayMap<>();
        for(int i = 1; i<= 33; i++) {
            createMonster(i);
        }
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
    public AGuardian createMonster(int ID) {
        AGuardian guardian = GuardianDB.getInstance().createGuardian(ID, 1);
        this.monsters.put(ID, guardian);
        return guardian;
    }

    public static BattleFactory getInstance() {
        if(instance == null) instance = new BattleFactory();
        return instance;
    }

    public TeamComponent createOpponentTeam(MonsterArea ma) {
        TeamComponent team = new TeamComponent();
        float oneMonsterProb = 1 - ma.attackProbabilities.get(1) + ma.attackProbabilities.get(2);
        int numMonsters;

        // 1 Monster?
        if(MathUtils.randomBoolean(oneMonsterProb)) numMonsters = 1;
        else {
            // Decide 2 or 3 Monsters
            if(MathUtils.randomBoolean(ma.attackProbabilities.get(1)/(1-oneMonsterProb))) numMonsters = 2;
            else numMonsters = 3;
        }

        for(int j=0; j<numMonsters; j++) {
            team.team.put(j,BattleFactory.getInstance().createMonster(
                decideWichMonster(ma.monsters, ma.monsterProbabilities)));
        }
        return team;
    }

    public int decideWichMonster(Array<Integer> monsters, Array<Float> probs) {
        double p = MathUtils.random();
        double cumulativeProbability = 0.0;
        for (int i=0;i<monsters.size;i++) {
            cumulativeProbability += probs.get(i);
            if (p <= cumulativeProbability) {
                return monsters.get(i);
            }
        }
        return monsters.get(0);
    }
}
