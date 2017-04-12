package de.limbusdev.guardianmonsters.fwmengine.cutscene;

import com.badlogic.gdx.Screen;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.ui.AScreen;

/**
 * MetamorphosisScreen
 *
 * @author Georg Eckert 2017
 */

public class MetamorphosisScreen extends AScreen {
    public MetamorphosisScreen(int before, int after) {
        super(new MetamorphosisHUD(Services.getUI().getInventorySkin(), before, after));
    }
}
