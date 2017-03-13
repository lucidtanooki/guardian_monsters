package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.enums.Element;

import static de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleHUDTextButton.BOTTOMLEFT;
import static de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleHUDTextButton.BOTTOMRIGHT;
import static de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleHUDTextButton.CENTER;
import static de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleHUDTextButton.LEFT;
import static de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleHUDTextButton.RIGHT;
import static de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleHUDTextButton.TOPLEFT;
import static de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.BattleHUDTextButton.TOPRIGHT;

public class SevenButtonsWidget extends BattleWidget {

    // Buttons
    private ArrayMap<Integer,TextButton> buttons;
    private ClickListener clickListener;
    protected Skin skin;

    public static final int[] ABILITY_ORDER = {5,3,1,0,4,2,6};

    public SevenButtonsWidget (Skin skin, ClickListener clickListener, int[] buttonOrder) {

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
        for(int i : positions) {
            tb = new BattleHUDTextButton("", skin, i, Element.NONE);
            buttons.put(buttonOrder[i],tb);
            addActor(tb);
        }

        this.clickListener = clickListener;
        initCallbackHandler();

    }

    private void initCallbackHandler() {
        for (int i = 0; i < 7; i++) {
            final int j = i;
            final TextButton attButt = buttons.get(i);
            attButt.addListener(
                new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event,x,y);
                        System.out.println("SevenButtonsWidget: Clicked button " + j);
                        if(!attButt.isDisabled()) {
                            clickListener.onButtonNr(j);
                        }
                    }
                }
            );
        }
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    protected void enableButton(int index) {
        buttons.get(index).setColor(Color.WHITE);
        buttons.get(index).setDisabled(false);
        buttons.get(index).setTouchable(Touchable.enabled);
    }

    protected void disableButton(int index) {
        buttons.get(index).setColor(Color.GRAY);
        buttons.get(index).setDisabled(true);
        buttons.get(index).setTouchable(Touchable.disabled);
    }

    public void setButtonText(int index, String text) {
        buttons.get(index).setText(text);
    }

    public void setButtonStyle(int index, Skin skin, String style) {
        Button.ButtonStyle bs = skin.get(style, TextButton.TextButtonStyle.class);
        buttons.get(index).setStyle(bs);
    }

    protected ArrayMap<Integer,TextButton> getButtons() {
        return buttons;
    }

    protected TextButton getButton(int index) {
        return buttons.get(index);
    }

    protected void replaceButton(final TextButton button, final int index) {
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
                    if(button.isDisabled()) {
                        clickListener.onButtonNr(index);
                    }
                }
            }
        );

        addActor(button);
    }


    // INNER INTERFACE
    public interface ClickListener {
        void onButtonNr(int nr);
    }
}
