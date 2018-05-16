package main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.team;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.ui.widgets.ATeamChoiceWidget;
import de.limbusdev.guardianmonsters.ui.widgets.Callback;
import de.limbusdev.utils.geometry.IntVec2;

/**
 * @author Georg Eckert 2017
 */

public class TeamBarWidget extends ATeamChoiceWidget
{


    public TeamBarWidget(Skin skin, ArrayMap<Integer, AGuardian> team, Callback.ButtonID callbacks) {
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
