/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * SimpleClickListener
 *
 * @author Georg Eckert 2018
 */

public class SimpleClickListener extends ClickListener
{
    private Callback callback;

    public SimpleClickListener(Callback callback)
    {
        super();
        this.callback = callback;
    }

    @Override
    public void clicked(InputEvent event, float x, float y)
    {
        clicked();
    }

    private void clicked()
    {
        callback.onClick();
    }
}
