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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

import java.util.Comparator;
import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;
import de.limbusdev.guardianmonsters.services.Services;

import static de.limbusdev.guardianmonsters.guardians.Constant.LEFT;
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
        TextureRegion monReg;
        monReg = Services.getMedia().getMonsterSprite(index, metaForm);
        if(side == LEFT)
            if(!monReg.isFlipX())
                monReg.flip(true, false);
        guardianImage = new Image(monReg);
        guardianImage.setPosition(0,0, Align.bottom);
        addActor(guardianImage);

        Animation anim = Services.getMedia().getStatusEffectAnimation(HEALTHY.toString().toLowerCase());
        statusEffectAnimation = new AnimatedImage(anim);
        addActor(statusEffectAnimation);
        statusEffectAnimation.setPosition(0,96,Align.top);
    }

    public void setStatusEffect(IndividualStatistics.StatusEffect statusEffect)
    {
        Animation anim = Services.getMedia().getStatusEffectAnimation(statusEffect.toString().toLowerCase());
        statusEffectAnimation.setAnimation(anim);
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
