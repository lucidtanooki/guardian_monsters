package de.limbusdev.guardianmonsters.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.enums.SFXType;
import de.limbusdev.guardianmonsters.geometry.IntVector2;
import de.limbusdev.guardianmonsters.managers.MediaManager;
import de.limbusdev.guardianmonsters.model.MonsterInBattle;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */
public class BattleStatusOverviewWidget extends WidgetGroup {

    private Array<MonsterStateWidget> monsterStateWidgetsLeft, monsterStateWidgetsRight;

    public BattleStatusOverviewWidget(Skin skin) {
        super();

        this.monsterStateWidgetsLeft = new Array<MonsterStateWidget>();
        this.monsterStateWidgetsRight = new Array<MonsterStateWidget>();

        // Hero Team ###############################################################################
        MonsterStateWidget msw = new MonsterStateWidget(skin);
        msw.setPosition(IndPos.statWPos1.x,IndPos.statWPos1.y);
        monsterStateWidgetsLeft.add(msw);
        msw = new MonsterStateWidget(skin);
        msw.setPosition(IndPos.statWPos2.x,IndPos.statWPos2.y);
        monsterStateWidgetsLeft.add(msw);
        msw = new MonsterStateWidget(skin);
        msw.setPosition(IndPos.statWPos3.x,IndPos.statWPos3.y);
        monsterStateWidgetsLeft.add(msw);

        // Opponent Team ###########################################################################
        msw = new MonsterStateWidget(skin);
        msw.setPosition(GS.RES_X-IndPos.statWPos1.x,IndPos.statWPos1.y,Align.bottomRight);
        monsterStateWidgetsRight.add(msw);
        msw = new MonsterStateWidget(skin);
        msw.setPosition(GS.RES_X-IndPos.statWPos2.x,IndPos.statWPos2.y,Align.bottomRight);
        monsterStateWidgetsRight.add(msw);
        msw = new MonsterStateWidget(skin);
        msw.setPosition(GS.RES_X-IndPos.statWPos3.x,IndPos.statWPos3.y,Align.bottomRight);
        monsterStateWidgetsRight.add(msw);

        for(MonsterStateWidget w : monsterStateWidgetsLeft) addActor(w);
        for(MonsterStateWidget w : monsterStateWidgetsRight) addActor(w);

    }

    /**
     *
     * @param hero heros monsters
     * @param oppo opponents monsters
     */
    public void init(Array<MonsterInBattle> hero, Array<MonsterInBattle> oppo) {
        clear();

        // Clear Actions
        for(MonsterStateWidget w : monsterStateWidgetsLeft) w.clearActions();
        for(MonsterStateWidget w : monsterStateWidgetsRight) w.clearActions();


        for(MonsterStateWidget w : monsterStateWidgetsLeft) {
            w.addAction(Actions.alpha(1));
            w.setVisible(false);
        }
        for(MonsterStateWidget w : monsterStateWidgetsRight) {
            w.addAction(Actions.alpha(1));
            w.setVisible(false);
        }


        // Initialize Status UIs ...................................................................
        // Hero Team
        switch(hero.size) {
            case 3:  monsterStateWidgetsLeft.get(2).init(hero.get(2).monster);
            case 2:  monsterStateWidgetsLeft.get(2).init(hero.get(2).monster);
            default: monsterStateWidgetsLeft.get(2).init(hero.get(2).monster);break;
        }

        // Opponent Team
        switch(oppo.size) {
            case 3:  monsterStateWidgetsRight.get(2).init(oppo.get(2).monster);
            case 2:  monsterStateWidgetsRight.get(2).init(oppo.get(2).monster);
            default: monsterStateWidgetsRight.get(2).init(oppo.get(2).monster);break;
        }
    }

    public void fadeStatusWidget(int pos, boolean side) {
        if(side) {
            monsterStateWidgetsLeft.get(pos).addAction(
                Actions.sequence(Actions.alpha(0, 2), Actions.visible(false)));
        } else {
            monsterStateWidgetsLeft.get(pos).addAction(
                Actions.sequence(Actions.alpha(0, 2), Actions.visible(false)));
        }

    }


    /**
     * Possible Indicator coordinates
     */
    private final static class IndPos {
        private static final IntVector2 statWPos1 = new IntVector2(GS.COL*5,GS.RES_Y-GS.ROW*7);
        private static final IntVector2 statWPos2 = new IntVector2(GS.COL*2,GS.RES_Y-GS.ROW*10);
        private static final IntVector2 statWPos3 = new IntVector2(GS.COL*8,GS.RES_Y-GS.ROW*4);
    }

}
