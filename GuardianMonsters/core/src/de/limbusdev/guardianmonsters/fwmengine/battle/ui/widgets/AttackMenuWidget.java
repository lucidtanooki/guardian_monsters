package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Attack;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 *
 * If the return value is negative, user touched the back button
 * Created by georg on 03.07.16.
 */
public class AttackMenuWidget extends BattleWidget implements ObservableWidget {

    // Buttons
    private Array<TextButton> attackButtons;
    private ImageButton backButton;

    private Array<WidgetObserver> observers;

    private Image bgImg;

    public int chosenAttack=0;


    /**
     *
     * @param skin battle action UI skin
     */
    public AttackMenuWidget(final AHUD hud, Skin skin) {
        super(hud);

        observers = new Array<WidgetObserver>();

        this.setBounds(0,0,GS.RES_X,GS.RES_Y/4);

        bgImg = new Image(skin.getDrawable("attPane"));
        bgImg.setPosition(GS.RES_X/2, 0, Align.bottom);
        //addActor(bgImg);

        attackButtons = new Array<TextButton>();


        // Attack Buttons
        TextButton tb = new TextButton("Attack 1", skin, "tb-attack-none");
        tb.setSize(82*GS.zoom,32*GS.zoom);
        tb.setPosition(GS.RES_X/2,32*GS.zoom+1*GS.zoom,Align.center);
        addActor(tb);
        attackButtons.add(tb);

        tb = new TextButton("Attack 3", skin, "tb-attack-none");
        tb.setSize(82*GS.zoom,32*GS.zoom);
        tb.setPosition(GS.RES_X/2+71*GS.zoom,16*GS.zoom+1*GS.zoom,Align.center);
        addActor(tb);
        attackButtons.add(tb);

        tb = new TextButton("Attack 5", skin, "tb-attack-none");
        tb.setSize(82*GS.zoom,32*GS.zoom);
        tb.setPosition(GS.RES_X/2+71*GS.zoom,(32+16)*GS.zoom+1*GS.zoom,Align.center);
        addActor(tb);
        attackButtons.add(tb);

        tb = new TextButton("Attack 2", skin, "tb-attack-none");
        tb.setSize(82*GS.zoom,32*GS.zoom);
        tb.setPosition(GS.RES_X/2-71*GS.zoom,16*GS.zoom+1*GS.zoom,Align.center);
        addActor(tb);
        attackButtons.add(tb);

        tb = new TextButton("Attack 4", skin, "tb-attack-none");
        tb.setSize(82*GS.zoom,32*GS.zoom);
        tb.setPosition(GS.RES_X/2-71*GS.zoom,(32+16)*GS.zoom+1*GS.zoom,Align.center);
        addActor(tb);
        attackButtons.add(tb);

        tb = new TextButton("Attack 6", skin, "tb-attack-none");
        tb.setSize(82*GS.zoom,32*GS.zoom);
        tb.setPosition(GS.RES_X/2-(71+71)*GS.zoom,32*GS.zoom+1*GS.zoom,Align.center);
        addActor(tb);
        attackButtons.add(tb);

        tb = new TextButton("Attack 7", skin, "tb-attack-none");
        tb.setSize(82*GS.zoom,32*GS.zoom);
        tb.setPosition(GS.RES_X/2+(71+71)*GS.zoom,32*GS.zoom+1*GS.zoom,Align.center);
        addActor(tb);
        attackButtons.add(tb);


//        for (int i = 0; i < 6; i++) {
//            final int j = i;
//            TextButton attButt = new TextButton("attack " + i, skin, "tb-att");
//            attButt.setSize(264, 100);
//            attackButtons.add(attButt);
//            attButt.addListener(
//                new ClickListener() {
//                    @Override
//                    public void clicked(InputEvent event, float x, float y) {
//                        chosenAttack = j;
//                        notifyWidgetObservers();
//                    }
//                }
//            );
//        }


//        for(TextButton t : attackButtons) {
//            addActor(t);
//            t.setVisible(false);
//        }

//        attackButtons.get(0).setPosition(72+168,112,Align.bottomLeft);
//        attackButtons.get(1).setPosition(72+168+268,112,Align.bottomLeft);
//        attackButtons.get(2).setPosition(72+168+268+268,112,Align.bottomLeft);
//        attackButtons.get(3).setPosition(72+168,12,Align.bottomLeft);
//        attackButtons.get(4).setPosition(72+168+268,12,Align.bottomLeft);
//        attackButtons.get(5).setPosition(72+168+268+268,12,Align.bottomLeft);
    }

    public void init(Monster monster) {
        Array<Attack> attacks = monster.attacks;
//        for(Button b : attackButtons) b.setVisible(false);
        int i = 0;
        Iterator<Attack> attIt = attacks.iterator();
//        while(attIt.hasNext() && i<6) {
//            Attack att = attIt.next();
//            attackButtons.get(i).setVisible(true);
//            attackButtons.get(i).setText(Services.getL18N().l18n().get(att.name) + " (" + att.damage + ")");
//            i++;
//        }
    }


    @Override
    public void addWidgetObserver(WidgetObserver wo) {
        if(!observers.contains(wo, true)) observers.add(wo);
    }

    @Override
    public void notifyWidgetObservers() {
        for(WidgetObserver wo : observers) wo.getNotified(this);
    }
}
