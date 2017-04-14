package de.limbusdev.guardianmonsters.fwmengine.metamorphosis;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.ui.AScreen;

/**
 * MetamorphosisScreen
 *
 * This class only animates the guardian metamorphosis.
 * All internal data must be done before.
 *
 * @author Georg Eckert 2017
 */

public class MetamorphosisScreen extends AScreen {
    public MetamorphosisScreen(int before, int after) {
        super(new MetamorphosisHUD(Services.getUI().getInventorySkin(), before, after));
    }
}
