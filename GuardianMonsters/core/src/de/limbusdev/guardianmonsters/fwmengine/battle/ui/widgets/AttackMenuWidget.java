package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.data.SkinAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Ability;
import de.limbusdev.guardianmonsters.model.Monster;

public class AttackMenuWidget extends SevenButtonsWidget {

    private static int[] order = {5,3,1,0,4,2,6};

    /**
     *
     * @param skin battle action UI skin
     */
    public AttackMenuWidget(Skin skin, ClickListener clickListener) {
        super(skin, clickListener, order);
    }

    public void init(Monster monster) {


        // Set all buttons inactive
        for(Integer i : getButtons().keys()) {
            disableButton(i);
        }

        // for every attack, activate a button
        for(int i=0; i<7; i++) {
            Ability attack = monster.getActiveAbility(i);

            if(attack != null) {
                setButtonStyle(i,skin, SkinAssets.attackButtonStyle(attack.element));
                String mpCostString = (attack.MPcost > 0) ? (" " + Integer.toString(attack.MPcost)) : "";
                setButtonText(i,Services.getL18N().l18n(BundleAssets.ATTACKS).get(attack.name) + mpCostString);

                // Disable Ability, when monster does not have enough MP for it
                if(attack.MPcost <= monster.stat.getMP()) enableButton(i);
            }
        }
    }
}
