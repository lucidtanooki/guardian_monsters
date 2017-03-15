package de.limbusdev.guardianmonsters.fwmengine.menus.ui.items;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.items.Inventory;
import de.limbusdev.guardianmonsters.model.items.Item;
import de.limbusdev.guardianmonsters.model.items.ItemSignal;

/**
 * @author Georg Eckert 2017
 */

public class ItemInventoryButton extends TextButton implements Listener<ItemSignal> {

    private Item item;
    private Label counter;
    private Inventory inventory;

    public ItemInventoryButton(Item item, Skin skin, Inventory inventory) {
        super(Services.getL18N().l18n(BundleAssets.INVENTORY).get(item.getName()), skin);
        construct(item, inventory);
    }

    public ItemInventoryButton(Item item, Skin skin, String styleName, Inventory inventory) {
        super(Services.getL18N().l18n(BundleAssets.INVENTORY).get(item.getName()), skin, styleName);
        construct(item, inventory);
    }

    public ItemInventoryButton(Item item, TextButtonStyle style, Inventory inventory) {
        super(Services.getL18N().l18n(BundleAssets.INVENTORY).get(item.getName()), style);
        construct(item, inventory);
    }


    /**
     * Adds preview image and counter to the button
     * @param item
     */
    private void construct(Item item, Inventory inventory) {

        this.inventory = inventory;
        this.item = item;
        inventory.add(this);

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
    public void receive(Signal<ItemSignal> signal, ItemSignal itemSignal) {
        if(this.item.equals(itemSignal.item)) {
            Integer amount = inventory.getItemAmount(item);
            if (amount > 0) {
                counter.setText(amount.toString());
            } else {
                remove();
            }
        }
    }
}
