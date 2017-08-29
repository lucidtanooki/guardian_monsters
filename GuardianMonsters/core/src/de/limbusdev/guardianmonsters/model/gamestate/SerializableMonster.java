package de.limbusdev.guardianmonsters.model.gamestate;

import de.limbusdev.guardianmonsters.guardians.abilities.AbilityGraph;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;

/**
 * SerializableMonster
 *
 * @author Georg Eckert 2017
 */

public class SerializableMonster
{

    public int ID;
    public String nickname;
    public SerializableAbilityGraph graph;
    public SerializableStat stat;

    @ForSerializationOnly
    public SerializableMonster() {}

    public SerializableMonster(AGuardian guardian) {
        this.ID = guardian.getSpeciesDescription().getID();
        this.nickname = guardian.getNickname();
        this.graph = new SerializableAbilityGraph(guardian.getAbilityGraph());
        this.stat = new SerializableStat(guardian.getStatistics());
    }

    public static Guardian deserialize(SerializableMonster sMonster) {

        AbilityGraph graph = SerializableAbilityGraph.deserialize(sMonster.graph);
        IndividualStatistics statistics = SerializableStat.deserialize(sMonster.stat);

        new SerializableStat();

        return null; //new Guardian("", sMonster.ID, sMonster.nickname, statistics, graph);
    }

}
