package de.limbusdev.guardianmonsters.fwmengine.guardosphere;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.scene2d.SubImageImageButton;
import de.limbusdev.guardianmonsters.services.Services;

/**
 * GuardoSphereButton
 *
 * @author Georg Eckert 2017
 */

public class GuardoSphereButton extends SubImageImageButton {
    public GuardoSphereButton(Skin skin, AGuardian guardian) {
        super(skin, "button-gs", construct(guardian));
    }

    private static Image construct(AGuardian guardian) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(
            Services.getMedia().getMonsterMiniSprite(guardian.getSpeciesDescription().getID(), guardian.getAbilityGraph().getCurrentForm()));
        Image miniSprite = new Image(drawable);
        return miniSprite;
    }
}
