package de.limbusdev.guardianmonsters.ui.widgets;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.items.ItemSignal;

/**
 * @author Georg Eckert 2017
 */

public class ItemListWidget extends Group implements Listener<ItemSignal> {

    private Table itemTable;
    private Inventory inventory;
    private Skin skin;
    private int lastChosenItem=0;
    private ClickListener clickListener;
    private Array<Item.Category> currentFilters;

    public interface ClickListener {
        void onChoosingItem(Item item);
    }

    public ItemListWidget(Skin skin, Inventory inventory, ClickListener handler, Array<Item.Category> filters)
    {
        this.inventory = inventory;
        this.skin = skin;
        this.clickListener = handler;
        this.currentFilters = filters;

        itemTable = new Table();
        itemTable.align(Align.topLeft);

        ScrollPane scrollPane = new ScrollPane(itemTable, skin);


        scrollPane.setSize(192,200);
        scrollPane.setPosition(2,2);
        scrollPane.setScrollBarPositions(false,true);
        addActor(scrollPane);

        init(inventory, filters);
    }

    private void init(Inventory inventory, Array<Item.Category> filters)
    {
        itemTable.clearChildren();
        this.inventory = inventory;
        inventory.add(this);

        Array<Button> buttons = new Array<>();
        final ButtonGroup<TextButton> btnGroup = new ButtonGroup<>();
        btnGroup.setMinCheckCount(1);
        btnGroup.setMaxCheckCount(1);

        // Only proceed of there are any items
        if(inventory.getItems().size > 0) {
            int counter = 0;
            for (final Item i : inventory.getItems().keys()) {

                if(filters.contains(Item.Category.ALL, false) || filters.contains(i.getCategory(), false)) {
                    final ItemInventoryButton item = new ItemInventoryButton(i, skin, "item-button-sandstone", inventory);
                    inventory.add(this);
                    itemTable.add(item).width(192).height(40);
                    buttons.add(item);
                    btnGroup.add(item);
                    itemTable.row().spaceBottom(1);

                    item.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            clickListener.onChoosingItem(i);
                            lastChosenItem = btnGroup.getCheckedIndex();
                        }
                    });

                    if (counter == 0) item.setChecked(true);
                    counter++;
                }
            }

            if(btnGroup.getButtons().size > 0) {
                if (lastChosenItem > btnGroup.getButtons().size - 1) {
                    lastChosenItem = btnGroup.getButtons().size - 1;
                }

                btnGroup.setChecked(btnGroup.getButtons().get(lastChosenItem).getText().toString());
            }
        }
    }

    public void applyFilter(Item.Category filter)
    {
        Array<Item.Category> filters = new Array<>();
        filters.add(filter);
        applyFilter(filters);
    }

    public void applyFilter(Array<Item.Category> filters) {
        lastChosenItem = 0;
        currentFilters = filters;
        init(inventory, filters);
    }

    @Override
    public void receive(Signal<ItemSignal> signal, ItemSignal itemSignal) {
        if(itemSignal.message == ItemSignal.Message.DELETED) {
            // Refresh Inventory List, if last item of it's kind got removed
            if (!inventory.containsItem(itemSignal.item)) {
                init(inventory, currentFilters);
            }
        }
    }
}
