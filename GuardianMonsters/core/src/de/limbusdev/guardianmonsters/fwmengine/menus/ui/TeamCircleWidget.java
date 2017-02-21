package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
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
    private ClickHandler handler;
    private ButtonGroup<ImageButton> memberButtons;
    private int currentPosition=0, oldPosition=0;
    private Skin skin;
    private Group buttons;

    public TeamCircleWidget(Skin skin, ArrayMap<Integer,Monster> team, ClickHandler clHandler) {
        super();

        this.skin = skin;
        this.handler = clHandler;

        positions = new Array<>();
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

        buttons = new Group();
        addActor(buttons);

        init(team);

    }

    public void init(ArrayMap<Integer,Monster> team) {
        memberButtons = new ButtonGroup<>();
        buttons.clearChildren();

        for(final int key : team.keys()) {
            Monster m = team.get(key);
            Image preview = new Image(Services.getMedia().getMonsterMiniSprite(m.ID));
            preview.setPosition(38/2-8, 38/2-8, Align.bottomLeft);
            ImageButton.ImageButtonStyle ibs = new ImageButton.ImageButtonStyle();
            Drawable imUp = skin.getDrawable("transparent");
            imUp.setMinHeight(38);
            imUp.setMinWidth(38);
            ibs.imageUp = imUp;
            ibs.imageChecked = skin.getDrawable("teamCircle-chosen");
            ibs.imageDown = skin.getDrawable("teamCircle-chosen");
            ImageButton button = new ImageButton(ibs);
            button.addActor(preview);
            IntVec2 pos = positions.get(key);
            button.setPosition(pos.x-3, pos.y-4, Align.bottomLeft);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    oldPosition = currentPosition;
                    currentPosition = key;
                    handler.onTeamMemberButton(key);
                    System.out.println("Current Position set to " + key);
                }
            });

            memberButtons.add(button);
            buttons.addActor(button);
            if(key == currentPosition) {
                button.setChecked(true);
            }
        }
        memberButtons.setMaxCheckCount(1);
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public int getOldPosition() {
        return oldPosition;
    }

    public void setHandler(ClickHandler handler) {
        this.handler = handler;
    }

    public interface ClickHandler {
        void onTeamMemberButton(int position);
    }
}
