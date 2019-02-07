package de.limbusdev.guardianmonsters.guardians.battle;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardianFactory;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;


/**
 * @author Georg Eckert
 */
public class BattleFactory
{
    /* ............................................................................ ATTRIBUTES .. */
    private static BattleFactory instance;

    /* ........................................................................... CONSTRUCTOR .. */

    private BattleFactory() {}
    /* ............................................................................... METHODS .. */

    public static BattleFactory getInstance() {
        if(instance == null) instance = new BattleFactory();
        return instance;
    }

    public Team createOpponentTeam(ArrayMap<Integer, Float> availableGuardianProbabilities, Array<Float> teamSizeProbabilities, int minLevel, int maxLevel)
    {
        float oneMonsterProb = 1 - teamSizeProbabilities.get(1) + teamSizeProbabilities.get(2);
        int numMonsters;

        // 1 Monster?
        if(MathUtils.randomBoolean(oneMonsterProb)) numMonsters = 1;
        else {
            // Decide 2 or 3 Monsters
            if(MathUtils.randomBoolean(teamSizeProbabilities.get(1)/(1-oneMonsterProb))) numMonsters = 2;
            else numMonsters = 3;
        }

        Team team = new Team(availableGuardianProbabilities.size, numMonsters, numMonsters);

        AGuardianFactory factory = GuardiansServiceLocator.INSTANCE.getGuardianFactory();

        for(int j=0; j<numMonsters; j++)
        {
            int level = MathUtils.random(minLevel, maxLevel);
            team.plus(factory.createGuardian(decideWichMonster(availableGuardianProbabilities), level));
        }
        return team;
    }

    public int decideWichMonster(ArrayMap<Integer, Float> availableGuardianProbabilities)
    {
        double p = MathUtils.random();
        double cumulativeProbability = 0.0;
        for(Integer key : availableGuardianProbabilities.keys()) {
            cumulativeProbability += availableGuardianProbabilities.get(key);
            if(p <= cumulativeProbability) {
                return key;
            }
        }
        return availableGuardianProbabilities.firstKey();
    }
}
