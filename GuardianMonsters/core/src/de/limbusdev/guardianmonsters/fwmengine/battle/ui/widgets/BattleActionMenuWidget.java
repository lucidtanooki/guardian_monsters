package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;
import de.limbusdev.guardianmonsters.utils.GS;

public class BattleActionMenuWidget extends BattleWidget {

    // Buttons
    public ImageButton backButton;
    public ImageButton monsterButton;
    public ImageButton bagButton;
    public ImageButton extraButton;

    private CallbackHandler callbackHandler;

    /**
     *
     * @param skin battle action UI skin
     */
    public BattleActionMenuWidget(final AHUD hud, Skin skin, CallbackHandler callbackHandler) {
        super(hud);

        this.callbackHandler = callbackHandler;

        // Monster Button
        monsterButton = new ImageButton(skin, "b-attack-monsters");
        monsterButton.setSize(105*GS.zoom,32*GS.zoom);
        monsterButton.setPosition(6*GS.zoom, 64*GS.zoom+1*GS.zoom, Align.topLeft);

        // Extra Button
        extraButton = new ImageButton(skin, "b-attack-extra");
        extraButton.setSize(105*GS.zoom,32*GS.zoom);
        extraButton.setPosition(6*GS.zoom, 1*GS.zoom, Align.bottomLeft);

        // Back Button
        backButton = new ImageButton(skin, "b-attack-back");
        backButton.setSize(105*GS.zoom,32*GS.zoom);
        backButton.setPosition(GS.RES_X-6*GS.zoom, 1*GS.zoom, Align.bottomRight);

        // Bag Button
        bagButton = new ImageButton(skin, "b-attack-bag");
        bagButton.setSize(105*GS.zoom,32*GS.zoom);
        bagButton.setPosition(GS.RES_X-6*GS.zoom, 64*GS.zoom+1*GS.zoom, Align.topRight);

        // Add to parent
        addActor(backButton);
        addActor(monsterButton);
        addActor(bagButton);
        addActor(extraButton);

        initCallbackHandler();
    }


    @Override
    public boolean fadeOutAndRemove() {
        return super.fadeOutAndRemove();
    }

    @Override
    public void addToStageAndFadeIn(Stage newParent) {
        super.addToStageAndFadeIn(newParent);
    }

    private void initCallbackHandler() {
        backButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbackHandler.onBackButton();
                }
            }
        );

        bagButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbackHandler.onBagButton();
                }
            }
        );

        monsterButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbackHandler.onMonsterButton();
                }
            }
        );

        extraButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbackHandler.onExtraButton();
                }
            }
        );
    }

    public void setCallbackHandler(CallbackHandler callbackHandler) {
        this.callbackHandler = callbackHandler;
    }

    public void disableAllButBackButton() {
        enable();
        disable(bagButton);
        disable(monsterButton);
        disable(extraButton);
    }

    // INNER INTERFACE
    public interface CallbackHandler {
        public void onMonsterButton();
        public void onBagButton();
        public void onBackButton();
        public void onExtraButton();
    }
}
