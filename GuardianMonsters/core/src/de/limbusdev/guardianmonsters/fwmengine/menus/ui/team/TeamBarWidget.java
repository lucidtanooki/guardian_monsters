package de.limbusdev.guardianmonsters.fwmengine.menus.ui.team;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.geometry.IntVec2;
import de.limbusdev.guardianmonsters.model.monsters.Monster;

/**
 * @author Georg Eckert 2017
 */

public class TeamBarWidget extends ATeamChoiceWidget {


    public TeamBarWidget(Skin skin, ArrayMap<Integer, Monster> team, Callbacks callbacks) {
        super(skin, callbacks);

        setSize(260,44);

        getPositions().clear();
        getPositions().add(new IntVec2(4,8));
        getPositions().add(new IntVec2(4+(2+36),8));
        getPositions().add(new IntVec2(4+(2+36)*2,8));
        getPositions().add(new IntVec2(4+(2+36)*3,8));
        getPositions().add(new IntVec2(4+(2+36)*4,8));
        getPositions().add(new IntVec2(4+(2+36)*5,8));
        getPositions().add(new IntVec2(4+(2+36)*6,8));


        Label bg = new Label("", skin, "list-item");
        bg.setSize(260,44);
        bg.setPosition(0,0, Align.bottomLeft);
        addActor(bg);

        init(team);


    }
}
