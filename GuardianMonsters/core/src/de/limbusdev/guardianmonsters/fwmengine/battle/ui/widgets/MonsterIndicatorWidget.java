package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.battle.model.MonsterInBattle;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */
public class MonsterIndicatorWidget extends BattleWidget implements ObservableWidget {

    private Array<WidgetObserver> observers;

    // Buttons
    public Array<ImageButton> indicatorButtons;

    public int chosen;

    private Array<Boolean> availableChoices;


    /**
     *
     * @param skin battle action UI skin
     */
    public MonsterIndicatorWidget(final AHUD hud, Skin skin, int align) {
        super(hud);

        initializeAttributes();
        observers = new Array<WidgetObserver>();
        indicatorButtons = new Array<ImageButton>();

        this.setBounds(0,0,0,0);


        ImageButton ind = new ImageButton(skin, "choice-r");
        ind.setPosition(0, 32, align);
        ind.setScale(GS.zoom);
        indicatorButtons.add(ind);
        ind.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setIndicatorPosition(0);
            }
        });

        ind = new ImageButton(skin, "choice-r");
        ind.setPosition(0, 0,align);
        indicatorButtons.add(ind);
        ind.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setIndicatorPosition(1);
            }
        });

        ind = new ImageButton(skin, "choice-r");
        ind.setPosition(0, 64, align);
        indicatorButtons.add(ind);
        ind.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setIndicatorPosition(2);
            }
        });

    }

    private void initializeAttributes() {
        chosen = 0;
        availableChoices = new Array<Boolean>();
        for(int i=0; i<3; i++) {
            availableChoices.add(false);
        }
    }

    public void init(ArrayMap<Integer,Monster> team) {
        clear();
        initializeAttributes();

        for(Integer key : team.keys()) {
            Monster m = team.get(key);
            indicatorButtons.get(key).setVisible(true);
            addActor(indicatorButtons.get(key));
            availableChoices.set(key,true);
        }

        int indicatorStartPos = 0;


        for(Integer key : team.keys()) {
            Monster m = team.get(key);
            if(m.getHP() > 0) {
                indicatorStartPos = key;
                break;
            }
        }

        setIndicatorPosition(indicatorStartPos);
    }

    private void setIndicatorPosition(int pos) {
        System.out.println("Choosing monster: " + pos);
        chosen = pos;
        setIndicatorButtonChecked(pos);

        notifyWidgetObservers();
    }

    /**
     *
     * @param pos
     */
    private void setIndicatorButtonChecked(int pos) {
        Array<ImageButton> buttons;
        buttons = indicatorButtons;

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
     * @param pos
     */
    public void deactivateChoice(int pos) {
            indicatorButtons.get(pos).setVisible(false);
            availableChoices.set(pos,false);
            for(int i=0; i<3; i++)
                if(availableChoices.get(i))
                    setIndicatorPosition(i);
        notifyWidgetObservers();
    }
}
