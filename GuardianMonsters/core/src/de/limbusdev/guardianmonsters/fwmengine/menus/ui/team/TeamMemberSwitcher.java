package de.limbusdev.guardianmonsters.fwmengine.menus.ui.team;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.model.monsters.Monster;
import de.limbusdev.guardianmonsters.model.MonsterDB;

/**
 * @author Georg Eckert 2017
 */

public class TeamMemberSwitcher extends Group {

    private Callbacks callbacks;
    private int currentlyChosen;
    private MonsterPreviewWidget previewWidget;
    private Label name;

    public TeamMemberSwitcher(Skin skin, final ArrayMap<Integer, Monster> team, final Callbacks callbacks) {
        super();

        this.callbacks = callbacks;
        this.currentlyChosen = 0;

        setSize(96,64);

        Label bg = new Label("", skin, "list-item");
        bg.setSize(64,64);
        bg.setPosition(16,0, Align.bottomLeft);
        addActor(bg);

        name = new Label("", skin, "default");
        name.setSize(60,20);
        name.setPosition(18,6,Align.bottomLeft);
        addActor(name);

        ImageButton previous = new ImageButton(skin, "button-previous");
        previous.setPosition(0,16,Align.bottomLeft);
        addActor(previous);

        ImageButton next = new ImageButton(skin, "button-next");
        next.setPosition(80,16,Align.bottomLeft);
        addActor(next);

        previewWidget = new MonsterPreviewWidget(skin);
        previewWidget.setPosition(32,25,Align.bottomLeft);
        addActor(previewWidget);

        previous.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentlyChosen--;
                if(currentlyChosen < 0) {
                    currentlyChosen = team.size-1;
                }
                init(team.get(currentlyChosen));
                TeamMemberSwitcher.this.callbacks.onChanged(currentlyChosen);
            }
        });

        next.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentlyChosen++;
                if(currentlyChosen > team.size-1) {
                    currentlyChosen = 0;
                }
                init(team.get(currentlyChosen));
                TeamMemberSwitcher.this.callbacks.onChanged(currentlyChosen);
            }
        });

        init(team.get(0));
    }

    public void init(Monster m) {
        name.setText(MonsterDB.getLocalNameById(m.ID));
        previewWidget.setPreview(m.ID);
    }

    public interface Callbacks {
        /**
         * Is called, when one of the switch buttons is clicked to choose a new team member position
         * @param position
         */
        void onChanged(int position);
    }

    public int getCurrentlyChosen() {
        return currentlyChosen;
    }
}
