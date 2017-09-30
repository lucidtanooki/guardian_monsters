package de.limbusdev.guardianmonsters.guardians.abilities;

/**
 * GraphTemplate
 *
 * @author Georg Eckert 2017
 */

public class GraphTemplate {
    public static final int[][] coords = {
        {0,0,0}, {1,0,1}, {2,1,0}, {3,0,-1}, {4,-1,0}, {5,0,2}, {6,2,1}, {7,0,-2}, {8,-2,-1}, {9,-1,2},
        {10,2,2}, {11,1,-2}, {12,-2,-2}, {13,1,2}, {14,3,1}, {15,-1,-2}, {16,-3,-1}, {17,0,3}, {18,4,4}, {19,0,-3},
        {20,-4,-4}, {21,-1,4}, {22,4,2}, {23,1,-4}, {24,-4,-2}, {25,1,4}, {26,3,0}, {27,-1,-4}, {28,-3,0}, {29,2,3},
        {30,5,4}, {31,-2,-3}, {32,-5,-4}, {33,5,3}, {34,-5,-3}, {35,2,0}, {36,-2,0}, {37,6,3}, {38,-6,-3}, {39,5,2},
        {40,-5,-2}, {41,1,-1}, {42,-1,1}, {43,6,2}, {44,-6,-2}, {45,4,3}, {46,-4,-3}, {47,3,-1}, {48,-3,1}, {49,5,1},
        {50,-5,-1}, {51,2,-2}, {52,-2,2}, {53,5,0}, {54,-5,0}, {55,4,-4}, {56,-4,4}, {57,4,1}, {58,-4,-1}, {59,5,-3},
        {60,-5,3}, {61,6,1}, {62,-6,-1}, {63,2,-4}, {64,-2,4}, {65,7,0}, {66,-7,0}, {67,5,-1}, {68,-5,1}, {69,4,-1},
        {70,-4,1}, {71,2,-3}, {72,-2,3}, {73,7,1}, {74,-7,-1}, {75,6,-2}, {76,-6,2}, {77,4,-2}, {78,-4,2}, {79,6,-3},
        {80,-6,3}, {81,7,-1}, {82,-7,1}, {83,7,-3}, {84,-7,3}, {85,3,-2}, {86,-3,2}, {87,6,-4}, {88,-6,4}, {89,8,0},
        {90,-8,0}, {91,7,-4}, {92,-7,4}, {93,4,-3}, {94,-4,3}, {95,8,2}, {96,-8,-2}, {97,8,-2}, {98,-8,2}, {99,8,4},
        {100,-8,-4}
    };

    public static final int[][] conns =  {
        {0,1},{1,5},{5,9},{5,17},{5,13},{5,17},{17,21},{17,25},{25,29},
        {0,3}, {3,7}, {7,11}, {7,15}, {7,19}, {19,23}, {19,27}, {27,31},
        {0,2}, {2,6}, {6,10}, {10,18}, {18,30}, {30,37}, {37,43}, {43,49}, {49,57}, {49,61}, {49,53},
        {53,69}, {69,77}, {77,85}, {77,93},
        {53,65}, {65,89}, {65,81}, {81,97}, {65,73}, {73,95}, {95,99},
        {6,14}, {14,22}, {22,39}, {22,33}, {33,45},
        {14,26}, {26,35}, {35,41}, {41,47}, {47,51}, {51,55},
        {55,63}, {63,71},
        {55,59}, {59,67}, {67,75}, {75,79}, {79,83}, {83,87}, {87,91},
        {0,4}, {4,8}, {8,16}, {16,24}, {24,40}, {24,34}, {34,46},
        {16,28}, {28,36}, {36,42}, {42,48}, {48,52}, {52,56}, {56,64}, {64,72},
        {56,60}, {60,68}, {68,76}, {76,80}, {80,84}, {84,88}, {88,92},
        {8,12}, {12,20}, {20,32}, {32,38}, {38,44}, {44,50}, {50,62}, {50,58}, {50,54},
        {54,70}, {70,78}, {78,86}, {78,94},
        {54,66}, {66,82}, {82,98}, {66,90}, {66,74}, {74,96}, {96,100}
    };
}