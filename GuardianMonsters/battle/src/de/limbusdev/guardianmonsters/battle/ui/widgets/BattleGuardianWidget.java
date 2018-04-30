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
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

import java.util.Comparator;

import de.limbusdev.guardianmonsters.services.Services;

import static de.limbusdev.guardianmonsters.guardians.Constant.LEFT;

/**
 * BattleGuardianWidget
 *
 * @author Georg Eckert 2018
 */

public class BattleGuardianWidget extends BattleWidget
{
    private Image guardianImage;
    private Animation<TextureAtlas.AtlasRegion> statusEffectAnimation;

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
