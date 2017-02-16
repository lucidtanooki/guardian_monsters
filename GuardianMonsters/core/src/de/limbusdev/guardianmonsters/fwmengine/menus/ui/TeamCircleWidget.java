package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.geometry.IntVec2;
import de.limbusdev.guardianmonsters.model.Monster;

import static com.badlogic.gdx.controllers.ControlType.button;

/**
 * Created by georg on 16.02.17.
 */

public class TeamCircleWidget extends Group {

    private Array<IntVec2> positions;
    private final ClickHandler handler;

    public TeamCircleWidget(Skin skin, ArrayMap<Integer,Monster> team, ClickHandler clHandler) {
        super();

        this.handler = clHandler;

        positions = new Array<>();
        positions.add(new IntVec2(54,140-85));
        positions.add(new IntVec2(54,140-45));
        positions.add(new IntVec2(89,140-65));
        positions.add(new IntVec2(89,140-105));
        positions.add(new IntVec2(54,140-125));
        positions.add(new IntVec2(19,140-105));
        positions.add(new IntVec2(19,140-65));

        setSize(140,140);
        Image bgImg = new Image(skin.getDrawable("teamCircle"));
        bgImg.setPosition(0,0, Align.bottomLeft);
        addActor(bgImg);

        for(final int key : team.keys()) {
            Monster m = team.get(key);
            Drawable preview = new TextureRegionDrawable(Services.getMedia().getMonsterMiniSprite(m.ID));
            ImageButton.ImageButtonStyle ibs = new ImageButton.ImageButtonStyle();
            ibs.imageUp = preview;
            ImageButton button = new ImageButton(ibs);
            IntVec2 pos = positions.get(key);
            button.setPosition(pos.x + 8, pos.y + 8, Align.bottomLeft);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    handler.onTeamMemberButton(key);
                }
            });
            addActor(button);
        }
    }

    public interface ClickHandler {
        public void onTeamMemberButton(int position);
    }
}
