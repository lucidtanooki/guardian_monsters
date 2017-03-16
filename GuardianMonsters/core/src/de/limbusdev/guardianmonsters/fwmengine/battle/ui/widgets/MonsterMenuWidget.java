package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.model.monsters.Element;
import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleSystem;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.monsters.Monster;
import de.limbusdev.guardianmonsters.model.MonsterDB;

/**
 * Created by georg on 11.12.16.
 */

public class MonsterMenuWidget extends SevenButtonsWidget {

    private static int order[] = {0,1,2,3,4,5,6};

    public MonsterMenuWidget(Skin skin, Callbacks callbacks) {
        super(skin, callbacks, order);
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
            TextButton bwi = new BattleHUDTextButton(
                Services.getL18N().l18n(BundleAssets.MONSTERS).get(MonsterDB.getInstance().getNameById(m.ID)),
                skin, key, Element.AIR
            );
                new ButtonWithImage(
                Services.getL18N().l18n(BundleAssets.MONSTERS).get(MonsterDB.getInstance().getNameById(m.ID)),
                skin,
                "tb-attack-air");

            replaceButton(bwi,key);

            if(m.stat.isFit() && !inBattle.containsValue(m,false)) {
                enableButton(key);
            }
        }

    }

}
