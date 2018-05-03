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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.widgets.Callback;

import static de.limbusdev.guardianmonsters.guardians.Constant.LEFT;
import static de.limbusdev.guardianmonsters.guardians.Constant.RIGHT;
import static de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics.StatusEffect.HEALTHY;

/**
 * BattleGuardianWidget
 *
 * @author Georg Eckert 2018
 */

public class BattleGuardianWidget extends BattleWidget implements Observer
{
    private Image guardianImage;
    private AnimatedImage statusEffectAnimation;

    public BattleGuardianWidget(int index, int metaForm, boolean side)
    {
        guardianImage = new Image();
        guardianImage.setSize(128,128);
        guardianImage.setPosition(0,0, Align.bottom);
        addActor(guardianImage);

        Animation anim = Services.getMedia().getStatusEffectAnimation(HEALTHY.toString().toLowerCase());
        statusEffectAnimation = new AnimatedImage(anim);
        addActor(statusEffectAnimation);
        statusEffectAnimation.setPosition(0,96,Align.top);

        init(index, metaForm, side);
    }

    private void init(int index, int metaForm, boolean side)
    {
        TextureRegion monReg;
        monReg = Services.getMedia().getMonsterSprite(index, metaForm);
        if(side == LEFT)
            if(!monReg.isFlipX())
                monReg.flip(true, false);
        TextureRegionDrawable drawable = new TextureRegionDrawable(monReg);
        guardianImage.setDrawable(drawable);
    }

    public void setStatusEffect(IndividualStatistics.StatusEffect statusEffect)
    {
        Animation anim = Services.getMedia().getStatusEffectAnimation(statusEffect.toString().toLowerCase());
        statusEffectAnimation.setAnimation(anim);
    }

    public void substitute(int index, int metaForm, boolean side, Callback callback)
    {
        Animation anim = Services.getMedia().getBanningAnimation();
        SelfRemovingAnimation sra = new SelfRemovingAnimation(anim);
        sra.setPosition(0,0,Align.bottom);
        addActor(sra);
        guardianImage.addAction(Actions.sequence(
            Actions.delay(1f),
            Actions.visible(false),
            Actions.run(() -> {
                Animation anim2 = Services.getMedia().getSummoningAnimation();
                SelfRemovingAnimation sra2 = new SelfRemovingAnimation(anim2);
                sra2.setPosition(0,0,Align.bottom);
                addActor(sra2);
            }),
            Actions.delay(1f),
            Actions.run(() -> {init(index, metaForm, side);}),
            Actions.visible(true),
            Actions.delay(1f),
            Actions.run(callback::onClick)
        ));
    }

    public void replaceDefeated(int index, int metaForm, boolean side, Callback callback)
    {
        Animation anim = Services.getMedia().getSummoningAnimation();
        SelfRemovingAnimation sra = new SelfRemovingAnimation(anim);

        sra.setPosition(0,0,Align.bottom);

        guardianImage.addAction(Actions.sequence(
            Actions.fadeOut(1f),
            Actions.run(() -> {
                TextureRegion tombStoneDrawable = Services.getUI().getBattleSkin().getRegion("tomb-stone");
                if (side == RIGHT) {
                    tombStoneDrawable.flip(true, false);
                }
                guardianImage.setDrawable(new TextureRegionDrawable(tombStoneDrawable));
            }),
            Actions.fadeIn(1f),
            Actions.run(() -> addActor(sra)),
            Actions.delay(1f),
            Actions.run(() -> {init(index, metaForm, side);}),
            Actions.delay(1f),
            Actions.run(callback::onClick)
        ));
    }

    public void die(boolean side)
    {
        if(side == LEFT) {
            guardianImage.addAction(Actions.sequence(
                Actions.alpha(0f, 2f),
                Actions.visible(false),
                Actions.run(() -> {
                    guardianImage.setDrawable(
                        Services.getUI().getBattleSkin().getDrawable("tomb-stone"));
                    if (side == RIGHT) {
                        guardianImage.setScaleX(-1f);
                    }
                }),
                Actions.visible(true),
                Actions.alpha(1f, 2f)
            ));
        } else /* side == RIGHT */ {
            guardianImage.addAction(Actions.sequence(Actions.fadeOut(2f), Actions.visible(false)));
        }
    }

    @Override
    public void update(Observable observable, Object o)
    {
        if(observable instanceof AGuardian) {
            AGuardian guardian = (AGuardian) observable;
            setStatusEffect(guardian.getIndividualStatistics().getStatusEffect());
        }
    }

    public static class ZComparator implements Comparator<BattleGuardianWidget>
    {
        public static final int SMALLER = -1;
        public static final int BIGGER = 1;
        public static final int EQUAL = 0;

        @Override
        public int compare(BattleGuardianWidget t0, BattleGuardianWidget t1)
        {
            if(t0.getY() > t1.getY()) {
                return SMALLER;
            } else if (t0.getY() < t1.getY()) {
                return BIGGER;
            }else {
                return EQUAL;
            }
        }
    }
}
