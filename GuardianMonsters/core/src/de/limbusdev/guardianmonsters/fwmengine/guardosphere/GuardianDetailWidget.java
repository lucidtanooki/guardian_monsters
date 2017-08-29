package de.limbusdev.guardianmonsters.fwmengine.guardosphere;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.services.Services;

/**
 * GuardianDetailWidget
 *
 * @author Georg Eckert 2017
 */

public class GuardianDetailWidget extends Group {

    private static final int WIDTH = 152;
    private static final int HEIGHT= 180;

    private Image monsterSprite;
    private Label name;
    private Label level;

    public GuardianDetailWidget(Skin skin) {
        setSize(WIDTH,HEIGHT);
        Image background = new Image(skin.getDrawable("guardosphere-frame"));
        background.setSize(WIDTH,HEIGHT);
        background.setPosition(0,0, Align.bottomLeft);
        addActor(background);

        monsterSprite = new Image(skin.getDrawable("transparent"));
        monsterSprite.setSize(128,128);
        monsterSprite.setPosition(12,HEIGHT-8,Align.topLeft);
        addActor(monsterSprite);

        name = new Label("Name", skin, "white");
        name.setSize(92,20);
        name.setPosition(12,20,Align.bottomLeft);
        addActor(name);

        level = new Label("Lvl 0", skin, "white");
        level.setSize(32,20);
        level.setPosition(12+96,20,Align.bottomLeft);
        addActor(level);
    }

    public void showDetails(AGuardian guardian) {
        TextureAtlas.AtlasRegion region = Services.getMedia().getMonsterSprite(guardian.getSpeciesDescription().getID());
        monsterSprite.setDrawable(new TextureRegionDrawable(region));
        name.setText(guardian.getNickname());
        level.setText("Lvl " + guardian.getStatistics().getLevel());
    }
}
