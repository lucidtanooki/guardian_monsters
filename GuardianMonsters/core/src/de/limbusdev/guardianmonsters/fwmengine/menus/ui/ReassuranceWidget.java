package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Created by georg on 20.02.17.
 */

public class ReassuranceWidget extends Group {

    public TextButton buttonYes, buttonNo;
    public Label question;

    public ReassuranceWidget(Skin skin) {
        super();
        setSize(GS.WIDTH, GS.HEIGHT);

        Image bg = new Image(skin.getDrawable("black-a80"));
        bg.setSize(GS.WIDTH, GS.HEIGHT);
        bg.setPosition(0,0,Align.bottomLeft);
        addActor(bg);

        question = new Label(Services.getL18N().l18n(BundleAssets.INVENTORY).get("reassurance"), skin, "paper");
        question.setSize(256,64);
        question.setPosition(GS.WIDTH/2-128, GS.HEIGHT/2-32, Align.bottomLeft);
        addActor(question);

        buttonNo = new TextButton(Services.getL18N().l18n(BundleAssets.GENERAL).get("no"), skin, "button-sandstone");
        buttonNo.setSize(64,24);
        buttonNo.setPosition(GS.WIDTH/2+2,40,Align.bottomLeft);
        buttonNo.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove();
            }
        });
        addActor(buttonNo);

        buttonYes = new TextButton(Services.getL18N().l18n(BundleAssets.GENERAL).get("yes"), skin, "button-sandstone");
        buttonYes.setSize(64,24);
        buttonYes.setPosition(GS.WIDTH/2-2,40,Align.bottomRight);
        addActor(buttonYes);
    }
}
