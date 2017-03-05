package de.limbusdev.guardianmonsters.fwmengine.menus.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;


/**
 * LogoWithCounter
 *
 * @author Georg Eckert 2017
 */

public class LogoWithCounter extends Group {

    public Label counter;

    public LogoWithCounter(Skin skin, String backgroundStyle, String symbolStyle) {
        super();

        setSize(64,27);

        Group group = new Group();
        group.setSize(56,24);

        Image symbol = new Image(skin.getDrawable(symbolStyle));
        symbol.setSize(16,16);
        symbol.setPosition(0,4,Align.bottomLeft);
        group.addActor(symbol);

        counter = new Label("0", skin, "default");
        counter.setPosition(20,5,Align.bottomLeft);
        group.addActor(counter);

        Container container = new Container(group);
        container.setSize(64,27);
        container.setPosition(0,0,Align.bottomLeft);
        container.pad(4).padBottom(6);
        container.setBackground(skin.getDrawable(backgroundStyle));

        addActor(container);
    }
}
