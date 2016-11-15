package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.fwmengine.managers.Media;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInformation;
import de.limbusdev.guardianmonsters.utils.GS;


/*
 * Copyright (c) 2016 by Georg Eckert
 *
 * Licensed under GPL 3.0 https://www.gnu.org/licenses/gpl-3.0.en.html
 */

public class MonsterStatusInventoryWidget extends Table{
    private HorizontalGroup hbox;
    private VerticalGroup vBoxMonsters;
    private Image monsterImgBg, selectDisplayImg, dataImgBg;
    private Image monsterImg;
    private Array<TextButton> monsterButtons;
    private Array<Monster> team;
    private Skin skin;
    private Media media;
    private TextButton infoHead, mpLabel, hpLabel, expLabel;

    public MonsterStatusInventoryWidget (Skin skin) {
        super();
        this.media = Services.getMedia();
        this.skin = skin;
        this.setFillParent(true);
        this.monsterButtons = new Array<TextButton>();

        hbox = new HorizontalGroup();
        this.add(hbox);
        Stack selectStack = new Stack();
        vBoxMonsters = new VerticalGroup();
        vBoxMonsters.padTop(GS.ROW*2);
        vBoxMonsters.align(Align.left);
        hbox.addActor(selectStack);

        selectDisplayImg = new Image(skin.getDrawable("selectDisplayBg"));
        selectStack.add(selectDisplayImg);
        selectStack.add(vBoxMonsters);

        // Middle Pane
        Stack dataDisplayStack = new Stack();
        dataImgBg = new Image(skin.getDrawable("dataDisplayBg"));
        dataDisplayStack.add(dataImgBg);
        VerticalGroup dataGroup = new VerticalGroup();
        dataGroup.setFillParent(true);
        dataGroup.pad(GS.COL*2);
        dataGroup.align(Align.topLeft);
        infoHead = new TextButton(Services.getL18N().l18n().get("stat_inv_data_sheet"), skin, "b-data-head");
        infoHead.getLabel().setAlignment(Align.left);
        infoHead.pad(GS.COL);
        dataGroup.addActor(infoHead);

        hpLabel = new TextButton("HP", skin, "b-data-entry");
        hpLabel.getLabel().setAlignment(Align.left);
        hpLabel.pad(GS.COL);
        dataGroup.addActor(hpLabel);

        mpLabel = new TextButton("MP", skin, "b-data-entry");
        mpLabel.getLabel().setAlignment(Align.left);
        mpLabel.pad(GS.COL);
        dataGroup.addActor(mpLabel);

        expLabel = new TextButton("EXP", skin, "b-data-entry");
        expLabel.getLabel().setAlignment(Align.left);
        expLabel.pad(GS.COL);
        dataGroup.addActor(expLabel);

        dataDisplayStack.add(dataGroup);
        hbox.addActor(dataDisplayStack);

        monsterImgBg = new Image(skin.getDrawable("monDisplayBg"));

        Stack monImgStack = new Stack();
        monImgStack.add(monsterImgBg);
        monsterImg = new Image();
        monsterImg.setSize(480,480);
        monsterImg.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                monsterImg.addAction(Actions.sequence(
                        Actions.scaleTo(1f,.9f,.3f, Interpolation.sine),
                        Actions.scaleTo(1,1.1f,.2f, Interpolation.sine),
                        Actions.scaleTo(1,1,.1f, Interpolation.sine)));
            }
        });

        monsterImg.addAction(
                Actions.sequence(
                        Actions.sizeTo(480,480),
                        Actions.forever(Actions.sequence(
                            Actions.moveBy(0,20,3, Interpolation.sine),
                            Actions.moveBy(0,-20,3, Interpolation.sine)
        ))));
        monImgStack.add(monsterImg);
        hbox.addActor(monImgStack);


        this.setDebug(GS.DEBUGGING_ON);


    }

    public void init(TeamComponent team) {
        this.team = team.monsters;
        for(Monster m : team.monsters) {
            final TextButton tb = new TextButton(
                    MonsterInformation.getInstance().monsterNames.get(m.ID-1), skin, "b-monster");
            tb.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    setActiveMonster(monsterButtons.indexOf(tb,true));
                }
            });
            monsterButtons.add(tb);
        }

        for(TextButton tb : monsterButtons) vBoxMonsters.addActor(tb);

        setActiveMonster(0);
        this.invalidate();
    }

    private void setActiveMonster(int i) {
        this.invalidate();
        for(TextButton tb : monsterButtons) tb.setChecked(false);
        monsterButtons.get(i).setChecked(true);
        monsterImg.setDrawable(new TextureRegionDrawable(media.getMonsterSprite(team.get(i).ID)));
        hpLabel.setText("HP    " + team.get(i).getHP() + "/" + team.get(i).getHPfull());
        mpLabel.setText("MP    " + team.get(i).getMP() + "/" + team.get(i).getMPfull());
        expLabel.setText("EXP   " + team.get(i).getExp() + "/" + team.get(i).expAvailableInThisLevel());
    }
}
