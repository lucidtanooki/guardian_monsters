package de.limbusdev.guardianmonsters.inventory.ui.widgets.items;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;
import de.limbusdev.guardianmonsters.services.Services;

/**
 * @author Georg Eckert 2017
 */

public class WeaponDetailViewWidget extends ItemApplicationWidget
{

    private ArrayMap<String, Label> valueLabels;

    public WeaponDetailViewWidget(Inventory inventory, Team team)
    {
        super(inventory, team);

        Skin skin = Services.getUI().getInventorySkin();

        int offX = 4;
        int offY = 74;
        int gap = 18;

        valueLabels = new ArrayMap<>();
        Label value;
        String[] labels = {"hp", "mp", "exp", "pstr", "pdef", "mstr", "mdef", "speed"};
        Image key;
        int reset = 0;
        for (int i = 0; i < labels.length; i++) {

            if (i > 0 && i % 3 == 0) {
                offX += 56;
                reset = i;
            }

            key = new Image(skin.getDrawable("stats-symbol-" + labels[i]));
            key.setSize(16, 16);
            key.setPosition(offX, offY - gap * (i + 1 - reset), Align.topLeft);
            addActor(key);

            value = new Label("-", skin, "default");
            value.setPosition(offX + 17, offY - gap * (i + 1 - reset), Align.topLeft);
            addActor(value);
            valueLabels.put(labels[i], value);
        }
    }

    @Override
    public void initialize(Item item) {
        super.initialize(item);

        Equipment eq = (Equipment) item;
        String sign;

        int value = eq.getAddsHP();
        if (value != 0) {
            sign = value >= 0 ? "+" : "-";
            valueLabels.get("hp").setText(sign + Integer.toString(value) + "%");
        }

        value = eq.getAddsMP();
        if (value != 0) {
            sign = value >= 0 ? "+" : "-";
            valueLabels.get("mp").setText(sign + Integer.toString(value) + "%");
        }

        value = eq.getAddsPStr();
        if (value != 0) {
            sign = value >= 0 ? "+" : "-";
            valueLabels.get("pstr").setText(sign + Integer.toString(value));
        }

        value = eq.getAddsPDef();
        if (value != 0) {
            sign = value >= 0 ? "+" : "-";
            valueLabels.get("pdef").setText(sign + Integer.toString(value));
        }

        value = eq.getAddsMStr();
        if (value != 0) {
            sign = value >= 0 ? "+" : "-";
            valueLabels.get("mstr").setText(sign + Integer.toString(value));
        }

        value = eq.getAddsMDef();
        if (value != 0) {
            sign = value >= 0 ? "+" : "-";
            valueLabels.get("mdef").setText(sign + Integer.toString(value));
        }

        value = eq.getAddsSpeed();
        if (value != 0) {
            sign = value >= 0 ? "+" : "-";
            valueLabels.get("speed").setText(sign + Integer.toString(value));
        }
    }
}
