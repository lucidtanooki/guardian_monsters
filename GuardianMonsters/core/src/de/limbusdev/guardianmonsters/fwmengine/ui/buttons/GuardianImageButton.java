package de.limbusdev.guardianmonsters.fwmengine.ui.buttons;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.guardians.monsters.Monster;
import de.limbusdev.guardianmonsters.scene2d.SubImageImageButton;

/**
 * GuardianImageButton
 *
 * @author Georg Eckert 2017
 */

public class GuardianImageButton extends SubImageImageButton {
    public GuardianImageButton(Skin skin, String style, Monster guardian) {
        super(skin, style, construct(guardian));
    }

    private static Image construct(Monster guardian) {
        TextureRegionDrawable drawable = new TextureRegionDrawable(
            Services.getMedia().getMonsterMiniSprite(guardian.ID));
        Image miniSprite = new Image(drawable);
        return miniSprite;
    }
}