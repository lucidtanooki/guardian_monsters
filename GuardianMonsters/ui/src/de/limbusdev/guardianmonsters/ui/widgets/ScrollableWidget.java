package de.limbusdev.guardianmonsters.ui.widgets;


import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;


/**
 * ScrollableWidget takes a child {@link Actor} and puts it in a bigger container. The Container is
 * then placed in the middle of the {@link ScrollPane} and both ScrollBars are activated.
 * @author Georg Eckert 2017
 */
public class ScrollableWidget extends ScrollPane {

    /**
     *
     * @param scrollWidth   width of the scrollpane
     * @param scrollHeight  height of the scrollpane
     * @param childWidth    width of the scrollable child area (should be >= actual child width)
     * @param childHeight   height of the scrollable child area (should be >= actual child height)
     * @param child         the child which will be scrollable afterwards
     * @param skin          skin for the scrollpane
     */
    public ScrollableWidget(int scrollWidth, int scrollHeight, int childWidth, int childHeight, Actor child, Skin skin) {
        super(construct(childWidth, childHeight, child), skin);
        setSize(scrollWidth, scrollHeight);
        setScrollBarPositions(true, true);
        layout();
        setScrollPercentX(.5f);
        setScrollPercentY(.5f);
    }

    /**
     *
     * @param width     width of the scrollable container
     * @param height    height of the scrollable container
     * @param child     child which will be placed in the middle of the container
     * @return
     */
    private static Actor construct(int width, int height, Actor child) {
        Group container = new Group();
        container.setSize(width,height);
        child.setPosition(width/2-child.getWidth()/2,height/2-child.getHeight()/2, Align.bottomLeft);
        container.addActor(child);

        return container;
    }
}
