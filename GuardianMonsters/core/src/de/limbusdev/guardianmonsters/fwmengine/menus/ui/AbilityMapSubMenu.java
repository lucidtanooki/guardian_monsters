package de.limbusdev.guardianmonsters.fwmengine.menus.ui;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.model.AbilityGraph;
import de.limbusdev.guardianmonsters.utils.GS;


/**
 * Created by Georg Eckert on 21.02.17.
 */

public class AbilityMapSubMenu extends AInventorySubMenu {

    private Group connections, circles;

    public AbilityMapSubMenu(Skin skin) {
        super(skin);
        connections = new Group();
        circles = new Group();

        AbilityGraph graph = new AbilityGraph();
        graph.getEdges();

        for(AbilityGraph.Edge e : graph.getEdges()) {
            AbilityGraph.Vertex curr = e.from;
            AbilityGraph.Vertex next = e.to;

            addConnection(skin, curr, next);
        }

        for(AbilityGraph.Vertex v : graph.getVertices().values()) {
            // Add Circle
            Image circle = new Image(skin.getDrawable("ability-circle-inactive"));
            circle.setPosition(GS.WIDTH/2-8+32*v.x,GS.HEIGHT/2-8+32*v.y, Align.bottomLeft);
            circles.addActor(circle);
        }

        addActor(connections);
        addActor(circles);
    }

    private void addConnection(Skin skin, AbilityGraph.Vertex from, AbilityGraph.Vertex to) {
        if(from.x != to.x && from.y == to.y) {
            // Horizontal
            int fx, tx;
            fx = from.x < to.x ? from.x : to.x;
            tx = from.x < to.x ? to.x : from.x;

            for(int x = fx; x < tx; x++) {
                Image bar = new Image(skin.getDrawable("ability-connection-hor"));
                bar.setPosition(GS.WIDTH/2+x*32, GS.HEIGHT/2-16+from.y*32, Align.bottomLeft);
                connections.addActor(bar);
            }
        }

        if(from.y != to.y && from.x == to.x) {
            // Vertical
            int fy, ty;
            fy = from.y < to.y ? from.y : to.y;
            ty = from.y < to.y ? to.y : from.y;

            for(int y = fy; y < ty; y++) {
                Image bar = new Image(skin.getDrawable("ability-connection-vert"));
                bar.setPosition(GS.WIDTH/2+from.x*32-16, GS.HEIGHT/2+y*32, Align.bottomLeft);
                connections.addActor(bar);
            }
        }

        if(from.y != to.y && from.x != to.x &&
            (from.x < to.x && from.y < to.y) ||
            (from.x > to.x && from.y > to.y)) {

            // Diagonal - Up-Right
            int fx, tx, fy, ty;
            fx = from.x < to.x ? from.x : to.x;
            tx = from.x < to.x ? to.x : from.x;
            fy = from.y < to.y ? from.y : to.y;
            ty = from.y < to.y ? to.y : from.y;

            int x=fx;
            for(int y = fy; y < ty; y++) {
                Image bar = new Image(skin.getDrawable("ability-connection-diag-ur-dl"));
                bar.setPosition(GS.WIDTH/2+x*32-4, GS.HEIGHT/2+y*32-4, Align.bottomLeft);
                connections.addActor(bar);
                x++;
            }
        }

        if(from.y != to.y && from.x != to.x &&
            (from.x < to.x && from.y > to.y) ||
            (from.x > to.x && from.y < to.y)) {

            // Diagonal - Up-Right
            int fx, tx, fy, ty;
            fx = from.x < to.x ? from.x : to.x;
            tx = from.x < to.x ? to.x : from.x;
            fy = from.y > to.y ? from.y : to.y;
            ty = from.y > to.y ? to.y : from.y;

            int x = fx;
            for(int y = fy; y > ty; y--) {
                Image bar = new Image(skin.getDrawable("ability-connection-diag-ul-dr"));
                bar.setPosition(GS.WIDTH/2+x*32-4, GS.HEIGHT/2+y*32-32-4, Align.bottomLeft);
                connections.addActor(bar);
                x++;
            }
        }
    }
}
