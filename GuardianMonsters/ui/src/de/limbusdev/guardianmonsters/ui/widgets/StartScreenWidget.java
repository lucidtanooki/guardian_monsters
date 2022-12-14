package de.limbusdev.guardianmonsters.ui.widgets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;

import de.limbusdev.guardianmonsters.assets.paths.AssetPath;
import de.limbusdev.guardianmonsters.scene2d.AnimatedImage;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.Constant;

/**
 * StartScreenWidget
 *
 * @author Georg Eckert 2017
 */

public class StartScreenWidget extends WidgetGroup {

    private Group group;
    public Button startButton;

    public StartScreenWidget(Skin skin) {
        super();

        this.group = new Group();
        group.setSize(Constant.WIDTH, Constant.HEIGHT);
        group.setPosition(0,0,Align.bottomLeft);
        addActor(group);

        Animation bgAnim = new Animation(.1f, Services.Media()
            .getTextureAtlas(AssetPath.Spritesheet.ANIMATIONS_BIG).findRegions("mainMenuAnimation"));
        bgAnim.setPlayMode(Animation.PlayMode.LOOP);
        AnimatedImage bgAnimation = new AnimatedImage(bgAnim);
        bgAnimation.setColor(1,1,1,.3f);
        bgAnimation.setPosition(35,-30, Align.bottomLeft);
        group.addActor(bgAnimation);

        Image logo = new Image(Services.Media().getTexture(AssetPath.Textures.MAIN_MENU_BG1));
        logo.setPosition(Constant.WIDTH / 2, Constant.HEIGHT / 2, Align.center);
        group.addActor(logo);

        Label creatorLabel = new Label("by Georg Eckert", Services.UI().getDefaultSkin(),"trans-white");
        creatorLabel.setPosition(Constant.WIDTH/2,76,Align.bottomLeft);
        creatorLabel.setAlignment(Align.center,Align.center);
        group.addActor(creatorLabel);

        I18NBundle i18n = Services.I18N().General();

        startButton = new TextButton(i18n.get("main_menu_touch_start"), skin, "button-96x32");
        startButton.setSize(96,32);
        startButton.setPosition(Constant.WIDTH/2 - 96/2, 16f, Align.bottomLeft);

        group.addActor(startButton);
    }
}
