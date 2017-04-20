package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleQueue;
import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleSystem;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.CombatTeam;
import de.limbusdev.guardianmonsters.model.monsters.Monster;
import de.limbusdev.guardianmonsters.model.MonsterDB;

import static de.limbusdev.guardianmonsters.Constant.LEFT;
import static de.limbusdev.guardianmonsters.Constant.RIGHT;

/**
 * @author Georg Eckert 2017
 */

public class TargetMenuWidget extends SevenButtonsWidget implements Listener<Monster> {

    private CombatTeam leftTeam, rightTeam;

    private static int[] order = {0,2,1,3,6,5,4};

    public TargetMenuWidget(Skin skin, Callbacks callbacks) {
        super(skin, callbacks, order);
    }

    public void init(BattleSystem battleSystem) {
        this.leftTeam = new CombatTeam();
        this.rightTeam = new CombatTeam();

        // Set all buttons inactive
        for(Integer i : getButtons().keys()) {
            disableButton(i);
        }

        BattleQueue queue = battleSystem.getQueue();
        addMonstersToMenu(queue.getCombatTeamLeft(), LEFT);
        addMonstersToMenu(queue.getCombatTeamRight(), RIGHT);
    }

    private void addMonstersToMenu(CombatTeam team, boolean side) {
        int offset = side ? 0 : 4;
        if(side == LEFT) {
            leftTeam = team;
        } else {
            rightTeam = team;
        }

        for(int key : team.keys()) {
            Monster m = team.get(key);
            setButtonText(key + offset, MonsterDB.getLocalNameById(m.ID));
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
            if(leftTeam.isMember(monster)) {
                index = leftTeam.getFieldPosition(monster);
            } else {
                index = rightTeam.getFieldPosition(monster);
            }
            disableButton(index);
        }
    }
}
