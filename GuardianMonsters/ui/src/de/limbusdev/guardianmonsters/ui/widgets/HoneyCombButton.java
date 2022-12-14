/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.guardians.Element;

/**
 * HoneyCombButton
 *
 * @author Georg Eckert 2018
 */

public class HoneyCombButton extends TextButton
{

    public static final int X = 0, Y = 1;
    public static final int LEFT=0, TOPLEFT=1, BOTTOMLEFT=2, CENTER=3, TOPRIGHT=4, BOTTOMRIGHT=5, RIGHT=6, CENTERTOP=7, CENTERBOTTOM=8;
    public static final int ALIGN = Align.bottomLeft;

    public static final int[][] SLOTS = {
        {29,17}, {101,33}, {101,1}, {173,17}, {245,33}, {245,1}, {317,17}, {173,49}, {173,1}
    };

    public HoneyCombButton(String text, Skin skin, int position, Element element)
    {
        super(text, skin, construct(position, element));
        setPosition(SLOTS[position][X], SLOTS[position][Y], ALIGN);
    }

    private static String construct(int position, Element element)
    {
        String style;

        switch(position)
        {
            case CENTERTOP:
                style = "b-attack-question-centertop";
                break;
            case CENTERBOTTOM:
                style = "tb-attack-none-centerbottom";
                break;
            default:
                if (element == null) element = Element.NONE;
                style = "tb-attack-" + element.toString().toLowerCase();
        }

        return style;
    }
}