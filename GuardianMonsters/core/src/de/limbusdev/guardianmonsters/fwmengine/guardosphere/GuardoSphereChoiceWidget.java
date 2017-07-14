package de.limbusdev.guardianmonsters.fwmengine.guardosphere;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.guardians.monsters.Monster;

/**
 * GuardoSphereChoiceWidget
 *
 * @author Georg Eckert 2017
 */

public class GuardoSphereChoiceWidget extends Group {
    private static final int WIDTH = 252;
    private static final int HEIGHT= 180;

    private Skin skin;
    private GuardoSphere sphere;
    private Table table;
    private ButtonGroup buttonGroup;
    private Array<Button> buttons;
    private Callbacks callbacks;

    public GuardoSphereChoiceWidget(Skin skin, GuardoSphere sphere, ButtonGroup group) {
        this.sphere = sphere;
        this.skin = skin;
        this.buttonGroup = group;

        callbacks = new Callbacks() {
            @Override
            public void onButtonPressed(int teamPosition) {
                System.out.println("GuardoSphereChoiceWidget: Dummy Callback");
            }
        };

        buttons = new Array<>();

        setSize(WIDTH,HEIGHT);
        Image background = new Image(skin.getDrawable("guardosphere-frame"));
        background.setSize(WIDTH,HEIGHT);
        background.setPosition(0,0, Align.bottomLeft);
        addActor(background);

        table = new Table();
        table.setSize(240,170);
        table.setPosition(6,4,Align.bottomLeft);
        addActor(table);

        refresh(0);
    }

    public void refresh(int page) {
        for(Button b : buttons) {
            buttonGroup.remove(b);
            b.remove();
        }
        buttons.clear();
        table.clear();

        for(int i=page*21; i<(page+1)*21; i++) {
            if(i % 7 == 0) {
                table.row();
            }
            if(sphere.containsKey(i)) {
                final int key = i;
                Monster monster = sphere.get(i);
                ImageButton monsterButton = new GuardoSphereButton(skin, monster);

                table.add(monsterButton).width(32).height(32);
                buttons.add(monsterButton);
                buttonGroup.add(monsterButton);
                monsterButton.addListener(
                    new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            callbacks.onButtonPressed(key);
                        }
                    }
                );
            } else {
                table.add(new Actor()).width(32).height(32);
            }
        }
    }

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public interface Callbacks {
        void onButtonPressed(int spherePosition);
    }
}
