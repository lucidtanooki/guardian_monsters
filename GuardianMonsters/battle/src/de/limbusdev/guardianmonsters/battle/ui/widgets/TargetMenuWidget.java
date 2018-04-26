package de.limbusdev.guardianmonsters.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ArrayMap;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.battle.BattleQueue;
import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem;
import de.limbusdev.guardianmonsters.guardians.battle.CombatTeam;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.Constant;

import static de.limbusdev.guardianmonsters.guardians.Constant.LEFT;
import static de.limbusdev.guardianmonsters.guardians.Constant.RIGHT;


/**
 * @author Georg Eckert 2017
 */

public class TargetMenuWidget extends SevenButtonsWidget implements Observer
{
    private CombatTeam leftTeam, rightTeam;
    private boolean areaMode;

    private static int[] order = {0,2,1,3,6,5,4};

    public TargetMenuWidget(Skin skin, Callbacks callbacks) {
        super(skin, callbacks, order);
    }

    public void init(BattleSystem battleSystem)
    {
        this.init(battleSystem, false);
    }

    public void init(BattleSystem battleSystem, boolean areaMode)
    {
        this.areaMode = areaMode;
        this.leftTeam = new CombatTeam();
        this.rightTeam = new CombatTeam();

        // Set all buttons inactive
        for(Integer i : getButtons().keys())
        {
            disableButton(i);
            setButtonText(4, "");
            setButtonStyle(i, Element.NONE);
        }

        BattleQueue queue = battleSystem.getQueue();
        addMonstersToMenu(queue.getCombatTeamLeft(), Constant.LEFT);
        addMonstersToMenu(queue.getCombatTeamRight(), Constant.RIGHT);

        if(areaMode) {
            setButtonText(4, Services.getL18N().Battle().get("battle_choose_area"));
            super.setButtonStyle(0, Element.ARTHROPODA);
            super.setButtonStyle(1, Element.ARTHROPODA);
            super.setButtonStyle(2, Element.ARTHROPODA);
            super.setButtonStyle(4, Element.FIRE);
            super.setButtonStyle(5, Element.FIRE);
            super.setButtonStyle(6, Element.FIRE);
        }
    }

    private void addMonstersToMenu(CombatTeam team, boolean side)
    {
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

    public AGuardian getMonsterOfIndex(int index)
    {
        if(index <=2) {
            return leftTeam.get(index);
        } else {
            return rightTeam.get(index-4);
        }
    }

    public ArrayMap<Integer,AGuardian> getCombatTeamOfIndex(int index)
    {
        if(getSideByButtonIndex(index) == LEFT) return leftTeam;
        else return rightTeam;
    }

    public void disableSide(boolean side)
    {
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

    public int getButtonPositionByFieldPosition(boolean side, int fieldPosition)
    {
        if(side == LEFT) return fieldPosition;
        else             return fieldPosition + 4;
    }

    public boolean getSideByButtonIndex(int index)
    {
        if(index < 4) return LEFT;
        else          return RIGHT;
    }

    @Override
    public void update(Observable o, Object arg)
    {
        Guardian guardian = (Guardian)o;

        if(guardian.getIndividualStatistics().isKO()) {
            int position;
            boolean side;

            if(leftTeam.isMember(guardian)) {
                side = LEFT;
                position = leftTeam.getFieldPosition(guardian);
            } else {
                side = RIGHT;
                position = rightTeam.getFieldPosition(guardian);
            }

            int buttonIndex = getButtonPositionByFieldPosition(side, position);
            disableButton(buttonIndex);
        }
    }
}
