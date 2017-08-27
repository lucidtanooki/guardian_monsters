package de.limbusdev.guardianmonsters.fwmengine.ui.buttons;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.scene2d.SubImageImageButton;
import de.limbusdev.guardianmonsters.services.Services;

/**
 * GuardianImageButton
 *
 * @author Georg Eckert 2017
 */

public class GuardianImageButton extends SubImageImageButton {
    public GuardianImageButton(Skin skin, String style, Guardian guardian) {
        super(skin, style, construct(guardian));
    }

    private static Image construct(Guardian guardian) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(
            Services.getMedia().getMonsterMiniSprite(guardian.getSpeciesData().getID()));
        Image miniSprite = new Image(drawable);
        return miniSprite;
    }
}