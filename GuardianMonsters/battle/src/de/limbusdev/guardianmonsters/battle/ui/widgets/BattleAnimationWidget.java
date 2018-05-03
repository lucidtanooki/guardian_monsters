/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.battle.ui.widgets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.assets.paths.AssetPath;
import de.limbusdev.guardianmonsters.battle.AbilityMedia;
import de.limbusdev.guardianmonsters.battle.AbilityMediaDB;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.battle.BattleQueue;
import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.Constant;
import de.limbusdev.guardianmonsters.ui.widgets.Callback;
import de.limbusdev.guardianmonsters.utils.geometry.IntVec2;

import static de.limbusdev.guardianmonsters.guardians.Constant.LEFT;
import static de.limbusdev.guardianmonsters.guardians.Constant.RIGHT;

/**
 * HINT: Don't forget calling the init() method
 *
 * @author Georg Eckert 2016
 */
public class BattleAnimationWidget extends BattleWidget
{
    private Array<WidgetObserver> observers;
    private ArrayMap<Integer,Boolean> leftPositionsOccupied, rightPositionsOccupied;

    private ArrayMap<Integer,BattleGuardianWidget> monsterImgsLeft, monsterImgsRight;
    private Array<BattleGuardianWidget> zSortedMonsterImgs;
    private ArrayMap<Integer, Animation<TextureAtlas.AtlasRegion>>
        statusEffectIndicatorsLeft, statusEffectIndicatorsRight;

    public boolean attackAnimationRunning;
    public boolean replacingDefeatedAnimationRunning;

    private Callback onHitAnimationComplete, onDieing, onDoingNothing;

