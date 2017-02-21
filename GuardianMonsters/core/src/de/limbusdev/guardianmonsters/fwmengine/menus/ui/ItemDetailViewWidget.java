package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

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

import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Inventory;
import de.limbusdev.guardianmonsters.model.Item;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Created by georg on 20.02.17.
 */

public class ItemDetailViewWidget extends Group {

    private Label itemName, itemDescription;
    private Image itemImg;
    private Skin skin;
    private ReassuranceWidget reassuranceWidget;
    private Inventory inventory;
    private ArrayMap<Integer, Monster> team;
    private Item item;


    public ItemDetailViewWidget(Skin skin, Inventory inventory, ArrayMap<Integer,Monster> monsters)  {
        super();

        this.skin = skin;
        this.inventory = inventory;
        this.team = monsters;

        reassuranceWidget = new ReassuranceWidget(skin);
        reassuranceWidget.setPosition(-264,0,Align.bottomLeft);

        Label bgLabel = new Label("", skin, "paper");
        bgLabel.setSize(162,200);
        bgLabel.setPosition(0,0,Align.bottomLeft);
        addActor(bgLabel);

        Label itemArea = new Label("", skin, "paper-dark-area");
        itemArea.setSize(40,40);
        itemArea.setPosition(61,156,Align.bottomLeft);
        addActor(itemArea);

        itemImg = new Image(skin.getDrawable("sword-barb-steel"));
        itemImg.setSize(32,32);
        itemImg.setPosition(65,160,Align.bottomLeft);
        addActor(itemImg);

        itemName = new Label("Steely Barb Sword", skin, "paper-border");
        itemName.setSize(156,25);
        itemName.setPosition(4,130,Align.bottomLeft);
        addActor(itemName);

        itemDescription = new Label("A new, stable and shiny sword.", skin, "paper-border");
        itemDescription.setSize(156,64);
        itemDescription.setPosition(4,128,Align.topLeft);
        itemDescription.setWrap(true);
        itemDescription.setAlignment(Align.topLeft);
        addActor(itemDescription);

        ImageButton delete = new ImageButton(skin, "button-delete");
        delete.setPosition(24,160,Align.bottomLeft);
        delete.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                addActor(reassuranceWidget);
            }
        });
        addActor(delete);

        final QuickOverviewGuardianList.CallbackHandler handler = new QuickOverviewGuardianList.CallbackHandler() {
            @Override
            public void onButton(int i) {
                item.apply(team.get(i));
            }
        };

        ImageButton use = new ImageButton(skin, "button-use");
        use.setPosition(106,160,Align.bottomLeft);
        use.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                QuickOverviewGuardianList monsterList = new QuickOverviewGuardianList(getSkin(), team, handler);
                monsterList.setPosition(-262,0,Align.topLeft);
                addActor(monsterList);
            }
        });
        addActor(delete);
        addActor(use);


    }

    public void init(Item itemToShow) {
        this.item = itemToShow;

        I18NBundle locale = Services.getL18N().l18n();
        itemName.setText(locale.get(item.getName()));
        itemDescription.setText(locale.get(item.getName()+"-description"));
        itemImg.setDrawable(skin, item.getName());

        reassuranceWidget.question.setText(locale.format("reassurance-throwaway", locale.get(item.getName())));
        reassuranceWidget.buttonYes.addListener(new ClickListener() {
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

    public Skin getSkin() {
        return skin;
    }
}
