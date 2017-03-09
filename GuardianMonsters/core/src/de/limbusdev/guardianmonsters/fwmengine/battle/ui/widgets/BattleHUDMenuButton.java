package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.enums.Element;

/**
 * BattleHUDMenuButton
 *
 * @author Georg Eckert 2017
 */

public class BattleHUDMenuButton extends ImageButton {

    public static final int X = 0, Y = 1;
    public static final int DEFEND=0, TEAM=1, BACK=2, BAG=3, SWORD=4, ESCAPE =5, EMPTY=6;
    public static final int ALIGN=Align.bottomLeft;

    public static final int[][] SLOTS = {
        {4,1}, {4,33}, {319,1}, {319,33}, {245,1}, {101,1}, {173,17}
    };

    public BattleHUDMenuButton(Skin skin, int position) {
        super(skin, construct(position));

        setPosition(SLOTS[position][X], SLOTS[position][Y], ALIGN);
    }

    private static String construct(int position) {
        String style;

        switch(position) {
            case DEFEND:
                style = "b-attack-extra";
                break;
            case TEAM:
                style = "b-attack-monsters";
                break;
            case BACK:
                style = "b-attack-back";
                break;
            case BAG:
                style = "b-attack-bag";
                break;
            case SWORD:
                style = "battle-fight";
                break;
            case ESCAPE:
                style = "battle-flee";
                break;
            default:    // EMPTY
                style = "battle-empty";
                break;
        }

        return style;
    }
}
