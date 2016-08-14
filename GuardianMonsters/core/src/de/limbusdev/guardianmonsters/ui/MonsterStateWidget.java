package de.limbusdev.guardianmonsters.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;


import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInformation;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 * Created by georg on 03.07.16.
 */
public class MonsterStateWidget extends WidgetGroup implements Observer {
    private ProgressBar hpBar;
    private ProgressBar mpBar;
    private ProgressBar epBar;
    private Label nameLabel;
    private Label levelLabel;
    private Image hudBgImg, hudRingImg, hudNameImg;

    public MonsterStateWidget(Skin skin) {
        hudBgImg = new Image(skin.getDrawable("monStateUIbg2"));
        hudBgImg.setPosition(0,0,Align.bottomLeft);
        hudBgImg.setHeight(56);
        hudBgImg.setWidth(416);
        hudRingImg = new Image(skin.getDrawable("monStateWidgetRing"));
        hudRingImg.setPosition(GS.COL*12,0,Align.bottomLeft);
        hudRingImg.setSize(56,56);
        hudNameImg = new Image(skin.getDrawable("monStateUIname"));
        hudNameImg.setPosition(0,0,Align.bottomLeft);

        Label.LabelStyle ls = new Label.LabelStyle();
        ls.background = skin.getDrawable("invis");
        ls.font = skin.getFont("default-font");
        ls.fontColor = Color.WHITE;

        nameLabel = new Label("Monster", ls);
        nameLabel.setWidth(192);
        nameLabel.setHeight(48);
        nameLabel.setPosition(24,4);

        hpBar = new ProgressBar(0, 100, 1, false, skin, "hp");
        mpBar = new ProgressBar(0, 100, 1, false, skin, "mp");
        epBar = new ProgressBar(0, 100, 1, false, skin, "ep");

        hpBar.setPosition(240,23,Align.bottomLeft);
        hpBar.setSize(176,19);
        hpBar.setValue(100);
        mpBar.setPosition(240, 13, Align.bottomLeft);
        mpBar.setSize(150,11);
        mpBar.setValue(100);
        mpBar.setValue(100);
        epBar.setPosition(4,4,Align.bottomLeft);
        epBar.setWidth(200);
        epBar.setValue(100);

        hpBar.setAnimateInterpolation(Interpolation.linear);
        hpBar.setAnimateDuration(1f);
        epBar.setAnimateInterpolation(Interpolation.linear);
        epBar.setAnimateDuration(.1f);

        ls.font = skin.getFont("white");
        levelLabel = new Label("0", ls);
        levelLabel.setPosition(222, 27, Align.center);

        // Sorting
        this.addActor(hudBgImg);
        this.addActor(hudNameImg);
        this.addActor(hpBar);
        this.addActor(mpBar);
        this.addActor(epBar);
        this.addActor(hudRingImg);
        this.addActor(nameLabel);
        this.addActor(levelLabel);

        this.setBounds(0,0,416,56);

    }

    /**
     * Initializes the widget to show a monsters status values
     * @param mon
     */
    public void init(Monster mon) {
        update(mon, "");
        nameLabel.setText(MonsterInformation.getInstance().monsterNames.get(mon.ID-1));
        mon.addObserver(this);
    }

    /**
     * Updates status view when the monster provides updates
     * @param o
     * @param arg
     */
    @Override
    public void update(Observable o, Object arg) {
        Monster obsMon = (Monster)o;
        this.hpBar.setValue(obsMon.getHPPerc());
        this.mpBar.setValue(obsMon.getMPPerc());
        this.epBar.setValue(obsMon.getExpPerc());
        this.levelLabel.setText(Integer.toString(obsMon.level));
        System.out.println("Received Update");
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
}