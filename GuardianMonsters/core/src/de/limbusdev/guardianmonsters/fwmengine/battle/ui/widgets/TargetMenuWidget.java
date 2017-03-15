package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ArrayMap;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleSystem;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterDB;
import de.limbusdev.guardianmonsters.model.Stat;

/**
 * @author Georg Eckert 2017
 */

public class TargetMenuWidget extends SevenButtonsWidget implements Listener<Monster> {

    public static final boolean LEFT = true;
    public static final boolean RIGHT = false;

    private ArrayMap<Integer,Monster> leftTeam, rightTeam;

    private static int[] order = {0,2,1,3,6,5,4};

    public TargetMenuWidget(Skin skin, ClickListener clickListener) {
        super(skin, clickListener, order);
    }

    public void init(BattleSystem battleSystem) {
        this.leftTeam = new ArrayMap<>();
        this.rightTeam = new ArrayMap<>();

        // Set all buttons inactive
        for(Integer i : getButtons().keys()) {
            disableButton(i);
        }


        addMonstersToMenu(battleSystem.getLeftInBattle(), LEFT);
        addMonstersToMenu(battleSystem.getRightInBattle(), RIGHT);
    }

    private void addMonstersToMenu(ArrayMap<Integer,Monster> team, boolean side) {
        int offset = side ? 0 : 4;
        if(side == LEFT) {
            leftTeam = team;
        } else {
            rightTeam = team;
        }

        for(int key : team.keys()) {
            Monster m = team.get(key);
            setButtonText(key + offset, Services.getL18N().l18n(BundleAssets.MONSTERS).get(
                MonsterDB.singleton().getNameById(m.ID)));
            enableButton(key + offset);

            // Add the TargetMenuWidget as a Listener
            m.add(this);
        }
    }

    public Monster getMonsterOfIndex(int index) {
        if(index <=2) {
            return leftTeam.get(index);
        } else {
            return rightTeam.get(index-4);
        }
    }

    public void disableSide(boolean side) {
        int from, to;
        if(side == LEFT) {
            // Disable all Buttons for the left team
            from = 0;
            to = 2;
        } else {
            // Disable all Buttons for the right team
            from = 4;
            to = 6;
        }

        for(int i = from; i<=to; i++) {
            disableButton(i);
        }
    }


    @Override
    public void receive(Signal<Monster> signal, Monster monster) {
        if(monster.stat.isKO()) {
            int index;
            if(leftTeam.containsValue(monster, false)) {
                index = leftTeam.indexOfValue(monster, false);
            } else {
                index = rightTeam.indexOfValue(monster, false);
            }
            disableButton(index);
        }
    }
}
