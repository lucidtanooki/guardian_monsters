package de.limbusdev.guardianmonsters.inventory.ui.widgets.items;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

/**
 * @author Georg Eckert 2017
 */

public class ItemCategoryToolbar extends Group {

    private ButtonGroup<ImageButton> buttonGroup;
    private ClickListener callbacks;

    public ItemCategoryToolbar(Skin skin, final ClickListener handler) {
        super();
        this.callbacks = handler;

        setSize(82,204);

        // ....................................................................................

        int offX = 2;
        int offY = 204-2;
        int gap = 36;

        // ....................................................................................
        ImageButton medicine = new ImageButton(skin, "b-toolbar-side-medicine");
        medicine.setPosition(offX, offY, Align.topLeft);
        medicine.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onMedicineButton();
            }
        });
        addActor(medicine);
        medicine.setChecked(true);

        // ....................................................................................
        ImageButton otherItems = new ImageButton(skin, "b-toolbar-side-other");
        otherItems.setPosition(offX, offY-gap*1, Align.topLeft);
        otherItems.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onOtherItemsButton();
            }
        });
        addActor(otherItems);

        // ....................................................................................
        ImageButton equip = new ImageButton(skin, "b-toolbar-side-equip");
        equip.setPosition(offX, offY-gap*2, Align.topLeft);
        equip.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onEquipItemsButton();
            }
        });
        addActor(equip);

        // ....................................................................................
        ImageButton keyItems = new ImageButton(skin, "b-toolbar-side-key");
        keyItems.setPosition(offX, offY-gap*3, Align.topLeft);
        keyItems.addListener(new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                callbacks.onKeyItemsButton();
            }
        });
        addActor(keyItems);

        buttonGroup = new ButtonGroup<>(medicine,otherItems,equip,keyItems);
        buttonGroup.setMaxCheckCount(1);
        buttonGroup.setMinCheckCount(1);

    }

    public interface ClickListener {
        void onMedicineButton();
        void onOtherItemsButton();
        void onEquipItemsButton();
        void onKeyItemsButton();
    }

}
