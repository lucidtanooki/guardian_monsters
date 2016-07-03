package org.limbusdev.monsterworld.ui;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import org.limbusdev.monsterworld.managers.MediaManager;
import org.limbusdev.monsterworld.utils.GS;

/**
 * Created by georg on 03.07.16.
 */
public class MonsterStateWidget extends WidgetGroup {
    private ProgressBar hpBar;
    private ProgressBar mpBar;
    private ProgressBar rpBar;
    private ProgressBar epBar;
    private Label nameLabel;
    private Label levelLabel;
    private Image hudBgImg, hudRingImg;

    public MonsterStateWidget(Skin skin) {
        hudBgImg = new Image(skin.getDrawable("monStateUIL"));
        hudBgImg.setPosition(0,0,Align.bottomLeft);
        hudBgImg.setHeight(56);
        hudBgImg.setWidth(416);
        hudRingImg = new Image(skin.getDrawable("monStateWidgetRing"));
        hudRingImg.setPosition(GS.COL*12,0,Align.bottomLeft);
        hudRingImg.setSize(56,56);

        Label.LabelStyle ls = new Label.LabelStyle();
        ls.background = skin.getDrawable("invis");
        ls.font = skin.getFont("default-font");
        ls.fontColor = Color.WHITE;

        Label nameLabel = new Label("Monster", ls);
        nameLabel.setWidth(192);
        nameLabel.setHeight(48);
        nameLabel.setPosition(24,4);

        hpBar = new ProgressBar(0, 100, 1, false, skin, "hp");
        mpBar = new ProgressBar(0, 100, 1, false, skin, "hp");
        rpBar = new ProgressBar(0, 100, 1, true, skin, "hp");
        epBar = new ProgressBar(0, 100, 1, false, skin, "hp");

        hpBar.setPosition(240,23,Align.bottomLeft);
        hpBar.setSize(176,19);
        hpBar.setValue(100);
        mpBar.setPosition(124, 6);
        mpBar.setWidth(100);
        mpBar.setValue(100);
        rpBar.setPosition(100, 5);
        rpBar.setHeight(22);
        mpBar.setValue(100);
        epBar.setPosition(9, 0);
        epBar.setWidth(120);
        epBar.setValue(100);

        hpBar.setAnimateInterpolation(Interpolation.linear);
        hpBar.setAnimateDuration(1f);
        rpBar.setAnimateInterpolation(Interpolation.linear);
        rpBar.setAnimateDuration(.1f);
        epBar.setAnimateInterpolation(Interpolation.linear);
        epBar.setAnimateDuration(.1f);

        ls.font = skin.getFont("white");
        levelLabel = new Label("0", ls);
        levelLabel.setPosition(222, 27, Align.center);

        // Sorting
        this.addActor(hudBgImg);
        this.addActor(hpBar);
        this.addActor(mpBar);
        this.addActor(rpBar);
        this.addActor(epBar);
        this.addActor(hudRingImg);
        this.addActor(nameLabel);
        this.addActor(levelLabel);
    }
}
