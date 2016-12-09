package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.data.AudioAssets;
import de.limbusdev.guardianmonsters.enums.AnimationType;
import de.limbusdev.guardianmonsters.enums.ButtonIDs;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.geometry.IntVector2;
import de.limbusdev.guardianmonsters.fwmengine.managers.Media;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */
public class BattleAnimationWidget extends BattleWidget implements ObservableWidget {

    private static boolean LEFT = true;
    private static boolean RIGHT = false;

    private Array<WidgetObserver> observers;

    private ArrayMap<Integer,Image> monsterImgsLeft, monsterImgsRight;
    private Media media;

    public boolean attackAnimationRunning;

    private CallbackHandler callbackHandler;

    public BattleAnimationWidget(final AHUD hud, CallbackHandler callbackHandler) {
        super(hud);

        observers = new Array<WidgetObserver>();
        this.monsterImgsLeft = new ArrayMap<Integer,Image>();
        this.monsterImgsRight = new ArrayMap<Integer,Image>();
        this.setBounds(0,0,0,0);
        this.media = Services.getMedia();

        this.callbackHandler = callbackHandler;

    }

    /**
     *
     * @param hero heros monsters
     * @param oppo opponents monsters
     */
    public void init(ArrayMap<Integer,Monster> hero, ArrayMap<Integer,Monster> oppo) {
        clear();

        monsterImgsLeft.clear();
        monsterImgsRight.clear();

        for(Integer key : hero.keys()) {
            Monster m = hero.get(key);
            setUpMonsterSprite(m.ID, key, true);
        }

        for(Integer key : oppo.keys()) {
            Monster m = oppo.get(key);
            setUpMonsterSprite(m.ID, key, false);
        }

        // Correct Image Sorting
        if(hero.containsKey(2) && hero.get(2).getHP() > 0) addActor(monsterImgsLeft.get(2));
        if(hero.containsKey(0) && hero.get(0).getHP() > 0) addActor(monsterImgsLeft.get(0));
        if(hero.containsKey(1) && hero.get(1).getHP() > 0) addActor(monsterImgsLeft.get(1));

        if(oppo.containsKey(2) && oppo.get(2).getHP() > 0) addActor(monsterImgsRight.get(2));
        if(oppo.containsKey(0) && oppo.get(0).getHP() > 0) addActor(monsterImgsRight.get(0));
        if(oppo.containsKey(1) && oppo.get(1).getHP() > 0) addActor(monsterImgsRight.get(1));
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
            align = Align.center;
            monsterImgsLeft.put(pos,monImg);
        } else {
            // Opponent Side
            switch(pos) {
                case 2:  position2d = ImPos.OPPO_TOP;break;
                case 1:  position2d = ImPos.OPPO_BOT;break;
                default: position2d = ImPos.OPPO_MID;break;
            }
            monsterImgsRight.put(pos,monImg);
            align = Align.center;
        }

