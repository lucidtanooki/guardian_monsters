package de.limbusdev.guardianmonsters.guardians.monsters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;

/**
 * AGuardianFactory
 *
 * Design Pattern: Factory Method
 *
 * @author Georg Eckert 2017
 */

public abstract class AGuardianFactory implements GuardiansServiceLocator.Service
{
    private static Collection<String> UUIDs;

    protected AGuardianFactory()
    {
        getUUIDs();
    }

    // ............................................................................................. IMPLEMENTED METHODS

    /**
     * Creates a unique identifier for a new Guardian
     * @return
     */
    public final static String createNewUUID()
    {
        boolean unique = false;
        String newUUID = "";
        while(!unique)
        {
            newUUID = UUID.randomUUID().toString();
            if(!getUUIDs().contains(newUUID))
            {
                unique = true;
            }
        }
        getUUIDs().add(newUUID);
        return newUUID;
    }


    public final static Collection<String> getUUIDs()
    {
        if(UUIDs == null) UUIDs = new ArrayList<>();
        return UUIDs;
    }


    // ............................................................................................. ABSTRACT METHODS

    /**
     * FACTORY METHOD
     *
     * Takes full responsibility of creating a guardian.
     *
     * @param ID    ID of the guardian to be created, defines species
     * @param level level if the guardian to be created
     * @return      the complete generated guardian
     */
    public abstract AGuardian createGuardian(int ID, int level);    // P04: Factory Method

    @Override
    public void destroy()
    {
        UUIDs = null;
    }
}
