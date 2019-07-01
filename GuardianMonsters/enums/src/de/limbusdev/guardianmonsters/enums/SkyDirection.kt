package de.limbusdev.guardianmonsters.enums

enum class SkyDirection
{
    N, NE, NW, NSTOP,
    W, WSTOP,
    E, ESTOP,
    S, SW, SE, SSTOP
}

/** North, East, South, West */
enum class Compass4 { N, E, S, W }

/** North, North-East, East, South-East, South, South-West, West, North-West */
enum class Compass8 { N, NE, E, SE, S, SW, W, NW }

enum class MoveDirection { N, E, S, W, NONE }