package de.limbusdev.guardianmonsters.battle.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem;
import de.limbusdev.guardianmonsters.guardians.battle.CombatTeam;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.ui.Constant;
import de.limbusdev.utils.geometry.IntVec2;


/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by Georg Eckert 2016
 */
public class BattleStatusOverviewWidget extends BattleWidget
{

    private Array<MonsterStateWidget>
        monsterStateWidgetsLeft,
        monsterStateWidgetsRight;

    public BattleStatusOverviewWidget(Skin skin)
    {
        super();
        this.monsterStateWidgetsLeft = new Array<>();
        this.monsterStateWidgetsRight = new Array<>();

        // Hero Team ###############################################################################
        MonsterStateWidget msw = new MonsterStateWidget(skin, true);
        msw.setPosition(IndPos.statWPos1left.x, IndPos.statWPos1left.y, Align.topLeft);
        monsterStateWidgetsLeft.add(msw);
        msw = new MonsterStateWidget(skin, true);
        msw.setPosition(IndPos.statWPos2left.x, IndPos.statWPos2left.y, Align.topLeft);
        monsterStateWidgetsLeft.add(msw);
        msw = new MonsterStateWidget(skin, true);
        msw.setPosition(IndPos.statWPos3left.x, IndPos.statWPos3left.y, Align.topLeft);
        monsterStateWidgetsLeft.add(msw);

        // Opponent Team ###########################################################################
        msw = new MonsterStateWidget(skin, false);
        msw.setPosition(IndPos.statWPos1right.x, IndPos.statWPos1right.y, Align.topRight);
        monsterStateWidgetsRight.add(msw);
        msw = new MonsterStateWidget(skin, false);
        msw.setPosition(IndPos.statWPos2right.x, IndPos.statWPos2right.y, Align.topRight);
        monsterStateWidgetsRight.add(msw);
        msw = new MonsterStateWidget(skin, false);
        msw.setPosition(IndPos.statWPos3right.x, IndPos.statWPos3right.y, Align.topRight);
        monsterStateWidgetsRight.add(msw);

        for(MonsterStateWidget w : monsterStateWidgetsLeft) {addActor(w);}
        for(MonsterStateWidget w : monsterStateWidgetsRight) {addActor(w);}

        setDebug(Constant.DEBUGGING_ON, true);

    }

    private void addStatusWidgetsForTeam(CombatTeam team, boolean side)
    {
        Array<MonsterStateWidget> stateWidgets = side ?
            monsterStateWidgetsLeft : monsterStateWidgetsRight;

        // Clear Actions
        for(MonsterStateWidget w : stateWidgets)
        {
            w.clearActions();
            w.remove();
            w.setVisible(true);
            w.setColor(Color.WHITE);
        }

        // Initialize UI
        for(int key : team.keys())
        {
            stateWidgets.get(key).init(team.get(key));
            addActor(stateWidgets.get(key));
        }
    }

    public void init(BattleSystem battleSystem)
    {
        addStatusWidgetsForTeam(battleSystem.getQueue().getCombatTeamLeft(), Constant.LEFT);
        addStatusWidgetsForTeam(battleSystem.getQueue().getCombatTeamRight(), Constant.RIGHT);
    }

    public void updateStatusWidgetToSubstitute(int pos, boolean side, AGuardian guardian)
    {
        Array<MonsterStateWidget> stateWidgets = side ?
            monsterStateWidgetsLeft : monsterStateWidgetsRight;

        MonsterStateWidget w = stateWidgets.get(pos);
        w.clearActions();
        w.addAction(Actions.sequence(
            Actions.fadeOut(1f),
            Actions.run(() -> {w.init(guardian);}),
            Actions.fadeIn(1f)
        ));
    }


    /**
     * Possible Indicator coordinates
     */
    private final static class IndPos
    {
        private static final IntVec2 statWPos1left = new IntVec2(56+24, 360-24);
        private static final IntVec2 statWPos2left = new IntVec2(56, 360-48);
        private static final IntVec2 statWPos3left = new IntVec2(56+48, 360);
        private static final IntVec2 statWPos1right = new IntVec2(640-8-24, statWPos1left.y);
        private static final IntVec2 statWPos2right = new IntVec2(640-8, statWPos2left.y);
        private static final IntVec2 statWPos3right = new IntVec2(640-8-48, statWPos3left.y);
    }


}
