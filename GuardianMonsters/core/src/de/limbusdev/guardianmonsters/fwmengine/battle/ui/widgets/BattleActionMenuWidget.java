package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import de.limbusdev.guardianmonsters.fwmengine.battle.ui.AHUD;

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
    public BattleActionMenuWidget(Skin skin, CallbackHandler callbackHandler) {
        super();

        this.callbackHandler = callbackHandler;

        monsterButton   = new BattleHUDMenuButton(skin, BattleHUDMenuButton.TEAM    );
        extraButton     = new BattleHUDMenuButton(skin, BattleHUDMenuButton.DEFEND  );
        backButton      = new BattleHUDMenuButton(skin, BattleHUDMenuButton.BACK    );
        bagButton       = new BattleHUDMenuButton(skin, BattleHUDMenuButton.BAG     );

        // Add to parent
        addActor(backButton     );
        addActor(monsterButton  );
        addActor(bagButton      );
        addActor(extraButton    );

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

    public void disableAllChildButtons() {
        enable();
        disable(bagButton);
        disable(monsterButton);
        disable(extraButton);
        disable(backButton);
    }

    // INNER INTERFACE
    public interface CallbackHandler {
        void onMonsterButton();
        void onBagButton();
        void onBackButton();
        void onExtraButton();
    }
}
