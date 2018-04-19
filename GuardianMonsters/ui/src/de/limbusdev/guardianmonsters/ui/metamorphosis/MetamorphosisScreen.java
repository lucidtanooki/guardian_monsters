package de.limbusdev.guardianmonsters.ui.metamorphosis;

import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.AScreen;

/**
 * MetamorphosisScreen
 *
 * This class only animates the guardian metamorphosis.
 * All internal data must be done before.
 *
 * @author Georg Eckert 2017
 */

public class MetamorphosisScreen extends AScreen
{
    public MetamorphosisScreen(int speciesID, int before)
    {
        super(new MetamorphosisHUD(Services.getUI().getInventorySkin(), speciesID, before, before+1));
    }
}
