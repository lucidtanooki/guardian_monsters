package de.limbusdev.guardianmonsters.guardians.monsters;

import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityGraph;

/**
 * Monster is the basic entity for the BattleSystem
 *
 * @author Georg Eckert 2015
 */
public class Guardian extends AGuardian
{
    private String nickname;

    // Components
    private SpeciesDescription      description;
    private IndividualStatistics    statistics;
    private IAbilityGraph           abilityGraph;


    // ............................................................................................. CONSTRUCTOR

    /**
     * The protected constructor makes it available from the {@link AGuardianFactory} only.
     * @param ID
     */
    protected Guardian(String UUID, int ID, SpeciesDescription desc, IndividualStatistics stat, IAbilityGraph graph)
    {
        super(UUID);

        this.description =  desc;
        this.statistics =   stat;
        this.abilityGraph = graph;

        setNickname("");
    }

    @Override
    public int getID()
    {
        return getSpeciesData().getID();
    }

    // ............................................................................................. GETTERS & SETTERS

    @Override
    public String getNickname()
    {
        return nickname;
    }

    @Override
    public void setNickname(String name)
    {
        this.nickname = name;
    }

    @Override
    public SpeciesDescription getSpeciesData()
    {
        return description;
    }

    public IndividualStatistics getStatistics()
    {
        return statistics;
    }

    @Override
    public IAbilityGraph getAbilityGraph()
    {
        return abilityGraph;
    }

    // ............................................................................................. OBJECT
    @Override
    public String toString()
    {
        return description.getNameID() + " Level: " + statistics.getLevel() + " UUID: " + getUUID();
    }
}
