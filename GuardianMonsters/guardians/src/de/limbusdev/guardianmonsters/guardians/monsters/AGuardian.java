package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;

import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityGraph;

/**
 * AGuardian
 *
 * Abstract guardian class that defines how a guardian
 * looks internally and what it provides.
 *
 * @author Georg Eckert 2017
 */

public abstract class AGuardian extends Signal<AGuardian> implements Listener<Stat>
{
    private static int INSTANCE_COUNTER = 0;

    private final int INSTANCE_ID;

    protected AGuardian()
    {
        this.INSTANCE_ID = INSTANCE_COUNTER++;
    }

    public abstract SpeciesData getSpeciesData();
    public abstract Stat getStat();
    public abstract IAbilityGraph getAbilityGraph();

    public abstract String getNickname();
    public abstract void setNickname(String name);

    public int getInstanceID()
    {
        return INSTANCE_ID;
    }

    @Override
    public void receive(Signal<Stat> signal, Stat object) {
        dispatch(this);
    }

    @Override
    public boolean equals(Object o)
    {
        if(!(o instanceof AGuardian)) {
            return false;
        }

        AGuardian g = (AGuardian) o;
        if(((AGuardian) o).getInstanceID() == INSTANCE_ID) {
            return true;
        }

        return false;
    }
}
