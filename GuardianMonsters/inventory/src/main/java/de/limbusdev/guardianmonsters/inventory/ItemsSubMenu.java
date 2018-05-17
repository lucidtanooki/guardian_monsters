package main.java.de.limbusdev.guardianmonsters.inventory;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import java.util.Observable;

import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.ui.widgets.ItemListWidget;
import main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.items.ItemCategoryToolbar;
import main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.items.ItemApplicationWidget;
import main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.items.KeyItemDetailViewWidget;
import main.java.de.limbusdev.guardianmonsters.inventory.ui.widgets.items.WeaponDetailViewWidget;

/**
 * Created by Georg Eckert on 17.02.17.
 */

public class ItemsSubMenu extends AInventorySubMenu
    implements ItemCategoryToolbar.ClickListener, ItemListWidget.ClickListener {

    private Inventory inventory;
    private ItemApplicationWidget detailView;
    private ArrayMap<Integer, AGuardian> team;
    private ItemListWidget itemListWidget;

    public ItemsSubMenu(Skin skin, Inventory inventory, ArrayMap<Integer, AGuardian> team) {

        super(skin);
        this.team = team;
        this.inventory = inventory;

        ItemCategoryToolbar toolbar = new ItemCategoryToolbar(skin, this);
        addActor(toolbar);

        // ......................................................................... SCROLLABLE LIST
        Array<Item.Category> filters = new Array<>();
        filters.add(Item.Category.MEDICINE);
        itemListWidget = new ItemListWidget(skin, inventory, this, filters);
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
                detailView = new ItemApplicationWidget(getSkin(), inventory, team);
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

    @Override
    protected void layout(Skin skin) {

    }
}
