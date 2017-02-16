package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Created by georg on 16.02.17.
 */

public class TeamSubMenu extends AInventorySubMenu {

    public TeamSubMenu(Skin skin, ArrayMap<Integer, Monster> team) {
        super(skin);

        Group monsterChoice = new Group();
        monsterChoice.setSize(140, GS.HEIGHT-36);
        monsterChoice.setPosition(0,0, Align.bottomLeft);
        Image monsterChoiceBg = new Image(skin.getDrawable("menu-col-bg"));
        monsterChoiceBg.setPosition(2,2,Align.bottomLeft);
        monsterChoice.addActor(monsterChoiceBg);

        // .................................................................................. VALUES
        Group monsterStats = new Group();
        monsterStats.setSize(140,GS.HEIGHT-36);
        monsterStats.setPosition(140+2,0,Align.bottomLeft);
        Image monsterStatsBg = new Image(skin.getDrawable("menu-col-bg"));
        monsterStatsBg.setPosition(2,2,Align.bottomLeft);
        monsterStats.addActor(monsterStatsBg);

        Label key = new Label("Monster Name", skin, "default");
        key.setPosition(8,200-8,Align.topLeft);
        monsterStats.addActor(key);

        key = new Label("HP", skin, "default");
        key.setPosition(8,200-8-18,Align.topLeft);
        monsterStats.addActor(key);

        key = new Label("MP", skin, "default");
        key.setPosition(8,200-8-18*2,Align.topLeft);
        monsterStats.addActor(key);

        key = new Label("P.Str.", skin, "default");
        key.setPosition(8,200-8-18*3,Align.topLeft);
        monsterStats.addActor(key);

        key = new Label("P.Def.", skin, "default");
        key.setPosition(8,200-8-18*4,Align.topLeft);
        monsterStats.addActor(key);

        key = new Label("M.Str.", skin, "default");
        key.setPosition(8,200-8-18*5,Align.topLeft);
        monsterStats.addActor(key);

        key = new Label("M.Def.", skin, "default");
        key.setPosition(8,200-8-18*6,Align.topLeft);
        monsterStats.addActor(key);

        key = new Label("Speed", skin, "default");
        key.setPosition(8,200-8-18*7,Align.topLeft);
        monsterStats.addActor(key);


        // .................................................................................. SPRITE
        Group monsterView = new Group();
        monsterView.setSize(140,GS.HEIGHT-36);
        monsterView.setPosition((140+2)*2,0,Align.bottomLeft);
        Image monsterViewBg = new Image(skin.getDrawable("menu-col-bg"));
        monsterViewBg.setPosition(2,2,Align.bottomLeft);
        monsterView.addActor(monsterViewBg);
        Image monsterImg = new Image();
        monsterImg.setSize(128,128);
        monsterImg.setPosition(6,200-4,Align.topLeft);
        monsterImg.setDrawable(new TextureRegionDrawable(Services.getMedia().getMonsterSprite(team.get(1).ID)));
        monsterView.addActor(monsterImg);

        Image statPentagram = new Image();
        statPentagram.setSize(64,64);
        statPentagram.setPosition(38+2,8,Align.bottomLeft);
        statPentagram.setDrawable(skin.getDrawable("statPentagram"));
        monsterView.addActor(statPentagram);


        addActor(monsterChoice);
        addActor(monsterStats);
        addActor(monsterView);

        setDebug(GS.DEBUGGING_ON, true);
    }
}
