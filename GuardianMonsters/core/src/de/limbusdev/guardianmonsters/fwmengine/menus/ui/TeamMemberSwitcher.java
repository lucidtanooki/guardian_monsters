package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.data.BundleAssets;
import de.limbusdev.guardianmonsters.data.TextureAssets;
import de.limbusdev.guardianmonsters.fwmengine.managers.Services;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.model.MonsterInfo;

/**
 * Created by Georg Eckert on 01.03.17.
 */

public class TeamMemberSwitcher extends Group {

    private CallbackHandler callbacks;
    private int currentlyChosen;
    private Image monsterImg;
    private Label name;

    public TeamMemberSwitcher(Skin skin, final ArrayMap<Integer, Monster> team, final CallbackHandler callbackHandler) {
        super();

        this.callbacks = callbackHandler;
        this.currentlyChosen = 0;

        setSize(96,64);

        Label bg = new Label("", skin, "list-item");
        bg.setSize(64,64);
        bg.setPosition(16,0, Align.bottomLeft);
        addActor(bg);

        name = new Label("", skin, "default");
        name.setSize(60,20);
        name.setPosition(18,6,Align.bottomLeft);
        addActor(name);

        ImageButton previous = new ImageButton(skin, "button-previous");
        previous.setPosition(0,16,Align.bottomLeft);
        addActor(previous);

        ImageButton next = new ImageButton(skin, "button-next");
        next.setPosition(80,16,Align.bottomLeft);
        addActor(next);


        Image monsterBg = new Image(Services.getUI().getBattleSkin().getDrawable("monster-preview"));
        monsterBg.setPosition(32,58,Align.topLeft);
        addActor(monsterBg);
        monsterImg = new Image(new TextureRegionDrawable(
            Services.getMedia().getTextureAtlas(TextureAssets.battleMonsterPreviews)
            .findRegion(Integer.toString(team.get(0).ID))
        ));
        monsterImg.setPosition(36,53,Align.topLeft);
        addActor(monsterImg);

        previous.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentlyChosen--;
                if(currentlyChosen < 0) {
                    currentlyChosen = team.size-1;
                }
                init(team.get(currentlyChosen));
                callbacks.onChanged(currentlyChosen);
            }
        });

        next.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentlyChosen++;
                if(currentlyChosen > team.size-1) {
                    currentlyChosen = 0;
                }
                init(team.get(currentlyChosen));
                callbacks.onChanged(currentlyChosen);
            }
        });

        init(team.get(0));
    }

    public void init(Monster m) {
        name.setText(Services.getL18N().l18n(BundleAssets.MONSTERS)
            .get(MonsterInfo.getInstance().getNameById(m.ID)));

        Image monsterBg = new Image(Services.getUI().getBattleSkin().getDrawable("monster-preview"));
        monsterBg.setPosition(32,58,Align.topLeft);
        addActor(monsterBg);
        monsterImg.setDrawable(new TextureRegionDrawable(
            Services.getMedia().getTextureAtlas(TextureAssets.battleMonsterPreviews)
                .findRegion(Integer.toString(m.ID))
        ));
        monsterImg.setPosition(36,53,Align.topLeft);
        addActor(monsterImg);
    }

    public interface CallbackHandler {
        void onChanged(int position);
    }

    public int getCurrentlyChosen() {
        return currentlyChosen;
    }
}
