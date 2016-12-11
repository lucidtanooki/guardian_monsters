package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInformation;

/**
 * Created by georg on 11.12.16.
 */

public class MonsterMenuWidget extends SevenButtonsWidget {

    private static int order[] = {0,1,2,3,4,5,6}
;
    private ArrayMap<Integer,Monster> team;

    public MonsterMenuWidget(AHUD hud, Skin skin, CallbackHandler callbackHandler) {
        super(hud, skin, callbackHandler, order);
    }

    public void init(Array<Monster> team) {
        this.team = new ArrayMap<Integer, Monster>();

        // Set all buttons inactive
        for(Integer i : getButtons().keys()) {
            disableButton(i);
        }


        int i = 0;
        for(Monster m : team) {
            if(i > 6) break;
            ButtonWithImage bwi = new ButtonWithImage(
                Services.getL18N().l18n().get(MonsterInformation.getInstance().monsterNames.get(m.ID)),
                skin,
                "tb-attack-air");

            TextureRegion miniSprite = Services.getMedia().getMonsterMiniSprite(m.ID);
            bwi.setChildImage(new TextureRegionDrawable(miniSprite));
            replaceButton(bwi,i);

            this.team.put(i,m);
            if(m.getHP() > 0) enableButton(i);
            i++;
        }

    }


}
