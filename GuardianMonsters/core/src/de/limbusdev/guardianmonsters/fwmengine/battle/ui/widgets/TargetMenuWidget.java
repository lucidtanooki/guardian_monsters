package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInformation;

/**
 * Created by georg on 26.11.16.
 */

public class TargetMenuWidget extends SevenButtonsWidget implements Observer {

    private ArrayMap<Integer,Monster> leftTeam, rightTeam;

    private static int[] order = {0,2,1,3,6,5,4};

    public TargetMenuWidget(AHUD hud, Skin skin, CallbackHandler callbackHandler) {
        super(hud, skin, callbackHandler, order);
    }

    public void init(Array<Monster> leftTeam, Array<Monster> rightTeam) {
        this.leftTeam = new ArrayMap<Integer, Monster>();
        this.rightTeam = new ArrayMap<Integer, Monster>();

        // Set all buttons inactive
        for(Integer i : getButtons().keys()) {
            disableButton(i);
        }


        int i = 0;
        for(Monster m : leftTeam) {
            if(m.getHP() > 0) {
                this.leftTeam.put(i,m);
                setButtonText(i, Services.getL18N().l18n().get(
                    MonsterInformation.getInstance().monsterNames.get(m.ID-1)));
                enableButton(i);
                i++;
            }
        }

        i = 4;
        for(Monster m : rightTeam) {
            if(m.getHP() > 0) {
                this.rightTeam.put(i,m);
                setButtonText(i, Services.getL18N().l18n().get(
                    MonsterInformation.getInstance().monsterNames.get(m.ID-1)));
                enableButton(i);
                i++;
            }
        }
    }

    public Monster getMonsterOfIndex(int index) {
        if(index <=2) {
            return leftTeam.get(index);
        } else {
            return rightTeam.get(index);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        // Disable Button if monster got killed
        if(o instanceof Monster) {
            Monster observedMonster = (Monster) o;
            if(observedMonster.getHP() <= 0) {
                int index;
                if(leftTeam.containsValue(observedMonster,false)) {
                    index = leftTeam.indexOfValue(observedMonster,false);
                } else {
                    index = rightTeam.indexOfValue(observedMonster,false);
                }
                disableButton(index);
            }
        }
    }
}
