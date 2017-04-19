package de.limbusdev.guardianmonsters.fwmengine.guardosphere;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.monsters.Monster;

/**
 * GuardoSphereButton
 *
 * @author Georg Eckert 2017
 */

public class GuardoSphereButton extends ImageButton {
    public GuardoSphereButton(Skin skin, Monster guardian) {
        super(skin, "button-gs");

        TextureRegionDrawable drawable = new TextureRegionDrawable(
            Services.getMedia().getMonsterMiniSprite(guardian.ID));
        Image miniSprite = new Image(drawable);
        miniSprite.setPosition(8,8, Align.bottomLeft);
        addActor(miniSprite);
    }
}
