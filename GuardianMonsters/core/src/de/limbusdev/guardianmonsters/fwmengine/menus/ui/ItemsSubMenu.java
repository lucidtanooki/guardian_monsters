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
import com.badlogic.gdx.utils.ArrayMap;

import java.util.Observable;
import java.util.Observer;

import de.limbusdev.guardianmonsters.model.Inventory;
import de.limbusdev.guardianmonsters.model.Item;
import de.limbusdev.guardianmonsters.model.Monster;

/**
 * Created by Georg Eckert on 17.02.17.
 */

public class ItemsSubMenu extends AInventorySubMenu implements Observer {

    private Inventory inventory;
    private Table itemTable;
    private ItemDetailViewWidget detailView;
    private int lastChosenItem=0;
    private ArrayMap<Integer, Monster> team;

    public ItemsSubMenu(Skin skin, Inventory inventory, ArrayMap<Integer, Monster> team) {
        super(skin);
        this.team = team;

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
        itemTable.align(Align.topLeft);

        ScrollPane scrollPane = new ScrollPane(itemTable, getSkin());


        scrollPane.setSize(192,200);
        scrollPane.setPosition(2,2);
        scrollPane.setScrollBarPositions(false,true);
        itemListView.setPosition(68,0, Align.bottomLeft);
        itemListView.addActor(scrollPane);
        addActor(itemListView);

        init(inventory);
    }

    private void init(Inventory inventory) {
        itemTable.clearChildren();
        this.inventory = inventory;
        inventory.addObserver(this);

        Array<Button> buttons = new Array<>();
        final ButtonGroup<TextButton> btnGroup = new ButtonGroup<>();
        btnGroup.setMinCheckCount(1);
        btnGroup.setMaxCheckCount(1);

        // Only proceed of there are any items
        if(inventory.getItems().size > 0) {
            int counter = 0;
            for (final Item i : inventory.getItems().keys()) {
                final ItemInventoryButton item = new ItemInventoryButton(i, getSkin(), "item-button-sandstone", inventory);
                inventory.addObserver(item);
                if (counter == 0) item.setChecked(true);
                counter++;
                itemTable.add(item).width(192).height(40);
                buttons.add(item);
                btnGroup.add(item);
                itemTable.row().spaceBottom(1);

                item.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        showItemDetailView(i);
                        lastChosenItem = btnGroup.getCheckedIndex();
                    }
                });
            }

            if (lastChosenItem > btnGroup.getButtons().size - 1) {
                lastChosenItem = btnGroup.getButtons().size - 1;
            }

            btnGroup.setChecked(btnGroup.getButtons().get(lastChosenItem).getText().toString());
        }
    }

    private void showItemDetailView(Item item)  {
        if(detailView != null) detailView.remove();
        switch(item.getType()) {
            case EQUIPMENT:
                detailView = new WeaponDetailViewWidget(getSkin(), inventory, team);
                break;
            default:
                detailView = new ItemDetailViewWidget(getSkin(), inventory, team);
                break;
        }
        detailView.setPosition(264,2, Align.bottomLeft);
        detailView.init(item);
        addActor(detailView);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof Inventory && arg instanceof Item) {
            // If item got deleted completely
            if(!inventory.getItems().containsKey((Item)arg)) {
                init(inventory);
            }
        }
    }
}
