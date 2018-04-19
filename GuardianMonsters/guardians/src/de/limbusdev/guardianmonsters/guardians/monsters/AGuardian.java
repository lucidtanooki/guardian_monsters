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

    protected AGuardian(String UUID)
    {
        this.UUID = UUID;
    }

    // ............................................................................................. GETTERS & SETTERS

    public String getUUID()
    {
        return UUID;
    }
    public abstract String getNickname();
    public abstract void setNickname(String name);

    // ............................................................................................. COMPONENTS

    public abstract SpeciesDescription getSpeciesDescription();
    public abstract IndividualStatistics getIndividualStatistics();
    public abstract IAbilityGraph getAbilityGraph();


    // ............................................................................................. OBSERVABLE

    private Class changeType;

    public void setStatisticsChanged()
    {
        this.setChanged();
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



    // ............................................................................................. DELEGATIONS

    // ............................................................. delegations: SpeciesDescription

    public abstract int getSpeciesID();
    public abstract CommonStatistics getCommonStatistics();

    // ........................................................... delegations: IndividualStatistics

    // ................................................................... delegations: AbilityGraph
}
