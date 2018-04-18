package de.limbusdev.guardianmonsters.battle.ui.widgets;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.guardians.battle.BattleQueue;
import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem;
import de.limbusdev.guardianmonsters.guardians.battle.CombatTeam;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.Constant;


/**
 * @author Georg Eckert 2017
 */

public class TargetMenuWidget extends SevenButtonsWidget implements Observer
{

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
        addMonstersToMenu(queue.getCombatTeamLeft(), Constant.LEFT);
        addMonstersToMenu(queue.getCombatTeamRight(), Constant.RIGHT);
    }

    private void addMonstersToMenu(CombatTeam team, boolean side) {
        int offset = side ? 0 : 4;
        if(side == Constant.LEFT) {
            leftTeam = team;
        } else {
            rightTeam = team;
        }

        for(int key : team.keys())
        {
            AGuardian m = team.get(key);
            setButtonText(key + offset, Services.getL18N().getLocalizedGuardianName(m));
            enableButton(key + offset);

            // Add the TargetMenuWidget as a Listener
            m.addObserver(this);
        }
    }

    public AGuardian getMonsterOfIndex(int index) {
        if(index <=2) {
            return leftTeam.get(index);
        } else {
            return rightTeam.get(index-4);
        }
    }

    public void disableSide(boolean side) {
        int from, to;
        if(side == Constant.LEFT) {
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


    public void receive(Signal<Guardian> signal, Guardian guardian) {

    }

    @Override
    public void update(Observable o, Object arg)
    {
        Guardian guardian = (Guardian)o;
        if(guardian.getIndividualStatistics().isKO()) {
            int index;
            if(leftTeam.isMember(guardian)) {
                index = leftTeam.getFieldPosition(guardian);
            } else {
                index = rightTeam.getFieldPosition(guardian);
            }
            disableButton(index);
        }
    }
}
