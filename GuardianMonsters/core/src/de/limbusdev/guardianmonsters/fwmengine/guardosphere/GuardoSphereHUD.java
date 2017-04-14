package de.limbusdev.guardianmonsters.fwmengine.guardosphere;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.data.AudioAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.fwmengine.ui.AHUD;
import de.limbusdev.guardianmonsters.fwmengine.ui.ParticleEffectActor;
import de.limbusdev.guardianmonsters.model.monsters.Team;

/**
 * GuardoSphereHUD
 *
 * @author Georg Eckert 2017
 */

public class GuardoSphereHUD extends AHUD {

    private Team team;
    private GuardoSphere guardoSphere;

    private GuardianDetailWidget detailWidget;

    public GuardoSphereHUD(Skin skin, Team team,GuardoSphere guardoSphere) {
        super(skin);

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

        GuardoSphereTeamWidget teamWidget = new GuardoSphereTeamWidget(skin, team);
        teamWidget.setPosition(8,8,Align.bottomLeft);


        stage.addActor(container);
        stage.addActor(detailWidget);
        stage.addActor(teamWidget);
    }

    @Override
    protected void reset() {

    }

    @Override
    public void show() {
        Services.getAudio().playLoopMusic(AudioAssets.guardosphereMusic);
    }

    @Override
    public void hide() {
        Services.getAudio().stopMusic(AudioAssets.guardosphereMusic);
    }
}
