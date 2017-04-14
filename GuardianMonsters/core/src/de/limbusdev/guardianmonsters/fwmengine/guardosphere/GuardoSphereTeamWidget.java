package de.limbusdev.guardianmonsters.fwmengine.guardosphere;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.monsters.Monster;
import de.limbusdev.guardianmonsters.model.monsters.Team;

/**
 * GuardoSphereTeamWidget
 *
 * @author Georg Eckert 2017
 */

public class GuardoSphereTeamWidget extends Group {

    private static final int WIDTH = 252;
    private static final int HEIGHT= 40;

    private Skin skin;
    private Team team;
    private HorizontalGroup monsterButtons;

    public GuardoSphereTeamWidget(Skin skin, Team team) {
        this.team = team;
        this.skin = skin;

        setSize(WIDTH,HEIGHT);
        Image background = new Image(skin.getDrawable("guardosphere-frame"));
        background.setSize(WIDTH,HEIGHT);
        background.setPosition(0,0, Align.bottomLeft);
        addActor(background);

        monsterButtons = new HorizontalGroup();
        monsterButtons.setSize(240,32);
        monsterButtons.setPosition(6,4,Align.bottomLeft);
        addActor(monsterButtons);

        refresh();
    }

    public void refresh() {
        monsterButtons.clear();

        ImageButton.ImageButtonStyle ibs = new ImageButton.ImageButtonStyle();

        ibs.imageUp = skin.getDrawable("guardosphere-frame");
        ibs.imageDown = skin.getDrawable("guardosphere-frame-down");

        for(Integer key : team.keys()) {

            Monster monster = team.get(key);
            TextureRegionDrawable drawable = new TextureRegionDrawable(
                Services.getMedia().getMonsterMiniSprite(monster.ID));
            ImageButton monsterButton = new ImageButton(ibs);
            monsterButton.setBounds(0,0,32,32);
            monsterButton.addActor(new Image(drawable));
            monsterButtons.addActor(monsterButton);
        }
    }
}