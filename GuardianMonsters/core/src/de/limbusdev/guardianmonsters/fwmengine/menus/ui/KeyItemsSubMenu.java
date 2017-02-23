package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.geometry.IntVec2;
import de.limbusdev.guardianmonsters.model.Inventory;
import de.limbusdev.guardianmonsters.model.Item;
import de.limbusdev.guardianmonsters.model.ItemInfo;
import de.limbusdev.guardianmonsters.utils.GS;

/**
 * Created by georg on 23.02.17.
 */

public class KeyItemsSubMenu extends AInventorySubMenu {

    private ArrayMap<Item, IntVec2> keyItemPositions;
    private IntVec2 relictRootPos = new IntVec2(GS.WIDTH/2-70,30);


    public KeyItemsSubMenu(Skin skin, Inventory inventory) {
        super(skin);

        Label bgLabel = new Label("", skin, "paper");
        bgLabel.setSize(424, 200);
        bgLabel.setPosition(2,2,Align.bottomLeft);
        addActor(bgLabel);

        Image relicsBgImg = new Image(skin.getDrawable("element-relics-bg"));
        relicsBgImg.setPosition(GS.WIDTH/2-70,30, Align.bottomLeft);
        addActor(relicsBgImg);

        keyItemPositions = new ArrayMap<>();

        keyItemPositions.put(ItemInfo.getInst().getItem("relict-earth"), new IntVec2(70,5));
        keyItemPositions.put(ItemInfo.getInst().getItem("relict-flame"), new IntVec2(6,5));
        keyItemPositions.put(ItemInfo.getInst().getItem("relict-lightning"), new IntVec2(22,26));
        keyItemPositions.put(ItemInfo.getInst().getItem("relict-arthropoda"), new IntVec2(86,26));
        keyItemPositions.put(ItemInfo.getInst().getItem("relict-mountain"), new IntVec2(54,45));
        keyItemPositions.put(ItemInfo.getInst().getItem("relict-frost"), new IntVec2(38,66));
        keyItemPositions.put(ItemInfo.getInst().getItem("relict-spirit"), new IntVec2(6,23));
        keyItemPositions.put(ItemInfo.getInst().getItem("relict-water"), new IntVec2(70,24));
        keyItemPositions.put(ItemInfo.getInst().getItem("relict-air"), new IntVec2(38,24));
        keyItemPositions.put(ItemInfo.getInst().getItem("relict-forest"), new IntVec2(102,23));
        keyItemPositions.put(ItemInfo.getInst().getItem("relict-lindworm"), new IntVec2(38,79));
        keyItemPositions.put(ItemInfo.getInst().getItem("relict-lindworm"), new IntVec2(38,79));
        keyItemPositions.put(ItemInfo.getInst().getItem("relict-demon"), new IntVec2(70,79));

        for(Item key : keyItemPositions.keys()) {
            if(inventory.getItems().containsKey(key)) {
                Image img = new Image(skin.getDrawable(key.getName() + "-big"));
                IntVec2 pos = keyItemPositions.get(key);
                img.setPosition(pos.x + relictRootPos.x, pos.y + relictRootPos.y, Align.bottomLeft);
                addActor(img);
            }
        }


    }



}
