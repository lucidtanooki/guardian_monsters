package de.limbusdev.guardianmonsters.guardians.monsters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;


/**
 * GuardianDescriptionService
 *
 * @author Georg Eckert 2017
 */

public class SpeciesDescriptionService implements ISpeciesDescriptionService
{
    private ArrayMap<Integer, SpeciesDescription> speciesDB;

    private static SpeciesDescriptionService instance;

    private SpeciesDescriptionService(String xmlSpeciesDescriptions)
    {
        speciesDB = new ArrayMap<>();
        XmlReader.Element rootElement = XMLGuardianParser.parseGuardianList(xmlSpeciesDescriptions);

        SpeciesDescription ancestor = null;
        for (int i = 0; i < rootElement.getChildCount(); i++)
        {
            SpeciesDescription info = XMLGuardianParser.parseMonster(rootElement.getChild(i), ancestor);
            speciesDB.put(info.getID(),info);

            if(info.getMetamorphsTo() == info.getID()+1) {
                ancestor = info;
            } else {
                ancestor = null;
            }
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
    public String getCommonNameById(int speciesID)
    {
        return speciesDB.get(speciesID).getNameID();
    }

    @Override
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

    @Override
    public void destroy()
    {
        instance = null;
    }
}
