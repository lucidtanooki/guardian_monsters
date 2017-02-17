package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.model.Inventory;
import de.limbusdev.guardianmonsters.model.Item;

/**
 * Created by georg on 17.02.17.
 */

public class ItemsSubMenu extends AInventorySubMenu {

    private Inventory inventory;
    List<Label> itemList;

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

        Image bgImg = new Image(skin.getDrawable("menu-col-bg"));
        bgImg.setPosition(2,2,Align.bottomLeft);
        itemListView.addActor(bgImg);

        itemList = new List<>(skin);
        ScrollPane scrollPane = new ScrollPane(itemList, getSkin());
        init();
        scrollPane.setSize(136,196);
        scrollPane.setPosition(2,2);
        scrollPane.setScrollBarPositions(false,true);
        itemListView.setPosition(74,0, Align.bottomLeft);
        itemListView.addActor(scrollPane);
        addActor(itemListView);
    }

    public void init(Inventory inventory) {
        this.inventory = inventory;

        Array<Label> labels = new Array<>();
        for(Item i : inventory.getItems().keys()) {
            Label l = new Label(i.getName(), getSkin(), "list-item");
            labels.add(l);
        }
        itemList.setItems(labels);
    }

    public void init() {
        Inventory inventory = new Inventory();
        inventory.putItemInInventory(new Item("Sword"));
        inventory.putItemInInventory(new Item("Potion"));
        inventory.putItemInInventory(new Item("Potion"));
        inventory.putItemInInventory(new Item("Antidot"));
        inventory.putItemInInventory(new Item("Antidot"));
        inventory.putItemInInventory(new Item("Antidot"));
        inventory.putItemInInventory(new Item("Antidot"));
        inventory.putItemInInventory(new Item("Antidot"));
        inventory.putItemInInventory(new Item("Antidot"));
        inventory.putItemInInventory(new Item("Antidot"));
        inventory.putItemInInventory(new Item("Antidot"));
        inventory.putItemInInventory(new Item("Antidot"));
        inventory.putItemInInventory(new Item("Antidot"));
        inventory.putItemInInventory(new Item("Antidot"));

        this.init(inventory);
    }
}
