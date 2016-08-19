package de.limbusdev.guardianmonsters.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */
public class AttackMenuWidget extends BattleWidget implements ObservableWidget {

    // Buttons
    private Array<TextButton> attackButtons;

    private Array<WidgetObserver> observers;

    private Image bgImg;

    public int chosenAttack=0;


    /**
     *
     * @param skin battle action UI skin
     */
    public AttackMenuWidget(Skin skin) {
        super();

        observers = new Array<WidgetObserver>();

        this.setBounds(0,0,GS.RES_X,GS.RES_Y/4);

        bgImg = new Image(skin.getDrawable("attPane"));
        bgImg.setPosition(GS.RES_X/2, 0, Align.bottom);
        addActor(bgImg);

        attackButtons = new Array<TextButton>();


        for (int i = 0; i < 6; i++) {
            final int j = i;
            TextButton attButt = new TextButton("attack " + i, skin, "tb-att");
            attButt.setSize(264, 100);
            attackButtons.add(attButt);
            attButt.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        chosenAttack = j;
                        notifyWidgetObservers();
                    }
                }
            );
        }


        for(TextButton t : attackButtons) {
            addActor(t);
            t.setVisible(false);
        }

        attackButtons.get(0).setPosition(72+168,112,Align.bottomLeft);
        attackButtons.get(1).setPosition(72+168+268,112,Align.bottomLeft);
        attackButtons.get(2).setPosition(72+168+268+268,112,Align.bottomLeft);
        attackButtons.get(3).setPosition(72+168,12,Align.bottomLeft);
        attackButtons.get(4).setPosition(72+168+268,12,Align.bottomLeft);
        attackButtons.get(5).setPosition(72+168+268+268,12,Align.bottomLeft);
    }

    public void init(Array<Attack> attacks) {
        int i = 0;
        Iterator<Attack> attIt = attacks.iterator();
        while(attIt.hasNext() && i<6) {
            Attack att = attIt.next();
            attackButtons.get(i).setVisible(true);
            attackButtons.get(i).setText(att.name + " (" + att.damage + ")");
            i++;
        }
    }


    @Override
    public void addWidgetObserver(WidgetObserver wo) {
        observers.add(wo);
    }

    @Override
    public void notifyWidgetObservers() {
        for(WidgetObserver wo : observers) wo.getNotified(this);
    }
}