    public BattleAnimationWidget(Callback onHitAnimationComplete, Callback onDieing, Callback onDoingNothing)
    {
        super();

        this.onHitAnimationComplete = onHitAnimationComplete;
        this.onDieing = onDieing;
        this.onDoingNothing = onDoingNothing;

        attackAnimationRunning = false;

        leftPositionsOccupied = new ArrayMap<>();
        rightPositionsOccupied = new ArrayMap<>();

        observers = new Array<>();
        this.monsterImgsLeft = new ArrayMap<>();
        this.monsterImgsRight = new ArrayMap<>();
        this.zSortedMonsterImgs = new Array<>();
        this.setBounds(0,0,0,0);

        // Status Effect Animations
        {
            this.statusEffectIndicatorsLeft = new ArrayMap<>();
            this.statusEffectIndicatorsRight = new ArrayMap<>();

            for (int i = 0; i <= 2; i++)
            {
                // left
                statusEffectIndicatorsLeft.put(i, Services.getMedia().getStatusEffectAnimation(
                    IndividualStatistics.StatusEffect.HEALTHY.toString().toLowerCase()
                ));
                // right
                statusEffectIndicatorsRight.put(i, Services.getMedia().getStatusEffectAnimation(
                    IndividualStatistics.StatusEffect.HEALTHY.toString().toLowerCase()
                ));
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha)
    {
        sortMonsterSpritesByDepth();
        super.draw(batch, parentAlpha);
    }

    private void sortMonsterSpritesByDepth()
    {
        zSortedMonsterImgs.sort(new BattleGuardianWidget.ZComparator());
        for(BattleGuardianWidget i : zSortedMonsterImgs) { i.setZIndex(zSortedMonsterImgs.indexOf(i,true)); }
    }

    public void init(BattleSystem battleSystem)
    {
        clear();
        zSortedMonsterImgs.clear();
        BattleQueue queue = battleSystem.getQueue();
        addMonsterAnimationsForTeam(queue.getCombatTeamLeft(),LEFT);
        addMonsterAnimationsForTeam(queue.getCombatTeamRight(),RIGHT);
    }

    private void addMonsterAnimationsForTeam(ArrayMap<Integer,AGuardian> team, boolean side)
    {
        ArrayMap<Integer,BattleGuardianWidget> imgs;
        ArrayMap<Integer,Boolean> positions;

        if(side == LEFT) {
            imgs = monsterImgsLeft;
            positions = leftPositionsOccupied;
        } else /* (side == RIGHT) */ {
            imgs = monsterImgsRight;
            positions = rightPositionsOccupied;
        }

        positions.clear();
        imgs.clear();

        // Not more than 3 monsters can join a fight
        int teamSize = team.size > 3 ? 3 : team.size;
        int counter = 0;
        int actualTeamSize = 0;

        while(actualTeamSize < teamSize && counter < team.size)
        {
            AGuardian m = team.get(counter);
            if(m.getIndividualStatistics().isFit()) {
                // Add monster to team
                setUpMonsterSprite(
                    m,
                    m.getSpeciesDescription().getID(),
                    m.getAbilityGraph().getCurrentForm(),
                    actualTeamSize,
                    side
                );
                positions.put(actualTeamSize,true);
                actualTeamSize++;
            }
            counter++;
        }

        // Correct Image Depth Sorting
        if(positions.containsKey(2)) { addActor(imgs.get(2)); }
        if(positions.containsKey(1)) { addActor(imgs.get(1)); }
        if(positions.containsKey(0)) { addActor(imgs.get(0)); }
    }

    /**
     * Adds Monster sprite at the given battle arena position
     * @param id
     * @param pos
     * @param side
     */
    private void setUpMonsterSprite(AGuardian guardian, int id, int metaForm, int pos, boolean side)
    {
        BattleGuardianWidget monImg = new BattleGuardianWidget(id, metaForm, side);
        guardian.addObserver(monImg);

        IntVec2 position2d;
        int align;
        if(side) {
            // Hero Side
            switch(pos) {
                case 2:  position2d = ImPos.HERO_TOP;break;
                case 1:  position2d = ImPos.HERO_BOT;break;
                default: position2d = ImPos.HERO_MID;break;
            }
            align = Align.bottom;
            monsterImgsLeft.put(pos,monImg);
        } else {
            // Opponent Side
            switch(pos) {
                case 2:  position2d = ImPos.OPPO_TOP;break;
                case 1:  position2d = ImPos.OPPO_BOT;break;
                default: position2d = ImPos.OPPO_MID;break;
            }
            monsterImgsRight.put(pos,monImg);
            align = Align.bottom;
        }

        zSortedMonsterImgs.add(monImg);

        monImg.setPosition(position2d.x, position2d.y, align);
    }


    public void animateSelfDefense()
    {
        onHitAnimationComplete.onClick();
    }

    public void animateItemUsage() {
        onDoingNothing.onClick();
    }

    /**
     * Animate an ability of the given monster
     * @param attPos    position of attacker
     * @param defPos    position of defender
     * @param attSide      side of attacker
     * @param defSide   side of target
     */
    public void animateAttack(
        final int attPos,
        final int defPos,
        boolean attSide,
        boolean defSide,
        final Ability ability)
    {
        BattleGuardianWidget attIm;
        final IntVec2 startPos,endPos;

        attackAnimationRunning = true;

        // Get Position of attacking monster
        if(attSide == LEFT)
            switch(attPos) {
                case 2:  startPos = ImPos.HERO_TOP;break;
                case 1:  startPos = ImPos.HERO_BOT;break;
                default: startPos = ImPos.HERO_MID;break;
            }
        else
            switch(attPos) {
                case 2: startPos = ImPos.OPPO_TOP; break;
                case 1: startPos = ImPos.OPPO_BOT; break;
                default:startPos = ImPos.OPPO_MID; break;
            }

        // Get position of target monster
        if(defSide == RIGHT) {
            switch (defPos) {
                case 2:
                    endPos = ImPos.OPPO_TOP;
                    break;
                case 1:
                    endPos = ImPos.OPPO_BOT;
                    break;
                default:
                    endPos = ImPos.OPPO_MID;
                    break;
            }
        } else {
            switch (defPos) {
                case 2:
                    endPos = ImPos.HERO_TOP;
                    break;
                case 1:
                    endPos = ImPos.HERO_BOT;
                    break;
                default:
                    endPos = ImPos.HERO_MID;
                    break;
            }
        }


        if(attSide == LEFT) attIm = monsterImgsLeft.get(attPos);
        else attIm = monsterImgsRight.get(attPos);

        attIm.addAction(getAnimationSequence(ability, startPos, endPos, defPos, attSide, defSide));
    }

    public void animateAreaAttack(final int attPos, boolean attSide, boolean defSide, final Ability ability)
    {
        BattleGuardianWidget attIm;
        if(attSide == LEFT) attIm = monsterImgsLeft.get(attPos);
        else attIm = monsterImgsRight.get(attPos);

        attIm.addAction(getAreaAttackAnimationSequence(ability, attSide, defSide));
    }


    /**
     * Assembles the animation action sequence
     * @param ability
     * @param origin    position of the attacks origin
     * @param target    position of the attacks target
     * @return
     */
    private Action getAnimationSequence(
        final Ability ability, final IntVec2 origin, final IntVec2 target, final int targetPos,
        boolean side, final boolean defSide)
    {
        final boolean direction = defSide;
        Action action;
        AbilityMedia abilityMedia = AbilityMediaDB.getInstance().getAbilityMedia(ability.name);

        // Short delay before ability starts
        Action delayAction = Actions.delay(.5f);
        Action horMovingAttDelay = Actions.delay(1f);

        // Moves actor from origin to target
        Action moveToTargetAction = Actions.moveToAligned(target.x, target.y, Align.bottom, .6f, Interpolation.pow2In);

        // Moves actor back from target to origin
        Action moveToOriginAction = Actions.moveToAligned(origin.x, origin.y, Align.bottom, .4f, Interpolation.pow2In);

        // Plays the ability animatiom
        Action attackAnimationAction = Actions.run(() -> animateAttackOfType(ability, origin, target));

        // Plays the attacks sound
        final String path = AssetPath.Audio.SFX.BATTLE().get(abilityMedia.getSfxType().toString().toUpperCase()).get(0);
        Action playSFXAction = Actions.run(() -> Services.getAudio().playSound(path));

        // Runs the callback handler
        Action callbackAction = Actions.run(() -> {
            onHitAnimationComplete.onClick();
        });

        // Animates the impact of the ability on the target
        Action animateImpactAction = Actions.run(() -> animateAttackImpact(targetPos, defSide));

        switch(abilityMedia.getAnimationType()) {
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

    private Action getAreaAttackAnimationSequence(
        final Ability ability, boolean side, final boolean defSide)
    {
        final boolean direction = defSide;
        Action action;
        AbilityMedia abilityMedia = AbilityMediaDB.getInstance().getAbilityMedia(ability.name);

        // Short delay before ability starts
        Action delayAction = Actions.delay(.5f);

        // Plays the ability animatiom
        Action attackAnimationAction = Actions.run(() -> animateAreaAttackOfType(ability, defSide));

        // Plays the attacks sound
        final String path = AssetPath.Audio.SFX.BATTLE().get(abilityMedia.getSfxType().toString().toUpperCase()).get(0);
        Action playSFXAction = Actions.run(() -> Services.getAudio().playSound(path));

        // Runs the callback handler
        Action callbackAction = Actions.run(() -> onHitAnimationComplete.onClick());

        // Animates the impact of the ability on the target
        Action animateImpactAction = Actions.run(() -> animateAreaAttackImpact(defSide));

        action = Actions.sequence(delayAction, attackAnimationAction, playSFXAction,
                    animateImpactAction, callbackAction);

        return action;
    }

    /**
     * Animates the  impact of the attack
     * @param side
     * @param defPos
     */
    private void animateAttackImpact(int defPos, boolean side)
    {
        BattleGuardianWidget defIm;
        if(side == LEFT) defIm = monsterImgsLeft.get(defPos);
        else defIm = monsterImgsRight.get(defPos);

        defIm.addAction(Actions.sequence(
            Actions.moveBy(0, 15, .1f, Interpolation.bounceIn),
            Actions.moveBy(0, -15, .1f, Interpolation.bounceIn)
        ));
    }

    /**
     * Animates the impact of an area attack for a whole side.
     * @param side
     */
    private void animateAreaAttackImpact(boolean side)
    {
        ArrayMap<Integer, BattleGuardianWidget> monsterImgs;

        if(side == LEFT) monsterImgs = monsterImgsLeft;
        else             monsterImgs = monsterImgsRight;

        for(BattleGuardianWidget defImg : monsterImgs.values())
        {
            defImg.addAction(Actions.sequence(
                Actions.moveBy(0, 15, .1f, Interpolation.bounceIn),
                Actions.moveBy(0, -15, .1f, Interpolation.bounceIn)
            ));
        }
    }

    public void animateMonsterKO(int pos, boolean side)
    {
        ArrayMap<Integer,BattleGuardianWidget> monImgs;
        if(side == LEFT) monImgs = monsterImgsLeft;
        else monImgs = monsterImgsRight;

        monImgs.get(pos).die(side, onDieing);
    }

    public void animateGuardianSubstitution(
        int pos, boolean side, int substitutesID, int substitutesForm,
        Callback onSubstitutionComplete, AGuardian substituted, AGuardian substitute)
    {
        ArrayMap<Integer,BattleGuardianWidget> monImgs;
        if(side == LEFT) { monImgs = monsterImgsLeft; }
        else             { monImgs = monsterImgsRight; }

        BattleGuardianWidget guardianWidget = monImgs.get(pos);
        substitute.addObserver(guardianWidget);
        guardianWidget.substitute(substitutesID, substitutesForm, side, onSubstitutionComplete::onClick);
    }

    public void animateReplacingDefeatedGuardian(
        int pos, boolean side, int substitutesID, int substitutesForm,
        Callback onSubstitutionComplete, AGuardian substituted, AGuardian substitute)
    {
        ArrayMap<Integer,BattleGuardianWidget> monImgs;
        if(side == LEFT) { monImgs = monsterImgsLeft; }
        else             { monImgs = monsterImgsRight; }

        BattleGuardianWidget guardianWidget = monImgs.get(pos);
        substitute.addObserver(guardianWidget);
        guardianWidget.replaceDefeated(substitutesID, substitutesForm, side, onSubstitutionComplete::onClick);
    }

    /**
     * Takes an ability and starts the animation
     * @param ability
     */
    private void animateAttackOfType(Ability ability, IntVec2 origin, IntVec2 target)
    {

        boolean direction = origin.x > target.x;

        Animation anim = Services.getMedia().getAttackAnimation(ability.name);
        SelfRemovingAnimation sra = new SelfRemovingAnimation(anim);
        anim.setFrameDuration(.1f);
        // Ability direction
        if(direction == LEFT) {
            sra.setSize(128,128);
        } else {
            sra.setSize(-128,128); // flipped animation
        }
        sra.setAlign(Align.bottom);

        AbilityMedia abilityMedia = AbilityMediaDB.getInstance().getAbilityMedia(ability.name);

        switch(abilityMedia.getAnimationType())
        {
            case MOVING_HOR:
                anim.setFrameDuration(1f/anim.getKeyFrames().length);
                sra.setPosition(origin.x, origin.y, Align.bottom);
                sra.addAction(Actions.moveToAligned(target.x, target.y, Align.bottom, 1f, Interpolation.linear));
                break;
            case MOVING_VERT:
                anim.setFrameDuration(1f/anim.getKeyFrames().length);
                sra.setPosition(target.x, target.y + 128, Align.bottom);
                sra.addAction(Actions.moveToAligned(target.x, target.y, Align.bottom, 1f, Interpolation.pow2In));
                break;
            case CONTACT:
            default: // CONTACTLESS
                sra.setPosition(target.x, target.y, Align.bottom);
                break;
        }

        addActor(sra);
    }

    private void animateAreaAttackOfType(Ability ability, boolean defSide)
    {
        boolean direction = defSide;

        Animation anim = Services.getMedia().getAttackAnimation(ability.name);
        SelfRemovingAnimation sra = new SelfRemovingAnimation(anim);
        anim.setFrameDuration(.1f);
        // Ability direction
        if(direction == LEFT) {
            sra.setSize(256,192);
        } else {
            sra.setSize(-256,192); // flipped animation
        }
        sra.setAlign(Align.bottom);

        AbilityMedia abilityMedia = AbilityMediaDB.getInstance().getAbilityMedia(ability.name);

        IntVec2 target = direction ? (new IntVec2(128,100)) : (new IntVec2(Constant.WIDTH+64,100));

        sra.setPosition(target.x, target.y, Align.bottom);

        addActor(sra);
    }

    private final static class ImPos
    {
        private static final IntVec2 HERO_MID = new IntVec2(32+64+64, 100+24);
        private static final IntVec2 HERO_BOT = new IntVec2(32+16+64, 100);
        private static final IntVec2 HERO_TOP = new IntVec2(32+112+64, 100+48);
        private static final IntVec2 OPPO_MID = new IntVec2(640+32-HERO_MID.x,HERO_MID.y);
        private static final IntVec2 OPPO_BOT = new IntVec2(640+32-HERO_BOT.x,HERO_BOT.y);
        private static final IntVec2 OPPO_TOP = new IntVec2(640+32-HERO_TOP.x,HERO_TOP.y);
    }


}
