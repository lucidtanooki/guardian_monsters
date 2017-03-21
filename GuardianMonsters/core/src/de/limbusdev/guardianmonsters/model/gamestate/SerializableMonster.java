package de.limbusdev.guardianmonsters.model.gamestate;

import de.limbusdev.guardianmonsters.model.abilities.AbilityGraph;
import de.limbusdev.guardianmonsters.model.monsters.Monster;
import de.limbusdev.guardianmonsters.model.monsters.Stat;

/**
 * SerializableMonster
 *
 * @author Georg Eckert 2017
 */

public class SerializableMonster {

    public int ID;
    public String nickname;
    public SerializableAbilityGraph graph;
    public SerializableStat stat;

    @ForSerializationOnly
    public SerializableMonster() {}

    public SerializableMonster(Monster monster) {
        this.ID = monster.ID;
        this.nickname = monster.nickname;
        this.graph = new SerializableAbilityGraph(monster.abilityGraph);
        this.stat = new SerializableStat(monster.stat);
    }

    public static Monster deserialize(SerializableMonster sMonster) {

        AbilityGraph graph = SerializableAbilityGraph.deserialize(sMonster.graph);
        Stat stat = SerializableStat.deserialize(sMonster.stat);

        new SerializableStat();

        return new Monster(sMonster.ID, sMonster.nickname, graph, stat);
    }

}
