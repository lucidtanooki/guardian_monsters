/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.services.Services;

import static de.limbusdev.guardianmonsters.ui.widgets.HoneyCombButton.BOTTOMLEFT;
import static de.limbusdev.guardianmonsters.ui.widgets.HoneyCombButton.BOTTOMRIGHT;
import static de.limbusdev.guardianmonsters.ui.widgets.HoneyCombButton.CENTER;
import static de.limbusdev.guardianmonsters.ui.widgets.HoneyCombButton.LEFT;
import static de.limbusdev.guardianmonsters.ui.widgets.HoneyCombButton.RIGHT;
import static de.limbusdev.guardianmonsters.ui.widgets.HoneyCombButton.TOPLEFT;
import static de.limbusdev.guardianmonsters.ui.widgets.HoneyCombButton.TOPRIGHT;

/**
 * HoneyComb7ButtonsWidget
 *
 * @author Georg Eckert 2018
 */

public class HoneyComb7ButtonsWidget extends WidgetGroup
{// Buttons
    private ArrayMap<Integer,TextButton> buttons;
    private Callback.ButtonID callbacks;
    protected Skin skin;

    public static final int[] ABILITY_ORDER = {5,3,1,0,4,2,6};

    public HoneyComb7ButtonsWidget (Skin skin, Callback.ButtonID callbacks, int[] buttonOrder) {

        super();
        this.skin = skin;

        if(buttonOrder.length < 7) {
            throw new IllegalArgumentException("buttonOrder must contain 7 values");
        }

        buttons = new ArrayMap<>();

        // Ability Buttons
        int[] positions = {
            LEFT,
            TOPLEFT,
            BOTTOMLEFT,
            CENTER,
            TOPRIGHT,
            BOTTOMRIGHT,
            RIGHT
        };

        TextButton tb;
        for(int i : positions)
        {
            tb = new HoneyCombButton("", skin, i, Element.NONE);
            buttons.put(buttonOrder[i],tb);
            addActor(tb);
        }

        this.callbacks = callbacks;
        initCallbackHandler();

    }

    private void initCallbackHandler()
    {
        for (int i = 0; i < 7; i++)
        {
            final int j = i;
            final TextButton attButt = buttons.get(i);
            attButt.addListener(new SimpleClickListener(() ->
            {
                System.out.println("SevenButtonsWidget: Clicked button " + j);
                if(!attButt.isDisabled()) {
                    callbacks.onClick(j);
                }
            }));
        }
    }

    public void setCallbacks(Callback.ButtonID callbacks)
    {
        this.callbacks = callbacks;
        initCallbackHandler();
    }

    protected void enableButton(int index) {
        buttons.get(index).setColor(Color.WHITE);
        buttons.get(index).setDisabled(false);
        buttons.get(index).setTouchable(Touchable.enabled);
    }

    protected void disableButton(int index)
    {
        buttons.get(index).setColor(Color.GRAY);
        buttons.get(index).setDisabled(true);
        buttons.get(index).setTouchable(Touchable.disabled);
    }

    public void setButtonText(int index, String text) {
        buttons.get(index).setText(text);
    }

    public void setButtonText(int index, Ability ability)
    {
        setButtonText(index, Services.getL18N().Abilities().get(ability.name));
    }

    public void setButtonStyle(int index, Skin skin, String style)
    {
        Button.ButtonStyle bs = skin.get(style, TextButton.TextButtonStyle.class);
        buttons.get(index).setStyle(bs);
    }

    public void setButtonStyle(int index, Element element)
    {
        Skin skin = Services.getUI().getBattleSkin();
        String styleString = "tb-attack-" + element.toString().toLowerCase();
        setButtonStyle(index, skin, styleString);
    }

    protected ArrayMap<Integer,TextButton> getButtons() {
        return buttons;
    }

    protected TextButton getButton(int index)
    {
        return buttons.get(index);
    }

    protected void replaceButton(final TextButton button, final int index)
    {
        Button removedButton = buttons.get(index);
        buttons.removeKey(index);
        button.setPosition(removedButton.getX(), removedButton.getY(), Align.bottomLeft);
        button.setScale(removedButton.getScaleX(),removedButton.getScaleY());
        button.setSize(removedButton.getWidth(),removedButton.getHeight());
        removedButton.remove();

        buttons.put(index,button);
        button.addListener(
            new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event,x,y);
                    System.out.println("SevenButtonsWidget: Clicked button " + index);
                    if(!button.isDisabled()) {
                        callbacks.onClick(index);
                    }
                }
            }
        );

        addActor(button);
    }
}