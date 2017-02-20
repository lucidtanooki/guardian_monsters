package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.model.Inventory;
import de.limbusdev.guardianmonsters.model.Item;
import de.limbusdev.guardianmonsters.model.ItemInfo;

/**
 * Created by georg on 17.02.17.
 */

public class ItemsSubMenu extends AInventorySubMenu {

    private Inventory inventory;
    private Table itemTable;
    private ItemDetailViewWidget detailView;

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


        scrollPane.setSize(192,200);
        scrollPane.setPosition(2,2);
        scrollPane.setScrollBarPositions(false,true);
        itemListView.setPosition(68,0, Align.bottomLeft);
        itemListView.addActor(scrollPane);
        addActor(itemListView);

        init();
    }

    public void init(Inventory inventory) {
        this.inventory = inventory;

        Array<Button> buttons = new Array<>();
        ButtonGroup<TextButton> btnGroup = new ButtonGroup<>();
        btnGroup.setMinCheckCount(1);
        btnGroup.setMaxCheckCount(1);

        int counter = 0;
        for(final Item i : inventory.getItems().keys()) {
            final ItemInventoryButton item = new ItemInventoryButton(i, getSkin(), "item-button-sandstone");
            inventory.addObserver(item);
            if(counter == 0) item.setChecked(true);
            counter++;
            itemTable.add(item).width(192).height(40);
            buttons.add(item);
            btnGroup.add(item);
            itemTable.row().spaceBottom(1);
            item.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showItemDetailView(i);
                }
            });
        }
    }

    public void init() {
        Inventory inventory = new Inventory();
        inventory.putItemInInventory(new Item.Bread());
        inventory.putItemInInventory(new Item.AngelTear());
        inventory.putItemInInventory(new Item.MedicineBlue());
        inventory.putItemInInventory(new Item.MedicineBlue());
        inventory.putItemInInventory(ItemInfo.getInst().getItem("sword-wood"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("claws-rusty"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("sword-silver"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("sword-knightly-steel"));
        inventory.putItemInInventory(ItemInfo.getInst().getItem("sword-barb-steel"));

        this.init(inventory);
    }

    private void showItemDetailView(Item item)  {
        if(detailView != null) detailView.remove();
        switch(item.getType()) {
            case EQUIPMENT:
                detailView = new WeaponDetailViewWidget(getSkin());
                break;
            default:
                detailView = new ItemDetailViewWidget(getSkin());
                break;
        }
        detailView.setPosition(264,2, Align.bottomLeft);
        detailView.init(item);
        addActor(detailView);
    }
}
