package de.limbusdev.guardianmonsters.fwmengine.menus.ui.team;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.fwmengine.menus.ui.items.GuardianOverviewButton;
import de.limbusdev.guardianmonsters.model.Item;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.utils.Constant;

/**
 * Gives the player a quick overview over all guardians currently in team. All status values are
 * shown and guardians not applicable for the choice to be made, should be disabled
 * <p>
 * Created by Georg Eckert on 21.02.17.
 */

public class QuickOverviewGuardianList extends Group {

    private CallbackHandler handler;
    private Image blackLayer;

    public QuickOverviewGuardianList(Skin skin, ArrayMap<Integer, Monster> team, CallbackHandler cBhandler, Item item) {

        this.handler = cBhandler;

        blackLayer = new Image(skin.getDrawable("black-a80"));
        blackLayer.setSize(Constant.WIDTH, Constant.HEIGHT);
        blackLayer.setPosition(0,0,Align.bottomLeft);
        addActor(blackLayer);

        ImageButton back = new ImageButton(skin, "button-back");
        back.setPosition(Constant.WIDTH-4, 0, Align.bottomRight);
        back.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                remove();
            }
        });
        addActor(back);

        Table monsterTable = new Table();
        monsterTable.align(Align.topLeft);

        ScrollPane scrollPane = new ScrollPane(monsterTable, skin);

        scrollPane.setSize(192, 236);
        scrollPane.setPosition(0, 0);
        scrollPane.setScrollBarPositions(false, true);
        addActor(scrollPane);

        for (int i = 0; i<team.size; i++) {
            final int index = i;
            Monster m = team.get(i);
            GuardianOverviewButton guardianButton = new GuardianOverviewButton(m, skin, "button-sandstone", item);

            monsterTable.add(guardianButton).width(192).height(64);
            monsterTable.row().spaceBottom(1);

            guardianButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    boolean moreItems = handler.onButton(index);
                    if(!moreItems) remove();
                }
            });
        }
    }

    public interface CallbackHandler {
        boolean onButton(int i);
    }

}
