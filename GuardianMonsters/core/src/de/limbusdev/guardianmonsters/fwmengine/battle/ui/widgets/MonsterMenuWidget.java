package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleSystem;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInfo;

/**
 * Created by georg on 11.12.16.
 */

public class MonsterMenuWidget extends SevenButtonsWidget {

    private static int order[] = {0,1,2,3,4,5,6};

    public MonsterMenuWidget(AHUD hud, Skin skin, CallbackHandler callbackHandler) {
        super(hud, skin, callbackHandler, order);
    }

    public void init(BattleSystem battleSystem, boolean side) {

        ArrayMap<Integer,Monster> team = side ?
            battleSystem.getLeftTeam() : battleSystem.getRightTeam();
        ArrayMap<Integer,Monster> inBattle = side ?
            battleSystem.getLeftInBattle() : battleSystem.getRightInBattle();

        // Set all buttons inactive
        for(Integer i : getButtons().keys()) {
            disableButton(i);
        }

        for(int key : team.keys()) {
            if(key > 6) break;
            Monster m = team.get(key);
            ButtonWithImage bwi = new ButtonWithImage(
                Services.getL18N().l18n().get(MonsterInfo.getInstance().getNameById(m.ID)),
                skin,
                "tb-attack-air");

            TextureRegion miniSprite = Services.getMedia().getMonsterMiniSprite(m.ID);
            bwi.setChildImage(new TextureRegionDrawable(miniSprite));
            replaceButton(bwi,key);

            if(m.getHP() > 0 && !inBattle.containsValue(m,false)) {
                enableButton(key);
            }
        }

    }

}
