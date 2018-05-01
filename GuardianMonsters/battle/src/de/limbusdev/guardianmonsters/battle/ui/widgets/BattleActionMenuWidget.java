package de.limbusdev.guardianmonsters.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class BattleActionMenuWidget extends BattleWidget
{

    // Buttons
    public ImageButton backButton;
    public ImageButton monsterButton;
    public ImageButton bagButton;
    public ImageButton extraButton;

    private Callback backCB, bagCB, monsterCB, extraCB;

    public BattleActionMenuWidget(Skin skin, Callback backCB)
    {
        this(skin, backCB, () -> {}, () -> {}, () -> {});
    }

    /**
     *
     * @param skin battle action UI skin
     */
    public BattleActionMenuWidget(
        Skin skin,
        Callback backCB,
        Callback bagCB,
        Callback monsterCB,
        Callback extraCB)
    {
        super();

        this.backCB     = backCB;
        this.bagCB      = bagCB;
        this.monsterCB  = monsterCB;
        this.extraCB    = extraCB;

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

    private void initCallbackHandler()
    {
        backButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    backCB.onClick();
                }
            }
        );

        bagButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    bagCB.onClick();
                }
            }
        );

        monsterButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    monsterCB.onClick();
                }
            }
        );

        extraButton.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y)
                {
                    extraCB.onClick();
                }
            }
        );
    }

    public void setCallbacks(Callback backCB, Callback bagCB, Callback monsterCB, Callback extraCB)
    {
        this.backCB     = backCB;
        this.bagCB      = bagCB;
        this.monsterCB  = monsterCB;
        this.extraCB    = extraCB;
    }

    public void disableAllButBackButton()
    {
        enable();
        disable(bagButton);
        disable(monsterButton);
        disable(extraButton);
    }

    public void disableAllChildButtons()
    {
        enable();
        disable(bagButton);
        disable(monsterButton);
        disable(extraButton);
        disable(backButton);
    }

    // Inner Interface
    public interface Callback
    {
        void onClick();
    }
}
