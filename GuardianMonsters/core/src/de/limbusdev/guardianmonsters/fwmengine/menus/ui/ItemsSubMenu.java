package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.ItemCategoryToolbar;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.ItemDetailViewWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.ItemListWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.KeyItemDetailViewWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.WeaponDetailViewWidget;
import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;

/**
 * Created by Georg Eckert on 17.02.17.
 */

public class ItemsSubMenu extends AInventorySubMenu
    implements ItemCategoryToolbar.ClickListener, ItemListWidget.ClickListener {

    private Inventory inventory;
    private ItemDetailViewWidget detailView;
    private ArrayMap<Integer, AGuardian> team;
    private ItemListWidget itemListWidget;

    public ItemsSubMenu(Skin skin, Inventory inventory, ArrayMap<Integer, AGuardian> team) {
        super(skin);
        this.team = team;
        this.inventory = inventory;

        ItemCategoryToolbar toolbar = new ItemCategoryToolbar(skin, this);
        addActor(toolbar);

        // ......................................................................... SCROLLABLE LIST
        itemListWidget = new ItemListWidget(skin, inventory, this, Item.Category.MEDICINE);
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
                detailView = new ItemDetailViewWidget(getSkin(), inventory, team);
                break;
        }
        detailView.setPosition(264,2, Align.bottomLeft);
        detailView.init(item);
        addActor(detailView);
    }

    // .............................................................................. CLICK LISTENER

    @Override
    public void onMedicineButton() {
        itemListWidget.applyFilter(Item.Category.MEDICINE);
    }

    @Override
    public void onOtherItemsButton() {
        itemListWidget.applyFilter(Item.Category.ALL);
    }

    @Override
    public void onEquipItemsButton() {
        itemListWidget.applyFilter(Item.Category.EQUIPMENT);
    }

    @Override
    public void onKeyItemsButton() {
        itemListWidget.applyFilter(Item.Category.KEY);
    }

    @Override
    public void onChoosingItem(Item item) {
        showItemDetailView(item);
    }

    @Override
    public void refresh() {
        // Do nothing
    }
}
