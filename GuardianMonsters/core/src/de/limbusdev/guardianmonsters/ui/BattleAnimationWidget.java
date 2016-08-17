package de.limbusdev.guardianmonsters.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
public class BattleAnimationWidget extends WidgetGroup implements ObservableWidget {

    private Array<WidgetObserver> observers;

    private ArrayMap<Integer,Image> monsterImgsLeft, monsterImgsRight;
    private MediaManager media;

    public boolean attackAnimationRunning;

    public BattleAnimationWidget(MediaManager media) {
        super();

        observers = new Array<WidgetObserver>();
        this.monsterImgsLeft = new ArrayMap<Integer,Image>();
        this.monsterImgsRight = new ArrayMap<Integer,Image>();
        this.setBounds(0,0,0,0);
        this.media = media;
    }

    /**
     *
     * @param hero heros monsters
     * @param oppo opponents monsters
     */
    public void init(Array<MonsterInBattle> hero, Array<MonsterInBattle> oppo) {
        clear();

        monsterImgsLeft.clear();
        monsterImgsRight.clear();
        for(MonsterInBattle m : hero)
            setUpMonsterSprite(m.monster.ID, m.battleFieldPosition, m.battleFieldSide);
        for(MonsterInBattle m : oppo)
            setUpMonsterSprite(m.monster.ID, m.battleFieldPosition, m.battleFieldSide);

        for(Integer key : monsterImgsLeft.keys())
            addActor(monsterImgsLeft.get(key));
        for(Integer key : monsterImgsRight.keys())
            addActor(monsterImgsRight.get(key));
    }

    /**
     * Adds Monster sprite at the given battle arena position
     * @param id
     * @param pos
     * @param side
     */
    private void setUpMonsterSprite(int id, int pos, boolean side) {
        Image monImg;
        TextureRegion monReg;
        monReg = media.getMonsterSprite(id);
        if(side)
            if(!monReg.isFlipX())
                monReg.flip(true, false);
        monImg = new Image(monReg);
        monImg.setSize(256,256);

        IntVector2 position2d;
        int align;
        if(side) {
            // Hero Side
            switch(pos) {
                case 2:  position2d = ImPos.HERO_TOP;break;
                case 1:  position2d = ImPos.HERO_BOT;break;
                default: position2d = ImPos.HERO_MID;break;
            }
            align = Align.bottomLeft;
            monsterImgsLeft.put(pos,monImg);
        } else {
            // Opponent Side
            switch(pos) {
                case 2:  position2d = ImPos.OPPO_TOP;break;
                case 1:  position2d = ImPos.OPPO_BOT;break;
                default: position2d = ImPos.OPPO_MID;break;
            }
            monsterImgsRight.put(pos,monImg);
            align = Align.bottomRight;
        }

        monImg.setPosition(position2d.x, position2d.y, align);
    }


    /**
     * Animate an attack of the given monster
     * @param attPos    position of attacker
     * @param defPos    position of defender
     */
    public void animateAttack(final int attPos, int defPos, boolean side) {
        Image attIm,defIm;
        IntVector2 startPos,endPos;
        int attAlign, defAlign;

        if(side) {
            attAlign = Align.bottomLeft;
            defAlign = Align.bottomRight;
        } else {
            attAlign = Align.bottomRight;
            defAlign = Align.bottomLeft;
        }

        attackAnimationRunning = true;

        if(side)
            switch(attPos) {
                case 2:  startPos = ImPos.HERO_TOP;break;
                case 1:  startPos = ImPos.HERO_BOT;break;
                default: startPos = ImPos.HERO_MID;break;
            }
        else
            switch(attPos) {
                case 2:
                    startPos = ImPos.OPPO_TOP;
                    break;
                case 1:
                    startPos = ImPos.OPPO_BOT;
                    break;
                default:
                    startPos = ImPos.OPPO_MID;
                    break;
            }

        if(side)
            switch(defPos) {
                case 2:  endPos = ImPos.OPPO_TOP;break;
                case 1:  endPos = ImPos.OPPO_BOT;break;
                default: endPos = ImPos.OPPO_MID;break;
            }
        else
            switch(defPos) {
                case 2:
                    endPos= ImPos.HERO_TOP;
                    break;
                case 1:
                    endPos = ImPos.HERO_BOT;
                    break;
                default:
                    endPos = ImPos.HERO_MID;
                    break;
            }


        if(side) attIm = monsterImgsLeft.get(attPos);
        else attIm = monsterImgsRight.get(attPos);
        attIm.addAction(Actions.sequence(
            Actions.moveToAligned(endPos.x, endPos.y, defAlign, .6f, Interpolation.pow2In),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    media.getSFX(SFXType.HIT, 0).play();
                }
            }),
            Actions.moveToAligned(startPos.x, startPos.y, attAlign, .3f, Interpolation.pow2Out),
            Actions.delay(.5f),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    attackAnimationRunning=false;
                    notifyWidgetObservers();
                }
            })
        ));
        if(side) defIm = monsterImgsRight.get(defPos);
        else defIm = monsterImgsLeft.get(defPos);
        defIm.addAction(Actions.sequence(
            Actions.delay(.6f), Actions.moveBy(0, 15, .1f, Interpolation.bounceIn),
            Actions.moveBy(0, -15, .1f, Interpolation.bounceIn)
        ));

    }

    public void animateMonsterKO(int pos, boolean side) {
        ArrayMap<Integer,Image> monImgs;
        if(side) monImgs = monsterImgsLeft;
        else monImgs = monsterImgsRight;

        monImgs.get(pos).addAction(Actions.sequence(Actions.alpha(0, 2), Actions.visible(false)));
    }


    @Override
    public void addWidgetObserver(WidgetObserver wo) {
        observers.add(wo);
    }

    @Override
    public void notifyWidgetObservers() {
        for(WidgetObserver wo : observers) wo.getNotified(this);
    }


    private final static class ImPos {
        private static final IntVector2 HERO_MID = new IntVector2(GS.COL*7,GS.ROW*18);
        private static final IntVector2 HERO_BOT = new IntVector2(GS.COL*1,GS.ROW*15);
        private static final IntVector2 HERO_TOP = new IntVector2(GS.COL*13,GS.ROW*21);
        private static final IntVector2 OPPO_MID = new IntVector2(GS.RES_X-GS.COL*7,GS.ROW*18);
        private static final IntVector2 OPPO_BOT = new IntVector2(GS.RES_X-GS.COL*1,GS.ROW*15);
        private static final IntVector2 OPPO_TOP = new IntVector2(GS.RES_X-GS.COL*13,GS.ROW*21);
    }
}