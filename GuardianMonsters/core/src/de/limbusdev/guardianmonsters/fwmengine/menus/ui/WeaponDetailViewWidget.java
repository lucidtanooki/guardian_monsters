package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.model.Equipment;
import de.limbusdev.guardianmonsters.model.Item;

/**
 * Created by georg on 20.02.17.
 */

public class WeaponDetailViewWidget extends ItemDetailViewWidget {

    private ArrayMap<String,Label> valueLabels;

    public WeaponDetailViewWidget(Skin skin) {
        super(skin);

        int offX = 4;
        int offY = 74;
        int gap = 18;

        valueLabels = new ArrayMap<>();
        Label value;
        String[] labels = {"hp", "mp", "exp", "pstr", "pdef", "mstr", "mdef", "speed"};
        Image key;
        int reset=0;
        for(int i=0; i<labels.length; i++) {

            if(i > 0 && i % 3 == 0) {
                offX += 56;
                reset=i;
            }

            key = new Image(skin.getDrawable("stats-symbol-" + labels[i]));
            key.setSize(16,16);
            key.setPosition(offX, offY-gap*(i+1-reset), Align.topLeft);
            addActor(key);

            value = new Label("0", skin, "default");
            value.setPosition(offX+17, offY-gap*(i+1-reset), Align.topLeft);
            addActor(value);
            valueLabels.put(labels[i], value);
        }
    }

    @Override
    public void init(Item item) {
        super.init(item);

        Equipment eq = (Equipment) item;

        int value = eq.getAddsHP();
        String sign = value >= 0 ? "+" : "-";
        valueLabels.get("hp").setText(sign + Integer.toString(value) + "%");

        value = eq.getAddsMP();
        sign = value >= 0 ? "+" : "-";
        valueLabels.get("mp").setText(sign + Integer.toString(value) + "%");

        value = eq.getAddsPStr();
        sign = value >= 0 ? "+" : "-";
        valueLabels.get("pstr").setText(sign + Integer.toString(value));

        value = eq.getAddsPDef();
        sign = value >= 0 ? "+" : "-";
        valueLabels.get("pdef").setText(sign + Integer.toString(value));

        value = eq.getAddsMStr();
        sign = value >= 0 ? "+" : "-";
        valueLabels.get("mstr").setText(sign + Integer.toString(value));

        value = eq.getAddsMDef();
        sign = value >= 0 ? "+" : "-";
        valueLabels.get("mdef").setText(sign + Integer.toString(value));

        value = eq.getAddsSpeed();
        sign = value >= 0 ? "+" : "-";
        valueLabels.get("speed").setText(sign + Integer.toString(value));
    }
}
