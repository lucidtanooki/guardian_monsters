package de.limbusdev.guardianmonsters.guardians.monsters;

import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityGraph;

/**
 * Monster is the basic entity for the BattleSystem
 *
 * @author Georg Eckert 2015
 */
public class Guardian extends AGuardian
{
    // Components
    private SpeciesDescription data;
    private IndividualStatistics statistics;
    private IAbilityGraph   abilityGraph;


    // ............................................................................................. CONSTRUCTOR
    /**
     * The protected constructor makes it available from the {@link AGuardianFactory} only.
     * @param ID
     */
    protected Guardian(String UUID, int ID, SpeciesDescription data, IndividualStatistics statistics, IAbilityGraph abilityGraph)
    {
        super(UUID);

        this.data = data;
        this.statistics = statistics;
        this.abilityGraph = abilityGraph;

        setNickname("");
    }

    @Override
    public int getID()
    {
        return getSpeciesData().getID();
    }

    // ............................................................................................. GETTERS & SETTERS
    @Override
    public SpeciesDescription getSpeciesData()
    {
        return data;
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
        return data.getNameID() + " Level: " + statistics.getLevel() + " UUID: " + getUUID();
    }
}
