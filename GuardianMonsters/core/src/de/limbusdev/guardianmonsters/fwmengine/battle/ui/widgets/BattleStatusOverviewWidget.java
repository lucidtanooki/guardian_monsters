package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleSystem;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.geometry.IntVec2;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */
public class BattleStatusOverviewWidget extends BattleWidget {

    private Array<MonsterStateWidget> monsterStateWidgetsLeft, monsterStateWidgetsRight;

    public BattleStatusOverviewWidget(final AHUD hud, Skin skin) {
        super(hud);
        this.monsterStateWidgetsLeft = new Array<MonsterStateWidget>();
        this.monsterStateWidgetsRight = new Array<MonsterStateWidget>();

        // Hero Team ###############################################################################
        MonsterStateWidget msw = new MonsterStateWidget(skin, true);
        msw.setPosition(IndPos.statWPos1.x+32*GS.zoom,IndPos.statWPos1.y);
        monsterStateWidgetsLeft.add(msw);
        msw = new MonsterStateWidget(skin, true);
        msw.setPosition(IndPos.statWPos2.x+32*GS.zoom,IndPos.statWPos2.y);
        monsterStateWidgetsLeft.add(msw);
        msw = new MonsterStateWidget(skin, true);
        msw.setPosition(IndPos.statWPos3.x+32*GS.zoom,IndPos.statWPos3.y);
        monsterStateWidgetsLeft.add(msw);

        // Opponent Team ###########################################################################
        msw = new MonsterStateWidget(skin, false);
        msw.setPosition(GS.RES_X-IndPos.statWPos1.x,IndPos.statWPos1.y,Align.bottomRight);
        monsterStateWidgetsRight.add(msw);
        msw = new MonsterStateWidget(skin, false);
        msw.setPosition(GS.RES_X-IndPos.statWPos2.x,IndPos.statWPos2.y,Align.bottomRight);
        monsterStateWidgetsRight.add(msw);
        msw = new MonsterStateWidget(skin, false);
        msw.setPosition(GS.RES_X-IndPos.statWPos3.x,IndPos.statWPos3.y,Align.bottomRight);
        monsterStateWidgetsRight.add(msw);

        for(MonsterStateWidget w : monsterStateWidgetsLeft) addActor(w);
        for(MonsterStateWidget w : monsterStateWidgetsRight) addActor(w);

    }

    private void addStatusWidgetsForTeam(ArrayMap<Integer,Monster> team, boolean side) {
        Array<MonsterStateWidget> stateWidgets = side ?
            monsterStateWidgetsLeft : monsterStateWidgetsRight;

        // Clear Actions
        for(MonsterStateWidget w : stateWidgets) {
            w.clearActions();
            w.remove();
            w.setVisible(true);
            w.setColor(Color.WHITE);
        }

        // Initialize UI
        for(int key : team.keys()) {
            stateWidgets.get(key).init(team.get(key));
            addActor(stateWidgets.get(key));
        }
    }

    public void init(BattleSystem battleSystem) {
        addStatusWidgetsForTeam(battleSystem.getLeftInBattle(),true);
        addStatusWidgetsForTeam(battleSystem.getRightInBattle(),false);
    }

    public void fadeStatusWidget(int pos, boolean side) {
        if(side) {
            monsterStateWidgetsLeft.get(pos).addAction(
                Actions.sequence(Actions.alpha(0, 2), Actions.visible(false)));
        } else {
            monsterStateWidgetsRight.get(pos).addAction(
                Actions.sequence(Actions.alpha(0, 2), Actions.visible(false)));
        }
    }


    /**
     * Possible Indicator coordinates
     */
    private final static class IndPos {
        private static final IntVec2 statWPos1 = new IntVec2(GS.COL*5,GS.RES_Y-GS.ROW*7);
        private static final IntVec2 statWPos2 = new IntVec2(GS.COL*2,GS.RES_Y-GS.ROW*10);
        private static final IntVec2 statWPos3 = new IntVec2(GS.COL*8,GS.RES_Y-GS.ROW*4);
    }


}
