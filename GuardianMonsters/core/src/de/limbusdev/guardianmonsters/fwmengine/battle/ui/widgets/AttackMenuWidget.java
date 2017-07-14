package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.limbusdev.guardianmonsters.assets.paths.AssetPath;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;

public class AttackMenuWidget extends SevenButtonsWidget {

    private static int[] order = {5,3,1,0,4,2,6};

    /**
     *
     * @param skin battle action UI skin
     */
    public AttackMenuWidget(Skin skin, Callbacks callbacks) {
        super(skin, callbacks, order);
    }

    public void init(Guardian guardian) {


        // Set all buttons inactive
        for(Integer i : getButtons().keys()) {
            disableButton(i);
        }

        // for every attack, activate a button
        for(int i=0; i<7; i++) {
            Ability attack = guardian.abilityGraph.getActiveAbility(i);

            if(attack != null) {
                setButtonStyle(i,skin, AssetPath.Skin.attackButtonStyle(attack.element));
                String mpCostString = (attack.MPcost > 0) ? (" " + Integer.toString(attack.MPcost)) : "";
                setButtonText(i,Services.getL18N().Abilities().get(attack.name) + mpCostString);

                // Disable Ability, when monster does not have enough MP for it
                if(attack.MPcost <= guardian.stat.getMP()) enableButton(i);
            }
        }
    }
}
