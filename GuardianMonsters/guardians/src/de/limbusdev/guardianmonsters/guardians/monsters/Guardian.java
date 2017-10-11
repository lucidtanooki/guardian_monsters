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
    private SpeciesDescription      speciesDescription;
    private IndividualStatistics    individualStatistics;
    private IAbilityGraph           abilityGraph;


    // ............................................................................................. CONSTRUCTOR

    /**
     * The protected constructor makes it available from the {@link AGuardianFactory} only.
     *
     * After Object creation the missing components have to be injected.
     * @param UUID
     */
    protected Guardian(String UUID)
    {
        super(UUID);
        setNickname("");
    }

    protected void injectSpeciesDescription(SpeciesDescription speciesDescription)
    {
        this.speciesDescription = speciesDescription;
    }

    protected void injectIndiviualStatistics(IndividualStatistics individualStatistics)
    {
        this.individualStatistics = individualStatistics;
    }

    protected void injectAbilityGraph(IAbilityGraph abilityGraph)
    {
        this.abilityGraph = abilityGraph;
    }

    // ............................................................................................. DELEGATED METHODS

    // delegated to SpeciesDescription Component
    @Override
    public int getSpeciesID()
    {
        return speciesDescription.getID();
    }

    @Override
    public CommonStatistics getCommonStatistics()
    {
        return speciesDescription.getCommonStatistics();
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
    public SpeciesDescription getSpeciesDescription()
    {
        return speciesDescription;
    }

    @Override
    public IndividualStatistics getIndividualStatistics()
    {
        return individualStatistics;
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
        String label = "\n+------------------------------------------------------------------------+\n";
        label += "| " + String.format("UUID: %s\n| SID: %d,\t Lvl: %d", getUUID(), getIndividualStatistics().getLevel(), getSpeciesID());
        label += "\n| " + individualStatistics.toString();
        label +=       "\n+------------------------------------------------------------------------+\n";
        return label;
    }
}
