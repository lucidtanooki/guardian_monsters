package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.data.SkinAssets;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.model.Monster;

public class AttackMenuWidget extends SevenButtonsWidget {

    private static int[] order = {5,3,1,0,4,2,6};

    /**
     *
     * @param skin battle action UI skin
     */
    public AttackMenuWidget(final AHUD hud, Skin skin, SevenButtonsWidget.CallbackHandler callbackHandler) {
        super(hud, skin, callbackHandler, order);
    }

    public void init(Monster monster) {

        // get monsters attacks
        Array<Attack> attacks = monster.attacks;

        // Set all buttons inactive
        for(Integer i : getButtons().keys()) {
            disableButton(i);
        }

        // for every attack, activate a button
        for(int i=0; i<attacks.size && i < 7; i++) {
            Attack att = attacks.get(i);
            setButtonStyle(i,skin, SkinAssets.attackButtonStyle(att.element));
            setButtonText(i,Services.getL18N().l18n().get(att.name) + " (" + att.damage + ")");
            enableButton(i);
        }
    }
}
