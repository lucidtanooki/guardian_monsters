package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

/**
 * Created by Georg Eckert on 21.02.17.
 */

public class AbilityGraph {

    private final static int X=0, Y=1;

    int coords[][] = {
        {0,0},
        {1,0}, {1,1}, {2,1}, {3,1}, {3,0}, {4,1}, {6,-1}, {5,-2},
        {-1,0}, {-1,-1}, {-2,-1}, {-3,-1}, {-3,0}, {-4,-1}, {-6,1}, {-5,2},
        {0,1}, {0,2},
        {0,-1}, {0,-2},
        {1,2}, {2,2}, {3,2},
        {-1,-2}, {-2,-2}, {-3,-2},
    };

    int conns[][] =  {
        {0,1}, {1,2}, {2,3}, {3,4}, {4,5}, {5,6}, {6,7}, {7,8},
        {0,9}, {9,10}, {10,11}, {11,12}, {12,13}, {13,14}, {14,15}, {15,16},
        {0,17}, {17,18}, {18,21}, {21,22}, {22,23},
        {0,19}, {19,20}, {20,24}, {24,25}, {25,26}
    };

    public class Vertex {
        public int x;
        public int y;

        public Vertex(int x, int y) {
            this.y = y;
            this.x = x;
        }
    }

    public class Edge {
        public Vertex from;
        public Vertex to;

        public Edge(Vertex from, Vertex to) {
            this.from = from;
            this.to = to;
        }
    }

    private ArrayMap<Integer, Vertex> vertices;
    private Array<Edge> edges;

    public AbilityGraph() {
        vertices = new ArrayMap<>();
        edges = new Array<>();

        for(int i = 0; i < coords.length; i++) {
            int v[] = coords[i];
            vertices.put(i, new Vertex(v[X], v[Y]));
        }

        for(int i = 0; i < conns.length; i++) {
            int e[] = conns[i];
            edges.add(new Edge(vertices.get(e[X]), vertices.get(e[Y])));
        }

    }

    public ArrayMap<Integer, Vertex> getVertices() {
        return vertices;
    }

    public Array<Edge> getEdges() {
        return edges;
    }
}
