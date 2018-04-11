package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;


/**
 * GuardianDescriptionService
 *
 * @author Georg Eckert 2018
 */

public class SpeciesDescriptionService implements ISpeciesDescriptionService
{
    private ArrayMap<Integer, SpeciesDescription> speciesDB;

    private static SpeciesDescriptionService instance;

    private SpeciesDescriptionService(String xmlSpeciesDescriptions)
    {
        speciesDB = new ArrayMap<>();
        JsonValue rootElement = JSONGuardianParser.parseGuardianList(xmlSpeciesDescriptions);

        for (int i = 0; i < rootElement.size; i++)
        {
            SpeciesDescription info = JSONGuardianParser.parseGuardian(rootElement.get(i));
            speciesDB.put(info.getID(),info);
        }
    }

    public static SpeciesDescriptionService getInstance(String xmlSpecies)
    {
        if(instance == null) {
            instance = new SpeciesDescriptionService(xmlSpecies);
        }
        return instance;
    }

    public static SpeciesDescriptionService getInstanceFromFile(String xmlSpeciesDescriptionFilePath)
    {
        String xml = Gdx.files.internal(xmlSpeciesDescriptionFilePath).readString();
        return getInstance(xml);
    }


    @Override
    public SpeciesDescription getSpeciesDescription(int speciesID)
    {
        return speciesDB.get(speciesID);
    }

    @Override
    public String getCommonNameById(int speciesID, int form)
    {
        return speciesDB.get(speciesID).getNameID(form);
    }


    @Override
    public void destroy()
    {
        instance = null;
    }
}
