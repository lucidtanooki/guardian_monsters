package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import de.limbusdev.guardianmonsters.model.Inventory;
import de.limbusdev.guardianmonsters.model.Item;

/**
 * Created by georg on 17.02.17.
 */

public class ItemsSubMenu extends AInventorySubMenu {

    private Inventory inventory;
    private Table itemTable;

    public ItemsSubMenu(Skin skin) {
        super(skin);

        ItemCategoryToolbar.CallbackHandler callbacks = new ItemCategoryToolbar.CallbackHandler() {
            @Override
            public void onMedicineButton() {
                // TODO
            }

            @Override
            public void onOtherItemsButton() {
                // TODO
            }

            @Override
            public void onEquipItemsButton() {
                // TODO
            }

            @Override
            public void onKeyItemsButton() {
                // TODO
            }
        };

        ItemCategoryToolbar toolbar = new ItemCategoryToolbar(skin, callbacks);
        addActor(toolbar);


        // ......................................................................... SCROLLABLE LIST
        Group itemListView = new Group();

        itemTable = new Table();

        ScrollPane scrollPane = new ScrollPane(itemTable, getSkin());
        init();

        scrollPane.setSize(192,196);
        scrollPane.setPosition(2,2);
        scrollPane.setScrollBarPositions(false,true);
        itemListView.setPosition(68,0, Align.bottomLeft);
        itemListView.addActor(scrollPane);
        addActor(itemListView);

    }

    public void init(Inventory inventory) {
        this.inventory = inventory;

        Array<Button> buttons = new Array<>();
        ButtonGroup<TextButton> btnGroup = new ButtonGroup<>();
        btnGroup.setMinCheckCount(1);
        btnGroup.setMaxCheckCount(1);

        int counter = 0;
        for(Item i : inventory.getItems().keys()) {
            ItemInventoryButton item = new ItemInventoryButton(i, getSkin(), "item-button-sandstone");
            inventory.addObserver(item);
            if(counter == 0) item.setChecked(true);
            counter++;
            itemTable.add(item).width(192).height(40);
            buttons.add(item);
            btnGroup.add(item);
            itemTable.row().spaceBottom(1);
        }
    }

    public void init() {
        Inventory inventory = new Inventory();
        inventory.putItemInInventory(new Item("tent"));
        inventory.putItemInInventory(new Item("potion-black"));
        inventory.putItemInInventory(new Item("potion-red"));
        inventory.putItemInInventory(new Item("potion-green"));
        inventory.putItemInInventory(new Item("potion-blue"));
        inventory.putItemInInventory(new Item("guardian-crystal-water"));
        inventory.putItemInInventory(new Item("antivenom"));

        this.init(inventory);
    }
}
