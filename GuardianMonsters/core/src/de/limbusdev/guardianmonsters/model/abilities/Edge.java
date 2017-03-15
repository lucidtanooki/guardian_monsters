package de.limbusdev.guardianmonsters.model.abilities;

/**
 * Edge
 *
 * @author Georg Eckert 2017
 */

public class Edge {

    public enum Orientation {
        HORIZONTAL, VERTICAL, UPLEFT, UPRIGHT,
    }

    public Node from;
    public Node to;
    public Orientation orientation;

    /**
     * For Serialization only!
     */
    public Edge (){}

    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
        if(from.x == to.x || from.y == to.y) {
            orientation = (from.x == to.x) ? Orientation.VERTICAL : Orientation.HORIZONTAL;
        } else {
            if((from.x < to.x && from.y < to.y) || (from.x > to.x && from.y > to.y)) {
                orientation = Orientation.UPRIGHT;
            } else {
                orientation = Orientation.UPLEFT;
            }
        }
    }

    public int getXLength() {
        return Math.abs(from.x - to.x);
    }

    public int getYLength() {
        return Math.abs(from.y - to.y);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Edge)) return false;
        Edge e = (Edge)obj;
        if((e.from == this.from && e.to == this.to) || (e.from == this.to && e.to == this.from)) {
            return true;
        } else {
            return false;
        }
    }
}