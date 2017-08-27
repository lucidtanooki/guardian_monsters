package de.limbusdev.guardianmonsters.guardians.monsters;

import java.util.Observable;

import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityGraph;

/**
 * AGuardian
 *
 * Abstract guardian class that defines how a guardian
 * looks internally and what it provides.
 *
 * @author Georg Eckert 2017
 */

public abstract class AGuardian extends Observable
{
    // Unique ID for Guardian Identification, must be stored when persisted
    private final String UUID;
    private String nickname;

    protected AGuardian(String UUID)
    {
        this.UUID = UUID;
    }

    // ............................................................................................. GETTERS & SETTERS

    public String getUUID()
    {
        return UUID;
    }
    public String getNickname()
    {
        return nickname;
    }
    public void setNickname(String name)
    {
        this.nickname = name;
    }
    public abstract int getID();


    // ............................................................................................. COMPONENTS

    public abstract SpeciesDescription getSpeciesData();
    public abstract IndividualStatistics getStatistics();
    public abstract IAbilityGraph getAbilityGraph();


    // ............................................................................................. OBSERVABLE

    private Class changeType;

    public void setStatisticsChanged()
    {
        this.changeType = IndividualStatistics.class;
    }

    public void setAbilitiesChanged()
    {
        this.changeType = IAbilityGraph.class;
    }

    public Class getChangeType()
    {
        return changeType;
    }


    // ............................................................................................. OBJECT
    @Override
    public boolean equals(Object other)
    {
        if(!(other instanceof AGuardian))
        {
            return false;
        }

        AGuardian otherGuardian = (AGuardian) other;
        if(otherGuardian.getUUID().equals(UUID))
        {
            return true;
        }

        return false;
    }
}
