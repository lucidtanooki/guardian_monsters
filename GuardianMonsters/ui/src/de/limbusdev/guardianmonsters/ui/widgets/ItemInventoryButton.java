package de.limbusdev.guardianmonsters.ui.widgets;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.items.ItemSignal;
import de.limbusdev.guardianmonsters.services.Services;

/**
 * @author Georg Eckert 2017
 */

public class ItemInventoryButton extends TextButton implements Listener<ItemSignal> {

    private Item item;
    private Label counter;
    private Inventory inventory;

    public ItemInventoryButton(Item item, Skin skin, Inventory inventory) {
        super(Services.I18N().Inventory().get(item.getName()), skin);
        construct(item, inventory);
    }

    public ItemInventoryButton(Item item, Skin skin, String styleName, Inventory inventory) {
        super(Services.I18N().Inventory().get(item.getName()), skin, styleName);
        construct(item, inventory);
    }

    public ItemInventoryButton(Item item, TextButtonStyle style, Inventory inventory) {
        super(Services.I18N().Inventory().get(item.getName()), style);
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

        Drawable itemDrawable = Services.Media().getItemDrawable(item.getName());
        Image itemImg = new Image(itemDrawable);
        itemImg.setAlign(Align.center);
        add(itemImg).width(32).height(32);

    }

    public Item getItem() {
        return item;
    }


    @Override
    public void receive(Signal<ItemSignal> signal, ItemSignal itemSignal) {
        if(this.item.equals(itemSignal.getItem())) {
            Integer amount = inventory.getAmountOf(item);
            if (amount > 0) {
                counter.setText(amount.toString());
            } else {
                remove();
            }
        }
    }
}