        monImg.setPosition(position2d.x, position2d.y, align);
    }


    /**
     * Animate an attack of the given monster
     * @param attPos    position of attacker
     * @param defPos    position of defender
     * @param side      side of attacker
     */
    public void animateAttack(final int attPos, final int defPos, boolean side, final Attack attack) {
        Image attIm;
        final IntVector2 startPos,endPos;

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

        attIm.addAction(getAnimationSequence(attack,startPos,endPos, defPos));
    }


    /**
     * Assembles the animation action sequence
     * @param attack
     * @param origin    position of the attacks origin
     * @param target    position of the attacks target
     * @return
     */
    private Action getAnimationSequence(final Attack attack, final IntVector2 origin, final IntVector2 target, final int targetPos) {
        final boolean direction = origin.x > target.x;
        Action action;

        // Short delay before attack starts
        Action delayAction = Actions.delay(.5f);
        Action horMovingAttDelay = Actions.delay(1f);

        // Moves actor from origin to target
        Action moveToTargetAction = Actions.moveToAligned(target.x, target.y, Align.center, .6f, Interpolation.pow2In);

        // Moves actor back from target to origin
        Action moveToOriginAction = Actions.moveToAligned(origin.x, origin.y, Align.center, .4f, Interpolation.pow2In);

        // Plays the attack animatiom
        Action attackAnimationAction = Actions.run(new Runnable() {
            @Override
            public void run() {
                animateAttackOfType(attack, origin, target);
            }
        });

        // Plays the attacks sound
        Action playSFXAction = Actions.run(new Runnable() {
            @Override
            public void run() {
                Services.getAudio().playSound(AudioAssets.get().getBattleSFX(attack.sfxType,0));
            }
        });

        // Runs the callback handler
        Action callbackAction = Actions.run(new Runnable() {
            @Override
            public void run() {
                callbackHandler.onHitAnimationComplete();
            }
        });

        // Animates the impact of the attack on the target
        Action animateImpactAction = Actions.run(new Runnable() {
            @Override
            public void run() {
                animateAttackImpact(!direction, targetPos);
            }
        });

        switch(attack.animationType) {
            case CONTACT:
                action = Actions.sequence(delayAction,moveToTargetAction,attackAnimationAction,
                    playSFXAction,animateImpactAction,callbackAction,moveToOriginAction);
                break;
            case MOVING_HOR:
                action = Actions.sequence(delayAction, attackAnimationAction, horMovingAttDelay,
                    playSFXAction, animateImpactAction, callbackAction);
                break;
            case MOVING_VERT:
            default: // CONTACTLESS
                action = Actions.sequence(delayAction, attackAnimationAction, playSFXAction,
                    animateImpactAction, callbackAction);
                break;
        }

        return action;
    }

    /**
     * Animates the  impact of the attack
     * @param side
     * @param defPos
     */
    private void animateAttackImpact(boolean side, int defPos) {
        Image defIm;
        if(side) defIm = monsterImgsRight.get(defPos);
        else defIm = monsterImgsLeft.get(defPos);
        defIm.addAction(Actions.sequence(
            Actions.moveBy(0, 15, .1f, Interpolation.bounceIn),
            Actions.moveBy(0, -15, .1f, Interpolation.bounceIn)
        ));
    }

    public void animateMonsterKO(int pos, boolean side) {
        ArrayMap<Integer,Image> monImgs;
        if(side) monImgs = monsterImgsLeft;
        else monImgs = monsterImgsRight;

        monImgs.get(pos).addAction(Actions.sequence(Actions.alpha(0, 2), Actions.visible(false)));
    }

    /**
     * Takes an attack and starts the animation
     * @param attack
     */
    private void animateAttackOfType(Attack attack, IntVector2 origin, IntVector2 target) {

        boolean direction = origin.x > target.x;

        Animation anim = media.getAttackAnimation(attack.name);
        SelfRemovingAnimation sra = new SelfRemovingAnimation(anim);
        anim.setFrameDuration(.1f);
        // Attack direction
        if(direction == LEFT) {
            sra.setSize(256,256);
        } else {
            sra.setSize(-256,256); // flipped animation
        }
        sra.setAlign(Align.center);

        switch(attack.animationType) {
            case MOVING_HOR:
                anim.setFrameDuration(1f/anim.getKeyFrames().length);
                sra.setPosition(origin.x, origin.y, Align.center);
                sra.addAction(Actions.moveToAligned(target.x, target.y, Align.center, 1f, Interpolation.linear));
                break;
            case MOVING_VERT:
                anim.setFrameDuration(1f/anim.getKeyFrames().length);
                sra.setPosition(target.x, target.y + 256, Align.center);
                sra.addAction(Actions.moveToAligned(target.x, target.y, Align.center, 1f, Interpolation.pow2In));
                break;
            case CONTACT:
            default: // CONTACTLESS
                sra.setPosition(target.x, target.y, Align.center);
                break;
        }

        addActor(sra);
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
        private static final IntVector2 HERO_MID = new IntVector2(GS.COL*13+128,GS.ROW*18+128);
        private static final IntVector2 HERO_BOT = new IntVector2(GS.COL*7+128,GS.ROW*15+128);
        private static final IntVector2 HERO_TOP = new IntVector2(GS.COL*19+128,GS.ROW*21+128);
        private static final IntVector2 OPPO_MID = new IntVector2(GS.RES_X-GS.COL*7-128,GS.ROW*18+128);
        private static final IntVector2 OPPO_BOT = new IntVector2(GS.RES_X-GS.COL*1-128,GS.ROW*15+128);
        private static final IntVector2 OPPO_TOP = new IntVector2(GS.RES_X-GS.COL*13-128,GS.ROW*21+128);
    }

    public interface CallbackHandler {
        void onHitAnimationComplete();
    }

}
