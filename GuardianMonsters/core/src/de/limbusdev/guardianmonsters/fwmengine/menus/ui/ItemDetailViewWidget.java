package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Item;

/**
 * Created by georg on 20.02.17.
 */

public class ItemDetailViewWidget extends Group {

    private Label itemName, itemDescription;
    private Image itemImg;
    private Skin skin;

    public ItemDetailViewWidget(Skin skin) {
        super();

        this.skin = skin;

        Label bgLabel = new Label("", skin, "paper");
        bgLabel.setSize(162,200);
        bgLabel.setPosition(0,0,Align.bottomLeft);
        addActor(bgLabel);

        Label itemArea = new Label("", skin, "paper-dark-area");
        itemArea.setSize(40,40);
        itemArea.setPosition(61,156,Align.bottomLeft);
        addActor(itemArea);

        itemImg = new Image(skin.getDrawable("sword-barb-steel"));
        itemImg.setSize(32,32);
        itemImg.setPosition(65,160,Align.bottomLeft);
        addActor(itemImg);

        itemName = new Label("Steely Barb Sword", skin, "paper-border");
        itemName.setSize(156,25);
        itemName.setPosition(4,130,Align.bottomLeft);
        addActor(itemName);

        itemDescription = new Label("A new, stable and shiny sword.", skin, "paper-border");
        itemDescription.setSize(156,64);
        itemDescription.setPosition(4,128,Align.topLeft);
        itemDescription.setWrap(true);
        itemDescription.setAlignment(Align.topLeft);
        addActor(itemDescription);

        ImageButton delete = new ImageButton(skin, "button-delete");
        delete.setPosition(24,160,Align.bottomLeft);
        addActor(delete);

        ImageButton use = new ImageButton(skin, "button-use");
        use.setPosition(106,160,Align.bottomLeft);
        addActor(use);


    }

    public void init(Item item) {
        I18NBundle locale = Services.getL18N().l18n();
        itemName.setText(locale.get(item.getName()));
        itemDescription.setText(locale.get(item.getName()+"-description"));
        itemImg.setDrawable(skin, item.getName());
    }

    public Skin getSkin() {
        return skin;
    }
}
