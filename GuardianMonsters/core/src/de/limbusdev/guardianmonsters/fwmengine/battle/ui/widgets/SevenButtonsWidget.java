package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInformation;
import de.limbusdev.guardianmonsters.utils.GS;

public class SevenButtonsWidget extends BattleWidget {

    // Buttons
    private ArrayMap<Integer,TextButton> buttons;
    private CallbackHandler callbackHandler;

    public SevenButtonsWidget (final AHUD hud, Skin skin, CallbackHandler callbackHandler,
                               int[] buttonOrder) {

        super(hud);

        if(buttonOrder.length < 7) {
            throw new IllegalArgumentException("buttonOrder must contain 7 values");
        }

        buttons = new ArrayMap<Integer, TextButton>();

        // Attack Buttons
        TextButton tb = new TextButton("", skin, "tb-attack-none");
        tb.setSize(82*GS.zoom,32*GS.zoom);
        tb.setPosition(GS.RES_X/2,32*GS.zoom+1*GS.zoom,Align.center);
        buttons.put(buttonOrder[3],tb);

        tb = new TextButton("", skin, "tb-attack-none");
        tb.setSize(82*GS.zoom,32*GS.zoom);
        tb.setPosition(GS.RES_X/2-71*GS.zoom,16*GS.zoom+1*GS.zoom,Align.center);
        buttons.put(buttonOrder[2],tb);

        tb = new TextButton("", skin, "tb-attack-none");
        tb.setSize(82*GS.zoom,32*GS.zoom);
        tb.setPosition(GS.RES_X/2+71*GS.zoom,16*GS.zoom+1*GS.zoom,Align.center);
        buttons.put(buttonOrder[5],tb);

        tb = new TextButton("", skin, "tb-attack-none");
        tb.setSize(82*GS.zoom,32*GS.zoom);
        tb.setPosition(GS.RES_X/2-71*GS.zoom,(32+16)*GS.zoom+1*GS.zoom,Align.center);
        buttons.put(buttonOrder[1],tb);

        tb = new TextButton("", skin, "tb-attack-none");
        tb.setSize(82*GS.zoom,32*GS.zoom);
        tb.setPosition(GS.RES_X/2+71*GS.zoom,(32+16)*GS.zoom+1*GS.zoom,Align.center);
        buttons.put(buttonOrder[4],tb);

        tb = new TextButton("", skin, "tb-attack-none");
        tb.setSize(82*GS.zoom,32*GS.zoom);
        tb.setPosition(GS.RES_X/2-(71+71)*GS.zoom,32*GS.zoom+1*GS.zoom,Align.center);
        buttons.put(buttonOrder[0],tb);

        tb = new TextButton("", skin, "tb-attack-none");
        tb.setSize(82*GS.zoom,32*GS.zoom);
        tb.setPosition(GS.RES_X/2+(71+71)*GS.zoom,32*GS.zoom+1*GS.zoom,Align.center);
        buttons.put(buttonOrder[6],tb);

        this.callbackHandler = callbackHandler;
        initCallbackHandler();

        for(TextButton b : buttons.values()) {
            addActor(b);
        }

    }

    private void initCallbackHandler() {
        for (int i = 0; i < 7; i++) {
            final int j = i;
            final TextButton attButt = buttons.get(i);
            attButt.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event,x,y);
                        System.out.println("SevenButtonsWidget: Clicked button " + j);
                        if(!attButt.isDisabled()) {
                            callbackHandler.onButtonNr(j);
                        }
                    }
                }
            );
        }
    }

    public void setCallbackHandler(CallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
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

    protected ArrayMap<Integer,TextButton> getButtons() {
        return buttons;
    }

    protected TextButton getButton(int index) {
        return buttons.get(index);
    }


    // INNER INTERFACE
    public interface CallbackHandler {
        public void onButtonNr(int nr);
    }
}
