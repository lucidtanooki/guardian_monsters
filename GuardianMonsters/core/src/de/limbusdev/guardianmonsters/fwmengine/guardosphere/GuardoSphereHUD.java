package de.limbusdev.guardianmonsters.fwmengine.guardosphere;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.assets.paths.AssetPath;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.AHUD;
import de.limbusdev.guardianmonsters.ui.widgets.ParticleEffectActor;

/**
 * GuardoSphereHUD
 *
 * @author Georg Eckert 2017
 */

public class GuardoSphereHUD extends AHUD implements GuardoSphereTeamWidget.Callbacks {

    private Team team;
    private GuardoSphere guardoSphere;

    private GuardianDetailWidget detailWidget;
    private ButtonGroup guardianButtonGroup;

    public GuardoSphereHUD(Skin skin, Team team,GuardoSphere guardoSphere) {
        super(skin);

        this.team = team;

        ParticleEffectActor particles = new ParticleEffectActor("guardosphere");
        particles.start();
        particles.setPosition(0,0, Align.bottomLeft);
        stage.addActor(particles);

        Container<Group> container = new Container<>(new Group());
        container.setBackground(skin.getDrawable("guardosphere-frame"));
        container.setSize(252,180);
        container.setPosition(8, Constant.HEIGHT-8,Align.topLeft);

        detailWidget = new GuardianDetailWidget(skin);
        detailWidget.setPosition(Constant.WIDTH-8,Constant.HEIGHT-8,Align.topRight);

        guardianButtonGroup = new ButtonGroup();
        guardianButtonGroup.setMaxCheckCount(1);
        guardianButtonGroup.setMinCheckCount(1);

        GuardoSphereTeamWidget teamWidget = new GuardoSphereTeamWidget(skin, team, guardianButtonGroup);
        teamWidget.setPosition(8,8,Align.bottomLeft);
        teamWidget.setCallbacks(this);

        stage.addActor(container);
        stage.addActor(detailWidget);
        stage.addActor(teamWidget);
    }

    @Override
    protected void reset() {

    }

    @Override
    public void show() {
        Services.getAudio().playLoopMusic(AssetPath.Audio.Music.GUARDOSPHERE);
    }

    @Override
    public void hide() {
        Services.getAudio().stopMusic(AssetPath.Audio.Music.GUARDOSPHERE);
    }

    @Override
    public void onButtonPressed(int teamPosition) {
        detailWidget.showDetails(team.get(teamPosition));
    }
}
