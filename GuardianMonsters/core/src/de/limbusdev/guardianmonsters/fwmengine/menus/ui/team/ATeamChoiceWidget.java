package de.limbusdev.guardianmonsters.fwmengine.menus.ui.team;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.geometry.IntVec2;
import de.limbusdev.guardianmonsters.model.monsters.Monster;

/**
 * Created by Georg Eckert on 01.03.17.
 */

public abstract class ATeamChoiceWidget extends Group {

    protected Array<IntVec2> positions;
    protected ClickListener handler;
    protected ButtonGroup<ImageButton> memberButtons;
    protected int currentPosition=0, oldPosition=0;
    protected Skin skin;
    protected Group buttons;

    public ATeamChoiceWidget(Skin skin, ClickListener clHandler) {
        super();
        this.skin = skin;
        this.handler = clHandler;
        positions = new Array<>();
        buttons = new Group();
    }

    public void init(ArrayMap<Integer,Monster> team) {
        memberButtons = new ButtonGroup<>();
        buttons.clearChildren();
        addActor(buttons);

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
            button.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
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

    public Array<IntVec2> getPositions() {
        return positions;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public int getOldPosition() {
        return oldPosition;
    }

    public void setHandler(ClickListener handler) {
        this.handler = handler;
    }

    public interface ClickListener {
        void onTeamMemberButton(int position);
    }

}
