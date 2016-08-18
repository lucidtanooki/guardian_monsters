package de.limbusdev.guardianmonsters.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.geometry.IntVector2;
import de.limbusdev.guardianmonsters.model.MonsterInBattle;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */
public class MonsterIndicatorWidget extends WidgetGroup implements ObservableWidget, BattleWidget {

    private Array<WidgetObserver> observers;

    // Buttons
    public Array<ImageButton> indicatorButtonsHero, indicatorButtonsOpponent;
    private Image indicatorHero, indicatorOpponent;

    public int chosenMember, chosenOpponent;

    private Array<Boolean> availableChoicesHero, availableChoicesOpponent;


    /**
     *
     * @param skin battle action UI skin
     */
    public MonsterIndicatorWidget(Skin skin) {
        super();

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

        // Battle HUD Monster Indicators
        indicatorOpponent = new Image(skin.getDrawable("indicator"));
        indicatorHero = new Image(skin.getDrawable("indicator"));


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
    public void init(Array<MonsterInBattle> hero, Array<MonsterInBattle> oppo) {
        clear();
        initializeAttributes();

        int i=0;
        for(MonsterInBattle m : hero) {
            addActor(indicatorButtonsHero.get(i));
            indicatorButtonsHero.get(i).setVisible(true);
            availableChoicesHero.set(i,true);
            i++;
        }
        i=0;
        for(MonsterInBattle m : oppo) {
            addActor(indicatorButtonsOpponent.get(i));
            indicatorButtonsOpponent.get(i).setVisible(true);
            availableChoicesOpponent.set(i,true);
            i++;
        }

        setIndicatorPosition(true,0);
        setIndicatorPosition(false,0);
        addActor(indicatorHero);
        addActor(indicatorOpponent);
    }

    private void setIndicatorPosition(boolean heroesTeam, int pos) {
        if(heroesTeam) {
            switch(pos) {
                case 2:  indicatorHero.setPosition(IndPos.HERO_TOP.x, IndPos.HERO_TOP.y, Align.center);break;
                case 1:  indicatorHero.setPosition(IndPos.HERO_BOT.x, IndPos.HERO_BOT.y, Align.center);break;
                default: indicatorHero.setPosition(IndPos.HERO_MID.x, IndPos.HERO_MID.y, Align.center);break;
            }
            setIndicatorButtonChecked(pos,heroesTeam);
            chosenMember = pos;
        } else {
            switch(pos) {
                case 2:indicatorOpponent.setPosition(IndPos.OPPO_TOP.x, IndPos.OPPO_TOP.y, Align.center);break;
                case 1:indicatorOpponent.setPosition(IndPos.OPPO_BOT.x, IndPos.OPPO_BOT.y, Align.center);break;
                default:indicatorOpponent.setPosition(IndPos.OPPO_MID.x, IndPos.OPPO_MID.y, Align.center);break;
            }
            setIndicatorButtonChecked(pos,heroesTeam);
            chosenOpponent = pos;
        }
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

        for(ImageButton b : buttons) b.setChecked(false);
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


    private final static class IndPos {
        private static final IntVector2 HERO_TOP  = new IntVector2(GS.COL*19, GS.ROW*34);
        private static final IntVector2 HERO_MID  = new IntVector2(GS.COL*13, GS.ROW*31);
        private static final IntVector2 HERO_BOT  = new IntVector2(GS.COL*7, GS.ROW*28);
        private static final IntVector2 OPPO_TOP  = new IntVector2(GS.RES_X-HERO_TOP.x, HERO_TOP.y);
        private static final IntVector2 OPPO_MID  = new IntVector2(GS.RES_X-HERO_MID.x, HERO_MID.y);
        private static final IntVector2 OPPO_BOT  = new IntVector2(GS.RES_X-HERO_BOT.x, HERO_BOT.y);
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

    @Override
    public void addFadeOutAction(float duration) {
        addAction(Actions.sequence(Actions.alpha(0, duration), Actions.visible(false)));
    }

    @Override
    public void addFadeInAction(float duration) {
        addAction(Actions.sequence(Actions.visible(true), Actions.alpha(1, duration)));
    }

    @Override
    public void addFadeOutAndRemoveAction(float duration) {
        addAction(Actions.sequence(Actions.alpha(0, duration), Actions.visible(false), Actions.run(new Runnable() {
            @Override
            public void run() {
                remove();
            }
        })));
    }

    @Override
    public void addFadeInAndAddToStageAction(float duration, Stage newParent) {
        newParent.addActor(this);
        addAction(Actions.sequence(Actions.visible(true), Actions.alpha(1, duration)));
    }
}
