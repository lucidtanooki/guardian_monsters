package de.limbusdev.guardianmonsters.fwmengine.guardosphere;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

/**
 * GuardoSphereTeamWidget
 *
 * @author Georg Eckert 2017
 */

public class GuardoSphereTeamWidget extends Group
{
    private static final int WIDTH = 252;
    private static final int HEIGHT= 40;

    private Skin skin;
    private Team team;
    private HorizontalGroup monsterButtons;
    private ButtonGroup buttonGroup;
    private Array<Button> buttons;
    private Callbacks callbacks;

    public GuardoSphereTeamWidget(Skin skin, Team team, ButtonGroup group)
    {
        this.team = team;
        this.skin = skin;
        this.buttonGroup = group;

        callbacks = teamPosition -> System.out.println("GuardoSphereTeamWidget: Dummy Callback");

        buttons = new Array<>();

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

    public void refresh()
    {
        for(Button b : buttons)
        {
            buttonGroup.remove(b);
            b.remove();
        }
        buttons.clear();
        monsterButtons.clear();

        for(final Integer key : team.keys())
        {
            AGuardian guardian = team.get(key);
            ImageButton monsterButton = new GuardoSphereButton(skin, guardian);
            monsterButtons.addActor(monsterButton);
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
        }
    }

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    public interface Callbacks
    {
        void onButtonPressed(int teamPosition);
    }
}