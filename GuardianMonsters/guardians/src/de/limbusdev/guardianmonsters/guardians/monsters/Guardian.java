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
    private SpeciesData   data;
    private Stat          stat;
    private IAbilityGraph abilityGraph;

    private String nickname;

    // ............................................................................................. CONSTRUCTOR
    /**
     * The protected constructor makes it available from the {@link AGuardianFactory} only.
     * @param ID
     */
    protected Guardian(int ID, SpeciesData data, Stat stat, IAbilityGraph abilityGraph)
    {
        super();

        this.data = data;
        this.stat = stat;
        this.abilityGraph = abilityGraph;

        this.nickname = "";
    }

    // ............................................................................................. GETTERS & SETTERS
    @Override
    public SpeciesData getSpeciesData()
    {
        return data;
    }

    @Override
    public Stat getStat()
    {
        return stat;
    }

    @Override
    public IAbilityGraph getAbilityGraph()
    {
        return abilityGraph;
    }

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

    // ............................................................................................. OBJECT
    @Override
    public String toString()
    {
        String out = "";
        out += data.getNameID() + " Level: " + stat.getLevel() + " Instance: " + getInstanceID();
        return out;
    }
}
