package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class BattleActionMenuWidget extends BattleWidget {

    // Buttons
    public ImageButton backButton;
    public ImageButton monsterButton;
    public ImageButton bagButton;
    public ImageButton extraButton;

    private ClickListener clickListener;

    /**
     *
     * @param skin battle action UI skin
     */
    public BattleActionMenuWidget(Skin skin, ClickListener clickListener) {
        super();

        this.clickListener = clickListener;

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
            new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    clickListener.onBackButton();
                }
            }
        );

        bagButton.addListener(
            new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    clickListener.onBagButton();
                }
            }
        );

        monsterButton.addListener(
            new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    clickListener.onMonsterButton();
                }
            }
        );

        extraButton.addListener(
            new com.badlogic.gdx.scenes.scene2d.utils.ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    clickListener.onExtraButton();
                }
            }
        );
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
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
    public static abstract class ClickListener {
        public void onMonsterButton(){}
        public void onBagButton(){}
        public void onBackButton(){}
        public void onExtraButton(){}
    }
}
