package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Inventory;
import de.limbusdev.guardianmonsters.model.Item;

/**
 * Created by georg on 19.02.17.
 */

public class ItemInventoryButton extends TextButton implements Observer {

    private Item item;
    private Label counter;
    private Inventory inventory;

    public ItemInventoryButton(Item item, Skin skin, Inventory inventory) {
        super(Services.getL18N().l18n(BundleAssets.INVENTORY).get(item.getName()), skin);
        this.inventory = inventory;
        augmentButton(item);
    }

    public ItemInventoryButton(Item item, Skin skin, String styleName, Inventory inventory) {
        super(Services.getL18N().l18n(BundleAssets.INVENTORY).get(item.getName()), skin, styleName);
        this.inventory = inventory;
        augmentButton(item);
    }

    public ItemInventoryButton(Item item, TextButtonStyle style, Inventory inventory) {
        super(Services.getL18N().l18n(BundleAssets.INVENTORY).get(item.getName()), style);
        this.inventory = inventory;
        augmentButton(item);
    }


    /**
     * Adds preview image and counter to the button
     * @param item
     */
    private void augmentButton(Item item) {

        this.item = item;

        getLabel().setAlignment(Align.left);
        counter = new Label(inventory.getItems().get(item).toString(), getSkin());
        counter.getStyle().font = getStyle().font;
        counter.setAlignment(Align.center);
        add(counter).width(32);

        Image itemImg = new Image(getSkin().getDrawable(item.getName()));
        itemImg.setAlign(Align.center);
        add(itemImg).width(32).height(32);

    }

    public Item getItem() {
        return item;
    }

    @Override
    public void update(Observable o, Object arg) {
        // if the received update is from Inventory and the given object is an Item
        if (o instanceof Inventory && arg instanceof Item) {
            Item updatedItem = (Item) arg;

            if (updatedItem.equals(this.item)) {
                Integer amount = inventory.getItemAmount(item);

                if (amount > 0) {
                    counter.setText(amount.toString());
                } else {
                    remove();
                }
            }
        }
    }
}
