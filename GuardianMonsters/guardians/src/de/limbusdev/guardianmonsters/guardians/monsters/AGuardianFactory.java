package de.limbusdev.guardianmonsters.guardians.monsters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

/**
 * AGuardianFactory
 *
 * Design Pattern: Factory Method
 *
 * @author Georg Eckert 2017
 */

public abstract class AGuardianFactory
{
    private Collection<String> UUIDs;

    public AGuardianFactory()
    {
        this.UUIDs = new ArrayList<>();
    }

    // ............................................................................................. IMPLEMENTED METHODS

    /**
     * Creates a unique identifier for a new Guardian
     * @return
     */
    public String createNewUUID()
    {
        boolean unique = false;
        String newUUID = "";
        while(!unique)
        {
            newUUID = UUID.randomUUID().toString();
            if(!UUIDs.contains(newUUID))
            {
                unique = true;
            }
        }
        UUIDs.add(newUUID);
        return newUUID;
    }

    // ............................................................................................. ABSTRACT METHODS

    public abstract AGuardian createGuardian(int ID, int level);
}
