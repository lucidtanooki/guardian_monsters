package de.limbusdev.guardianmonsters.inventory.ui.widgets.items;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;

import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.widgets.Callback;
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener;

/**
 * @author Georg Eckert 2017
 */

public class ItemDetailViewWidget extends Group {

    // UI
    private Label itemName, itemDescription, itemArea;
    private Image itemImg;
    private Skin skin;
    private ImageButton use;
    private Item item;


    // ................................................................................. CONSTRCUTOR
    public ItemDetailViewWidget(Skin skin, Item item, Callback callback)
    {
        super();

        this.skin = skin;

        constructLayout();

        use.addListener(new SimpleClickListener(callback::onClick));
    }



    public void init(Item itemToShow)
    {
        this.item = itemToShow;
        I18NBundle i18n = Services.getL18N().Inventory();

        itemName.setText(i18n.get(item.getName()));
        itemDescription.setText(i18n.get(item.getName()+"-description"));
        itemImg.setDrawable(Services.getMedia().getItemDrawable(itemToShow.getName()));
    }

    private void constructLayout()
    {
        Label bgLabel = new Label("", skin, "paper");
        bgLabel.setSize(162,200);
        bgLabel.setPosition(0,0,Align.bottomLeft);
        addActor(bgLabel);

        itemArea = new Label("", skin, "paper-dark-area");
        itemArea.setSize(40,40);
        itemArea.setPosition(61,156,Align.bottomLeft);
        addActor(itemArea);

        itemImg = new Image(skin.getDrawable("sword-barb-steel"));
        itemImg.setSize(32,32);
        itemImg.setPosition(65,160,Align.bottomLeft);
        addActor(itemImg);

        itemName = new Label("Item Name", skin, "paper-border");
        itemName.setSize(156,25);
        itemName.setPosition(4,130,Align.bottomLeft);
        addActor(itemName);

        itemDescription = new Label("Item Description", skin, "paper-border");
        itemDescription.setSize(156,64);
        itemDescription.setPosition(4,128,Align.topLeft);
        itemDescription.setWrap(true);
        itemDescription.setAlignment(Align.topLeft);
        addActor(itemDescription);

        use = new ImageButton(skin, "button-use");
        use.setPosition(106,160,Align.bottomLeft);
        addActor(use);
    }

    public Skin getSkin() {
        return skin;
    }

    public ImageButton getUse() {
        return use;
    }
}
