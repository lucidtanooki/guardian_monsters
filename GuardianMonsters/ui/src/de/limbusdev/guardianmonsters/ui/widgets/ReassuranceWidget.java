package de.limbusdev.guardianmonsters.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.Constant;

public class ReassuranceWidget extends Group {

    public TextButton buttonYes, buttonNo;
    public Label question;

    public ReassuranceWidget(Skin skin) {
        super();
        setSize(Constant.WIDTH, Constant.HEIGHT);

        Image bg = new Image(skin.getDrawable("black-a80"));
        bg.setSize(Constant.WIDTH, Constant.HEIGHT);
        bg.setPosition(0,0,Align.bottomLeft);
        addActor(bg);

        question = new Label(Services.I18N().Inventory().get("reassurance"), skin, "paper");
        question.setSize(292,64);
        question.setPosition(Constant.WIDTH/2-(292/2), Constant.HEIGHT/2-32, Align.bottomLeft);
        addActor(question);

        buttonNo = new TextButton(Services.I18N().General().get("no"), skin, "button-sandstone");
        buttonNo.setSize(64,24);
        buttonNo.setPosition(Constant.WIDTH/2+2,40,Align.bottomLeft);
        buttonNo.addListener(new SimpleClickListener(() -> remove()));
        addActor(buttonNo);

        buttonYes = new TextButton(Services.I18N().General().get("yes"), skin, "button-sandstone");
        buttonYes.setSize(64,24);
        buttonYes.setPosition(Constant.WIDTH/2-2,40,Align.bottomRight);
        addActor(buttonYes);
    }
}
