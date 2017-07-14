package de.limbusdev.guardianmonsters.model.gamestate;

import de.limbusdev.guardianmonsters.guardians.abilities.AbilityGraph;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Stat;

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

    public SerializableMonster(Guardian guardian) {
        this.ID = guardian.ID;
        this.nickname = guardian.nickname;
        this.graph = new SerializableAbilityGraph(guardian.abilityGraph);
        this.stat = new SerializableStat(guardian.stat);
    }

    public static Guardian deserialize(SerializableMonster sMonster) {

        AbilityGraph graph = SerializableAbilityGraph.deserialize(sMonster.graph);
        Stat stat = SerializableStat.deserialize(sMonster.stat);

        new SerializableStat();

        return new Guardian(sMonster.ID, sMonster.nickname, graph, stat);
    }

}
