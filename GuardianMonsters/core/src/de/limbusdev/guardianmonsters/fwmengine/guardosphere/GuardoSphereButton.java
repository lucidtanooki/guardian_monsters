package de.limbusdev.guardianmonsters.fwmengine.guardosphere;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.scene2d.SubImageImageButton;

/**
 * GuardoSphereButton
 *
 * @author Georg Eckert 2017
 */

public class GuardoSphereButton extends SubImageImageButton {
    public GuardoSphereButton(Skin skin, Guardian guardian) {
        super(skin, "button-gs", construct(guardian));
    }

    private static Image construct(Guardian guardian) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(
            Services.getMedia().getMonsterMiniSprite(guardian.ID));
        Image miniSprite = new Image(drawable);
        return miniSprite;
    }
}
