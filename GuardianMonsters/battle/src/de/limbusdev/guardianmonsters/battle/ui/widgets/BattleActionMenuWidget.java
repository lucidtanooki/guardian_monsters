package de.limbusdev.guardianmonsters.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class BattleActionMenuWidget extends de.limbusdev.guardianmonsters.battle.ui.widgets.BattleWidget
{

    // Buttons
    public ImageButton backButton;
    public ImageButton monsterButton;
    public ImageButton bagButton;
    public ImageButton extraButton;

    private Callbacks callbacks;

    /**
     *
     * @param skin battle action UI skin
     */
    public BattleActionMenuWidget(Skin skin, Callbacks callbacks) {
        super();

        this.callbacks = callbacks;

        monsterButton   = new de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDMenuButton(skin, de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDMenuButton.TEAM    );
        extraButton     = new de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDMenuButton(skin, de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDMenuButton.DEFEND  );
        backButton      = new de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDMenuButton(skin, de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDMenuButton.BACK    );
        bagButton       = new de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDMenuButton(skin, de.limbusdev.guardianmonsters.battle.ui.widgets.BattleHUDMenuButton.BAG     );

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
            new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbacks.onBackButton();
                }
            }
        );

        bagButton.addListener(
            new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbacks.onBagButton();
                }
            }
        );

        monsterButton.addListener(
            new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbacks.onMonsterButton();
                }
            }
        );

        extraButton.addListener(
            new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    callbacks.onExtraButton();
                }
            }
        );
    }

    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
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
    public static abstract class Callbacks {
        public void onMonsterButton(){}
        public void onBagButton(){}
        public void onBackButton(){}
        public void onExtraButton(){}
    }
}
