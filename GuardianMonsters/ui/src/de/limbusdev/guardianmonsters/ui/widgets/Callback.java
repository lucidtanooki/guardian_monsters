/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.ui.widgets;

/**
 * Callback
 *
 * @author Georg Eckert 2018
 */

public interface Callback
{
    void onClick();

    interface SingleInt
    {
        void onClick(int key);
    }

    interface ButtonID
    {
        void onClick(int buttonID);
    }

    interface Boolean
    {
        void onBool(boolean bool);
    }
}
