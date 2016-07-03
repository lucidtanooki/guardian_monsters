package org.limbusdev.monsterworld.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.ecs.components.TeamComponent;
import org.limbusdev.monsterworld.utils.GlobPref;

/**
 * Created by georg on 12.12.15.
 */
public class BattleFactory {
    /* ............................................................................ ATTRIBUTES .. */
    private static Array<Monster> monsters;
    private static BattleFactory instance;
    /* ........................................................................... CONSTRUCTOR .. */

    private BattleFactory() {
        this.monsters = new Array<Monster>();
        for(int i = 0; i< GlobPref.MONSTER_SPRITES; i++) {
            Monster mon = new Monster();
            mon.ID = i+1;
            monsters.add(new Monster());
        }
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
    public Monster createMonster(int ID) {
        Monster monster = new Monster();
        monster.ID = ID;
        this.monsters.add(monster);
        return monster;
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

        for(int j=0; j<numMonsters; j++) team.monsters.add(BattleFactory.getInstance().createMonster(
                decideWichMonster(ma.monsters, ma.monsterProbabilities)));
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
