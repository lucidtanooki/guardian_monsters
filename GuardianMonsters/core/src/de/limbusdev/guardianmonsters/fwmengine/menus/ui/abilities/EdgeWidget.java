package de.limbusdev.guardianmonsters.fwmengine.menus.ui.abilities;

import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.model.abilities.Edge;
import de.limbusdev.guardianmonsters.model.abilities.Node;

/**
 * EdgeWidget
 *
 * @author Georg Eckert 2017
 */

public class EdgeWidget extends Group implements Listener<Node> {
    private ArrayMap<Edge.Orientation,String> edgeImgsDisabled;
    private Edge edge;
    private Array<Image> images;
    public Node pivot;
    private Skin skin;

    public EdgeWidget(Skin skin, Edge edge) {
        this.skin = skin;

        // Set Edge Image Names
        edgeImgsDisabled = new ArrayMap<>();
        edgeImgsDisabled.put(Edge.Orientation.HORIZONTAL, "graph-horizontal");
        edgeImgsDisabled.put(Edge.Orientation.VERTICAL, "graph-vertical");
        edgeImgsDisabled.put(Edge.Orientation.UPRIGHT, "graph-upright");
        edgeImgsDisabled.put(Edge.Orientation.UPLEFT, "graph-upleft");
        this.edge = edge;
        images = new Array<>();

        switch(edge.orientation) {
            case VERTICAL: assembleVerticalEdge(edge); break;
            case UPLEFT: assembleUpLeftEdge(edge); break;
            case UPRIGHT: assembleUpRightEdge(edge); break;
            default: assembleHorizontalEdge(edge); break;
        }

        changeStatus(edge);
    }

    private void assembleHorizontalEdge(Edge edge) {
        pivot = edge.from.x < edge.to.x ? edge.from : edge.to;
        for(int x = 0; x < edge.getXLength(); x++) {
            Image img = new Image(skin.getDrawable(edgeImgsDisabled.get(Edge.Orientation.HORIZONTAL)));
            img.setPosition(x*32,-3, Align.bottomLeft);
            addActor(img);
            images.add(img);
        }
    }

    private void assembleVerticalEdge(Edge edge) {
        pivot = edge.from.y < edge.to.y ? edge.from : edge.to;
        for(int y = 0; y < edge.getYLength(); y++) {
            Image img = new Image(skin.getDrawable(edgeImgsDisabled.get(Edge.Orientation.VERTICAL)));
            img.setPosition(-3,y*32, Align.bottomLeft);
            addActor(img);
            images.add(img);
        }
    }

    private void assembleUpLeftEdge(Edge edge) {
        pivot = edge.from.y < edge.to.y ? edge.from : edge.to;
        int x=0; int y=0;
        do {
            Image img = new Image(skin.getDrawable(edgeImgsDisabled.get(Edge.Orientation.UPLEFT)));
            img.setPosition(x*32+4,y*32-4,Align.bottomRight);
            addActor(img);
            images.add(img);
            x--;y++;
        } while (y < edge.getYLength());
    }

    private void assembleUpRightEdge(Edge edge) {
        pivot = edge.from.y < edge.to.y ? edge.from : edge.to;
        int x=0; int y=0;
        do {
            Image img = new Image(skin.getDrawable(edgeImgsDisabled.get(Edge.Orientation.UPRIGHT)));
            img.setPosition(x*32-4,y*32-4,Align.bottomLeft);
            addActor(img);
            images.add(img);
            x++;y++;
        } while (y < edge.getYLength());
    }

    public void changeStatus(Edge edge) {
        String ending;
        if(edge.from.isActive() || edge.to.isActive()) {
            ending = "-active";
        } else {
            ending = "";
        }

        for(Image i : images) {
            i.setDrawable(skin.getDrawable(edgeImgsDisabled.get(edge.orientation) + ending));
        }
    }


    @Override
    public void receive(Signal<Node> signal, Node object) {
        if(object.isActive()) {
            changeStatus(edge);
        }
    }
}