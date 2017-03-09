package de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * CreditsScreenWidget
 *
 * @author Georg Eckert 2017
 */

public class CreditsScreenWidget extends WidgetGroup {

    private VerticalGroup credits;

    public CreditsScreenWidget(Skin skin) {
        super();
        setSize(GS.WIDTH, GS.HEIGHT);

        // Background Transparent Black
        Image bgImg = new Image(skin.getDrawable("black"));
        bgImg.setColor(0,0,0,.75f);
        bgImg.setSize(GS.WIDTH,GS.HEIGHT);
        bgImg.setPosition(0,0, Align.bottomLeft);
        addActor(bgImg);

        // Credits
        credits = new VerticalGroup();
        credits.space(32);
        TextureAtlas logos = Services.getMedia().getTextureAtlas(TextureAssets.logosSpriteSheetFile);

        Image creditImg = new Image(logos.findRegion("limbusdev3dbit"));
        creditImg.setAlign(Align.top);
        credits.addActorAt(0, creditImg);

        String creditText = "developed by\n\nGeorg Eckert (limbusdev 2017)";
        Label creditLabel = new Label(creditText, skin, "default-white");
        creditLabel.setAlignment(Align.top,Align.center);
        credits.addActorAt(1, creditLabel);

        creditText =
            "Artwork\n\n" +
            "Monsters by\n" +
            "Moritz, Maria-Christin & Georg Eckert\n\n\n" +
            "Character Templates by PlayerRed-1";
        creditLabel = new Label(creditText, skin, "default-white");
        creditLabel.setAlignment(Align.top,Align.center);
        credits.addActorAt(2, creditLabel);

        creditText =
            "Music\n\n" +
            "Music by Matthew Pablo\n" +
            "http://www.matthewpablo.com\n\n" +
            "The Last Encounter (Battle Theme)\n" +
            "Liveley Meadow (Victory Fanfare & Song)" +
            "\n\n\n\n" +
            "Music by other Artists\n\n" +
            "City Loop by Homingstar (CC-BY-SA-3.0)\n\n" +
            "CalmBGM by syncopika (CC-BY-3.0)\n\n" +
            "XXXXXX written and produced\nby Ove Melaa (Omsofware@hotmail.com)";
        creditLabel = new Label(creditText, skin, "default-white");
        creditLabel.setAlignment(Align.top,Align.center);
        credits.addActorAt(3, creditLabel);

        creditText = "powered by";
        creditLabel = new Label(creditText, skin, "default-white");
        creditLabel.setAlignment(Align.top,Align.center);
        credits.addActorAt(4, creditLabel);

        creditImg = new Image(logos.findRegion("libgdx_pixel"));
        creditImg.setAlign(Align.top);
        credits.addActorAt(5, creditImg);

        credits.setPosition(GS.WIDTH/2, 0, Align.top);
        credits.validate();
        addActor(credits);
    }

    public void start(float time) {
        credits.addAction(Actions.sequence(
            Actions.moveBy(0, credits.getPrefHeight()+GS.HEIGHT, time),
            Actions.run(new Runnable() {
                @Override
                public void run() {
                    remove();
                    credits.setPosition(GS.WIDTH/2, 0, Align.top);
                }
            })
        ));
    }

}
