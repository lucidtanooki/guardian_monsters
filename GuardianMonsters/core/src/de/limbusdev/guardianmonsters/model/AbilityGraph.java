package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.enums.SkyDirection;

/**
 * Created by Georg Eckert on 21.02.17.
 */

public class AbilityGraph {

    public final static int HORIZONTAL = 0;
    public final static int VERTICAL = 1;
    public final static int UPLEFT = 2;
    public final static int UPRIGHT = 3;
    private final static int X=0, Y=1;
    private Array<Integer> activatedNodes;

    int coords[][] = {
        {0,0}, {0,1}, {0,2}, {0,3}, {-1,4}, {1,4}, {2,3}, {-1,2}, {1,2}, {0,-1},            /* 0 - 9 */
        {0,-2}, {-1,-2}, {1,-2}, {0,-3}, {1,-4}, {-1,-4}, {-2,-3}, {1,0}, {2,1}, {3,1},     /* 10 - 19 */
        {4,2}, {5,2}, {5,3}, {4,3}, {3,0}, {2,0}, {1,-1}, {3,-1}, {2,-2}, {4,-4},           /* 20 - 29 */
        {2,-4}, {2,-3}, {5,-3}, {5,-1}, {6,-2}, {6,-3}, {7,-3}, {6,-4}, {7,-4}, {2,2},      /* 30 - 39*/
        {4,4}, {5,4}, {6,3}, {6,2}, {5,1},  {4,1}, {6,1}, {5,0}, {4,-1}, {4,-2},
        {3,-2}, {4,-3}, {7,0}, {8,0}, {7,-1}, {8,-2}, {7,1}, {8,2}, {8,4}, {-1,0},
        {-2,-1}, {-3,-1}, {-4,-2}, {-5,-2}, {-5,-3}, {-4,-3}, {-3,0}, {-2,0}, {-1,1}, {-3,1},
        {-2,2}, {-4,4}, {-2,4}, {-2,3}, {-5,3}, {-5,1}, {-6,2}, {-6,3}, {-7,3}, {-6,4},
        {-7,4}, {-2,-2}, {-4,-4}, {-5,-4}, {-6,-3}, {-6,-2}, {-5,-1}, {-4,-1}, {-6,-1}, {-5,0},
        {-4,1}, {-4,2}, {-3,2}, {-4,3}, {-7,0}, {-8,0}, {-7,1}, {-8,2}, {-7,-1}, {-8,-2}, {-8,-4},
    };

    int conns[][] =  {
        {0,1}, {1,2}, {2,3}, {3,4}, {3,5}, {5,6}, {2,7}, {2,8},
        {0,9}, {9,10}, {10,11}, {10,12}, {10,13}, {13,14}, {13,15}, {15,16},
        {0,17}, {17,18}, {18,19}, {19,20}, {20,21}, {20,22}, {22,23},
        {19,24}, {24,25}, {25,26}, {26,27}, {27,28}, {28,29}, {29,30}, {30,31},
        {29,32}, {32,33}, {33,34}, {34,35}, {35,36}, {36,37}, {37,38},
        {18,39}, {39,40}, {40,41}, {41,42}, {42,43}, {43,44}, {44,45}, {44,46}, {44,47},
        {47,48}, {48,49}, {49,50}, {50,51},
        {47,52}, {52,53}, {52,54}, {54,55}, {52,56}, {56,57}, {57,58},
        {0,59}, {59,60}, {60,61}, {61,62}, {62,63}, {62,64}, {64,65},
        {61,66}, {66,67}, {67,68}, {68,69}, {69,70}, {70,71}, {71,72}, {72,73},
        {71,74}, {74,75}, {75,76}, {76,77}, {77,78}, {78,79}, {79,80},
        {60,81}, {81,82}, {82,83}, {83,84}, {84,85}, {85,86}, {86,87}, {87,88}, {86,89},
        {89,90}, {90,91}, {91,92},  {91,93},
        {89,94}, {94,95}, {94,96}, {96,97}, {94,98}, {98,99}, {99,100},
    };

    public class Vertex {
        public int x;
        public int y;
        public int ID;

        public Vertex(int x, int y, int ID) {
            this.y = y;
            this.x = x;
            this.ID = ID;
        }
    }

    public class Edge {
        public Vertex from;
        public Vertex to;
        public int orientation;

        public Edge(Vertex from, Vertex to) {
            this.from = from;
            this.to = to;
            if(from.x == to.x || from.y == to.y) {
                orientation = (from.x == to.x) ? VERTICAL : HORIZONTAL;
            } else {
                if((from.x < to.x && from.y < to.y) || (from.x > to.x && from.y > to.y)) {
                    orientation = UPRIGHT;
                } else {
                    orientation = UPLEFT;
                }
            }
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

    private ArrayMap<Integer, Vertex> vertices;
    private Array<Edge> edges;

    public AbilityGraph() {
        vertices = new ArrayMap<>();
        edges = new Array<>();
        activatedNodes = new Array<>();

        for(int i = 0; i < coords.length; i++) {
            int v[] = coords[i];
            vertices.put(i, new Vertex(v[X], v[Y], i));
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

    public Array<Integer> getActivatedNodes() {
        return activatedNodes;
    }

    public void activateNode(int ID) {
        if(!activatedNodes.contains(ID, false))
            activatedNodes.add(ID);
    }
}
