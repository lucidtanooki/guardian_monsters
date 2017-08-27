package de.limbusdev.guardianmonsters.fwmengine.menus.ui.team;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.utils.geometry.IntVec2;

/**
 * Created by Georg Eckert on 16.02.17.
 */

public class TeamCircleWidget extends ATeamChoiceWidget {

    public TeamCircleWidget(Skin skin, ArrayMap<Integer,AGuardian> team, Callbacks callbacks) {
        super(skin, callbacks);

        positions.add(new IntVec2(54,144-85));
        positions.add(new IntVec2(54,144-45));
        positions.add(new IntVec2(89,144-65));
        positions.add(new IntVec2(89,144-105));
        positions.add(new IntVec2(54,144-125));
        positions.add(new IntVec2(19,144-105));
        positions.add(new IntVec2(19,144-65));

        setSize(140,140);
        Image bgImg = new Image(skin.getDrawable("teamCircle"));
        bgImg.setPosition(0,0, Align.bottomLeft);
        addActor(bgImg);

        init(team);
    }
}
