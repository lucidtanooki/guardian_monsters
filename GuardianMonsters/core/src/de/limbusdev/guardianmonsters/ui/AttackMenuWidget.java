package de.limbusdev.guardianmonsters.ui;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
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
public class AttackMenuWidget extends WidgetGroup {

    // Buttons
    private Array<TextButton> attackButtons;

    private Array<WidgetObserver> observers;

    private Image bgImg;


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


        for(int i=0; i<6; i++) {
                TextButton attButt = new TextButton("attack " + i, skin, "tb-att");
                attButt.setSize(264, 100);
                attackButtons.add(attButt);
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

    public void addListenerToAllButtons(ClickListener cl) {
        for(TextButton b : attackButtons) b.addListener(cl);
    }

    public void addListenerToButton(int i, ClickListener cl) {
        attackButtons.get(i).addListener(cl);
    }


    public void addFadeOutAction(float duration) {
        addAction(Actions.sequence(Actions.alpha(0, duration), Actions.visible(false)));
    }

    public void addFadeInAction(float duration) {
        addAction(Actions.sequence(Actions.visible(true), Actions.alpha(1, duration)));
    }
}
