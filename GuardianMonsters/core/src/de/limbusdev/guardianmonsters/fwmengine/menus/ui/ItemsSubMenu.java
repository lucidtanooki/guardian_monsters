package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.menus.ui.*;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.ItemCategoryToolbar;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.KeyItemDetailViewWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.WeaponDetailViewWidget;
import de.limbusdev.guardianmonsters.model.Inventory;
import de.limbusdev.guardianmonsters.model.Item;
import de.limbusdev.guardianmonsters.model.Monster;

/**
 * Created by Georg Eckert on 17.02.17.
 */

public class ItemsSubMenu extends AInventorySubMenu {

    private Inventory inventory;
    private de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.ItemDetailViewWidget detailView;
    private ArrayMap<Integer, Monster> team;
    private de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.ItemListWidget itemListWidget;

    public ItemsSubMenu(Skin skin, Inventory inventory, ArrayMap<Integer, Monster> team) {
        super(skin);
        this.team = team;
        this.inventory = inventory;

        ItemCategoryToolbar.CallbackHandler callbacks = new ItemCategoryToolbar.CallbackHandler() {
            @Override
            public void onMedicineButton() {
                itemListWidget.applyFilter(Item.CATEGORY.MEDICINE);
            }

            @Override
            public void onOtherItemsButton() {
                itemListWidget.applyFilter(Item.CATEGORY.ALL);
            }

            @Override
            public void onEquipItemsButton() {
                itemListWidget.applyFilter(Item.CATEGORY.EQUIPMENT);
            }

            @Override
            public void onKeyItemsButton() {
                itemListWidget.applyFilter(Item.CATEGORY.KEY);
            }
        };

        ItemCategoryToolbar toolbar = new ItemCategoryToolbar(skin, callbacks);
        addActor(toolbar);


        // ......................................................................... SCROLLABLE LIST
        de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.ItemListWidget.CallbackHandler ilch = new de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.ItemListWidget.CallbackHandler() {
            @Override
            public void onChoosingItem(Item item) {
                showItemDetailView(item);
            }
        };
        itemListWidget = new de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.ItemListWidget(skin, inventory, ilch, Item.CATEGORY.MEDICINE);
        itemListWidget.setPosition(68,0,Align.bottomLeft);

        addActor(itemListWidget);
    }



    private void showItemDetailView(Item item)  {
        if(detailView != null) detailView.remove();
        switch(item.getCategory()) {
            case EQUIPMENT:
                detailView = new WeaponDetailViewWidget(getSkin(), inventory, team);
                break;
            case KEY:
                detailView = new KeyItemDetailViewWidget(getSkin(), inventory, team);
                break;
            default:
                detailView = new de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.ItemDetailViewWidget(getSkin(), inventory, team);
                break;
        }
        detailView.setPosition(264,2, Align.bottomLeft);
        detailView.init(item);
        addActor(detailView);
    }

}
