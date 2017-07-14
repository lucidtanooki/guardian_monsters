package de.limbusdev.guardianmonsters.fwmengine.menus.ui.items;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.I18NBundle;

import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.team.MonsterListWidget;
import de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets.ReassuranceWidget;
import de.limbusdev.guardianmonsters.guardians.items.Equipment;
import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;

/**
 * @author Georg Eckert 2017
 */

public class ItemDetailViewWidget extends Group implements MonsterListWidget.Callbacks{

    // UI
    private Label itemName, itemDescription, itemArea;
    private Image itemImg;
    private Skin skin;
    private ReassuranceWidget reassuranceWidget;
    private ImageButton delete, use;

    // Data
    private Inventory inventory;
    private ArrayMap<Integer, Guardian> team;
    private Item item;


    // ................................................................................. CONSTRCUTOR
    public ItemDetailViewWidget(Skin skin, Inventory inventory, ArrayMap<Integer,Guardian> monsters)  {
        super();

        this.skin       = skin;
        this.inventory  = inventory;
        this.team       = monsters;

        constructLayout();

        delete.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addActor(reassuranceWidget);
            }
        });


        use.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MonsterListWidget monsterListWidget = new MonsterListWidget(getSkin(), team, ItemDetailViewWidget.this, item);
                monsterListWidget.setPosition(-262,0,Align.topLeft);
                addActor(monsterListWidget);
            }
        });
    }



    public void init(Item itemToShow) {
        this.item = itemToShow;
        I18NBundle i18n = Services.getL18N().Inventory();

        itemName.setText(i18n.get(item.getName()));
        itemDescription.setText(i18n.get(item.getName()+"-description"));
        itemImg.setDrawable(skin, item.getName());

        reassuranceWidget.question.setText(i18n.format("reassurance-throwaway", i18n.get(item.getName())));
        reassuranceWidget.buttonYes.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                inventory.takeItemFromInventory(item);
                if(inventory.getItems().containsKey(item)) {
                    reassuranceWidget.remove();
                } else {
                    remove();
                }
            }
        });
    }

    private void constructLayout() {
        reassuranceWidget = new ReassuranceWidget(skin);
        reassuranceWidget.setPosition(-264,0,Align.bottomLeft);

        Label bgLabel = new Label("", skin, "paper");
        bgLabel.setSize(162,200);
        bgLabel.setPosition(0,0,Align.bottomLeft);
        addActor(bgLabel);

        itemArea = new Label("", skin, "paper-dark-area");
        itemArea.setSize(40,40);
        itemArea.setPosition(61,156,Align.bottomLeft);
        addActor(itemArea);

        itemImg = new Image(skin.getDrawable("sword-barb-steel"));
        itemImg.setSize(32,32);
        itemImg.setPosition(65,160,Align.bottomLeft);
        addActor(itemImg);

        itemName = new Label("Item Name", skin, "paper-border");
        itemName.setSize(156,25);
        itemName.setPosition(4,130,Align.bottomLeft);
        addActor(itemName);

        itemDescription = new Label("Item Description", skin, "paper-border");
        itemDescription.setSize(156,64);
        itemDescription.setPosition(4,128,Align.topLeft);
        itemDescription.setWrap(true);
        itemDescription.setAlignment(Align.topLeft);
        addActor(itemDescription);

        delete = new ImageButton(skin, "button-delete");
        delete.setPosition(24,160,Align.bottomLeft);
        addActor(delete);

        use = new ImageButton(skin, "button-use");
        use.setPosition(106,160,Align.bottomLeft);
        addActor(use);
    }

    public Skin getSkin() {
        return skin;
    }

    public ImageButton getDelete() {
        return delete;
    }

    public ImageButton getUse() {
        return use;
    }

    @Override
    public boolean onButton(int i) {
        inventory.takeItemFromInventory(item);
        if(item instanceof Equipment) {
            Item replaced = team.get(i).stat.giveEquipment((Equipment)item);
            if(replaced != null) {
                inventory.putItemInInventory(replaced);
            }
        } else {
            item.apply(team.get(i));
        }

        boolean empty = !(inventory.getItemAmount(item) > 0);
        if(empty) remove();
        return !empty;
    }
}
