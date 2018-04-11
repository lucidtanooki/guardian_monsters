package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.guardians.monsters.ISpeciesDescriptionService;
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics;
import de.limbusdev.guardianmonsters.services.Services;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */
public class MonsterStateWidget extends WidgetGroup implements Observer
{
    private ProgressBar hpBar;
    private ProgressBar mpBar;
    private ProgressBar epBar;
    private Label nameLabel;
    private Label levelLabel;
    private Image hudBgImg, hudRingImg, hudNameImg;

    /**
     *
     * @param skin  skin containing needed graphics
     * @param showExp  whether exp bar shall be shown
     */
    public MonsterStateWidget(Skin skin, boolean showExp) {
        hudBgImg = new Image(skin.getDrawable("monStateUIbg2"));
        hudBgImg.setPosition(0,0,Align.bottomLeft);

        hudRingImg = new Image(skin.getDrawable("monStateWidgetRing"));
        hudRingImg.setPosition(Constant.COL*12,0,Align.bottomLeft);

        hudNameImg = new Image(skin.getDrawable("monStateUIname"));
        hudNameImg.setPosition(0,0,Align.bottomLeft);

        Label.LabelStyle ls = new Label.LabelStyle();
        ls.background = skin.getDrawable("invis");
        ls.font = skin.getFont("font16");
        ls.fontColor = Color.WHITE;

        nameLabel = new Label("Monster", ls);
        nameLabel.setWidth(96);
        nameLabel.setHeight(24);
        nameLabel.setPosition(24,2);

        hpBar = new ProgressBar(0, 100, 1, false, skin, "hp");
        mpBar = new ProgressBar(0, 100, 1, false, skin, "mp");
        epBar = new ProgressBar(0, 100, 1, false, skin, "ep");

        hpBar.setPosition(120,13,Align.bottomLeft);
        hpBar.setSize(96,9);
        hpBar.setValue(0);
        mpBar.setPosition(120, 5, Align.bottomLeft);
        mpBar.setSize(92,11);
        mpBar.setValue(0);
        epBar.setPosition(4,2,Align.bottomLeft);
        epBar.setSize(100,4);
        epBar.setValue(0);

        hpBar.setAnimateInterpolation(Interpolation.linear);
        hpBar.setAnimateDuration(1f);
        mpBar.setAnimateInterpolation(Interpolation.linear);
        mpBar.setAnimateDuration(.5f);
        epBar.setAnimateInterpolation(Interpolation.linear);
        epBar.setAnimateDuration(.1f);

        ls.font = skin.getFont("font16w");
        levelLabel = new Label("0", ls);
        levelLabel.setPosition(110, 13, Align.center);

        // Sorting
        if(showExp) this.addActor(epBar);
        this.addActor(hudBgImg);
        this.addActor(hudNameImg);
        this.addActor(hpBar);
        this.addActor(mpBar);
        this.addActor(hudRingImg);
        this.addActor(nameLabel);
        this.addActor(levelLabel);

        this.setBounds(0,0,220,32);

    }

    /**
     * Initializes the widget to show a monsters status values
     * @param guardian
     */
    public void init(AGuardian guardian)
    {
        ISpeciesDescriptionService species = GuardiansServiceLocator.getSpecies();
        refresh(guardian);
        nameLabel.setText(Services.getL18N().Guardians().get((species.getCommonNameById(guardian.getSpeciesDescription().getID(),0))));
        // TODO currentForm
        guardian.addObserver(this);
    }

    public void refresh(AGuardian guardian) {
        IndividualStatistics statistics = guardian.getIndividualStatistics();
        this.hpBar.setValue(statistics.getHPfraction());
        this.mpBar.setValue(statistics.getMPfraction());
        this.epBar.setValue(statistics.getEXPfraction());
        this.levelLabel.setText(Integer.toString(statistics.getLevel()));
        if(statistics.isKO()) {
            addAction(Actions.sequence(Actions.alpha(0, 2), Actions.visible(false)));
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        hpBar.act(delta);
        mpBar.act(delta);
        epBar.act(delta);
        nameLabel.act(delta);
        levelLabel.act(delta);
        hudBgImg.act(delta);
        hudRingImg.act(delta);
        hudNameImg.act(delta);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof AGuardian)
        {
            refresh((Guardian) o);
        }
    }
}
