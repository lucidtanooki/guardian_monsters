package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.gdx.utils.ArrayMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import de.limbusdev.utils.services.IService;

/**
 * AGuardianFactory
 *
 * Design Pattern: Factory Method
 *
 * @author Georg Eckert 2017
 */

public abstract class AGuardianFactory
{
    private static Collection<String> UUIDs;
    private static ArrayMap<Integer, SpeciesDescription> speciesDB;

    public AGuardianFactory()
    {
        getUUIDs();
        getSpeciesDB();
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

    public final static ArrayMap<Integer, SpeciesDescription> getSpeciesDB()
    {
        if(speciesDB == null) speciesDB = new ArrayMap<>();
        return speciesDB;
    }

    public final static Collection<String> getUUIDs()
    {
        if(UUIDs == null) UUIDs = new ArrayList<>();
        return UUIDs;
    }

    public SpeciesDescription getSpeciesDescription(int speciesID)
    {
        return speciesDB.get(speciesID);
    }

    public String getNameById(int id)
    {
        return speciesDB.get(id).getNameID();
    }

    public int getNumberOfAncestors(int speciesID)
    {
        if(speciesID == 1) return 0;

        boolean hasAncestor = true;
        int ancestors = 0;

        while(hasAncestor) {
            SpeciesDescription possibleAncestor = getSpeciesDescription(speciesID - 1 - ancestors);
            if(possibleAncestor != null) {
                if (possibleAncestor.getMetamorphsTo() == speciesID - ancestors) {
                    ancestors++;
                } else {
                    hasAncestor = false;
                }
            } else {
                hasAncestor = false;
            }
        }

        return ancestors;
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
}
