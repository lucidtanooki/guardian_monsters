package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleSystem;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.ItemDetailViewWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.ItemListWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.team.MonsterListWidget;
import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;
import de.limbusdev.guardianmonsters.Constant;

/**
 * ItemChoice
 *
 * @author Georg Eckert 2017
 */

public class ItemChoice extends Group {

    private ArrayMap<Integer, Guardian> team;
    private Inventory inventory;
    private MonsterListWidget guardianList;
    private Item chosenItem;
    private BattleSystem battleSystem;
    private ItemDetailViewWidget detailViewWidget;

    public ItemChoice(Skin skin, Inventory inventory, ArrayMap<Integer,Guardian> team, BattleSystem battleSystem) {
        this.team = team;
        this.inventory = inventory;
        this.battleSystem = battleSystem;

        setSize(Constant.WIDTH, Constant.HEIGHT);
        setPosition(0, 0, Align.bottomLeft);
        Image overlay = new Image(Services.getUI().getInventorySkin().getDrawable("black-a80"));
        overlay.setSize(Constant.WIDTH, Constant.HEIGHT);
        overlay.setPosition(0, 0, Align.bottomLeft);
        addActor(overlay);

        setUp();

        ImageButton back = new ImageButton(skin, "button-back");
        back.setPosition(Constant.WIDTH-2, 2, Align.bottomRight);
        addActor(back);
        back.addListener(
            new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    remove();
                }
            }
        );

        detailViewWidget = new ItemDetailViewWidget(skin, inventory, team);
        detailViewWidget.setPosition(20,2,Align.bottomLeft);
        detailViewWidget.getDelete().setVisible(false);
    }

    private void setUp () {
        final MonsterListWidget.Callbacks callbacks = new MonsterListWidget.Callbacks() {
            @Override
            public boolean onButton(int i) {
                inventory.takeItemFromInventory(chosenItem);
                chosenItem.apply(team.get(i));
                remove();
                battleSystem.doNothing();
                return false;
            }
        };

        ItemListWidget.ClickListener clicks = new ItemListWidget.ClickListener() {
            @Override
            public void onChoosingItem(Item item) {
                chosenItem = item;
                detailViewWidget.init(item);
                addActor(detailViewWidget);
                detailViewWidget.getUse().clearListeners();
                detailViewWidget.getUse().addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if(guardianList != null) guardianList.remove();
                        guardianList = new MonsterListWidget(Services.getUI().getInventorySkin(), team, callbacks, chosenItem);
                        addActor(guardianList);
                        detailViewWidget.remove();
                    }
                });
            }
        };

        ItemListWidget itemList = new ItemListWidget(Services.getUI().getInventorySkin(), inventory, clicks, Item.Category.MEDICINE);
        itemList.setSize(140, Constant.HEIGHT);
        itemList.setPosition(Constant.WIDTH/2-32, 0, Align.bottomLeft);
        addActor(itemList);
    }
}
