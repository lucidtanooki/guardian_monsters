package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.battle.model.MonsterInBattle;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */
public class MonsterIndicatorWidget extends BattleWidget implements ObservableWidget {

    private Array<WidgetObserver> observers;

    // Buttons
    public Array<ImageButton> indicatorButtonsHero, indicatorButtonsOpponent;

    public int chosenMember, chosenOpponent;

    private Array<Boolean> availableChoicesHero, availableChoicesOpponent;


    /**
     *
     * @param skin battle action UI skin
     */
    public MonsterIndicatorWidget(final AHUD hud, Skin skin) {
        super(hud);

        initializeAttributes();
        observers = new Array<WidgetObserver>();
        indicatorButtonsHero = new Array<ImageButton>();
        indicatorButtonsOpponent = new Array<ImageButton>();

        this.setBounds(0,0,0,0);

        ImageButton ind = new ImageButton(skin, "choice-l");
        ind.setPosition(0, GS.ROW*22, Align.bottomLeft);
        indicatorButtonsHero.add(ind);
        ind.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setIndicatorPosition(true,0);
            }
        });

        ind = new ImageButton(skin, "choice-l");
        ind.setPosition(0, GS.ROW*17, Align.bottomLeft);
        indicatorButtonsHero.add(ind);
        ind.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setIndicatorPosition(true,1);
            }
        });

        ind = new ImageButton(skin, "choice-l");
        ind.setPosition(0, GS.ROW*27, Align.bottomLeft);
        indicatorButtonsHero.add(ind);
        ind.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setIndicatorPosition(true,2);
            }
        });

        ind = new ImageButton(skin, "choice-r");
        ind.setPosition(GS.RES_X, GS.ROW*22, Align.bottomRight);
        indicatorButtonsOpponent.add(ind);
        ind.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setIndicatorPosition(false,0);
            }
        });

        ind = new ImageButton(skin, "choice-r");
        ind.setPosition(GS.RES_X, GS.ROW*17, Align.bottomRight);
        indicatorButtonsOpponent.add(ind);
        ind.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setIndicatorPosition(false,1);
            }
        });

        ind = new ImageButton(skin, "choice-r");
        ind.setPosition(GS.RES_X, GS.ROW*27, Align.bottomRight);
        indicatorButtonsOpponent.add(ind);
        ind.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setIndicatorPosition(false,2);
            }
        });

    }

    private void initializeAttributes() {
        chosenMember = chosenOpponent = 0;
        availableChoicesHero = new Array<Boolean>();
        availableChoicesOpponent = new Array<Boolean>();
        for(int i=0; i<3; i++) {
            availableChoicesHero.add(false);
            availableChoicesOpponent.add(false);
        }
    }

    /**
     *
     * @param hero heros monsters
     * @param oppo opponents monsters
     */
    public void init(ArrayMap<Integer,MonsterInBattle> hero, ArrayMap<Integer,MonsterInBattle> oppo) {
        clear();
        initializeAttributes();

        for(Integer key : hero.keys()) {
            MonsterInBattle m = hero.get(key);
            indicatorButtonsHero.get(m.battleFieldPosition).setVisible(true);
            addActor(indicatorButtonsHero.get(m.battleFieldPosition));
            availableChoicesHero.set(m.battleFieldPosition,true);
        }
        for(Integer key : oppo.keys()) {
            MonsterInBattle m = oppo.get(key);
            indicatorButtonsOpponent.get(m.battleFieldPosition).setVisible(true);
            addActor(indicatorButtonsOpponent.get(m.battleFieldPosition));
            availableChoicesOpponent.set(m.battleFieldPosition,true);
        }

        int indicatorStartPosHero = 0;
        int indicatorStartPosOppo = 0;

        for(Integer key : hero.keys()) {
            MonsterInBattle m = hero.get(key);
            if(!m.KO) {
                indicatorStartPosHero = key;
                break;
            }
        }

        for(Integer key : oppo.keys()) {
            MonsterInBattle m = oppo.get(key);
            if(!m.KO) {
                indicatorStartPosOppo = key;
                break;
            }
        }

        setIndicatorPosition(true,  hero.get(indicatorStartPosHero).battleFieldPosition);
        setIndicatorPosition(false, oppo.get(indicatorStartPosOppo).battleFieldPosition);
    }

    private void setIndicatorPosition(boolean heroesTeam, int pos) {
        System.out.println("Choosing monster: " + (heroesTeam ? "left" : "right") + " " + pos);
        if(heroesTeam) chosenMember = pos;
        else chosenOpponent = pos;

        setIndicatorButtonChecked(pos,heroesTeam);
        notifyWidgetObservers();
    }

    /**
     *
     * @param pos
     * @param side true=hero, false=opponent
     */
    private void setIndicatorButtonChecked(int pos, boolean side) {
        Array<ImageButton> buttons;
        if(side) buttons = indicatorButtonsHero;
        else buttons = indicatorButtonsOpponent;

        for(ImageButton b : buttons) {
            b.setProgrammaticChangeEvents(true);
            b.setChecked(false);
        }
        buttons.get(pos).setChecked(true);

    }

    @Override
    public void addWidgetObserver(WidgetObserver wo) {
        observers.add(wo);
    }

    @Override
    public void notifyWidgetObservers() {
        for(WidgetObserver wo : observers) wo.getNotified(this);
    }

    /**
     *
     * @param side true=hero, false=opponent
     * @param pos
     */
    public void deactivateChoice(boolean side, int pos) {
        if(side) {
            indicatorButtonsHero.get(pos).setVisible(false);
            availableChoicesHero.set(pos,false);
            for(int i=0; i<3; i++)
                if(availableChoicesHero.get(i))
                    setIndicatorPosition(side,i);
        } else {
            indicatorButtonsOpponent.get(pos).setVisible(false);
            availableChoicesOpponent.set(pos,false);
            for(int i=0; i<3; i++)
                if(availableChoicesOpponent.get(i))
                    setIndicatorPosition(side,i);
        }
        notifyWidgetObservers();
    }
}
