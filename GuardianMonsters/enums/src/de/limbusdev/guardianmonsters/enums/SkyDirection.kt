package de.limbusdev.guardianmonsters.enums

enum class SkyDirection
{
    N, NE, NW, NSTOP,
    W, WSTOP,
    E, ESTOP,
    S, SW, SE, SSTOP;

    fun stop() : SkyDirection
    {
        return when(this)
        {
            N, NE, NW, NSTOP -> { NSTOP }
            S, SE, SW, SSTOP -> { SSTOP }
            E, ESTOP -> { ESTOP }
            W, WSTOP -> { WSTOP }
        }
    }

    fun nostop() : SkyDirection
    {
        return when(this)
        {
            N,NSTOP -> N
            S,SSTOP -> S
            E,ESTOP -> E
            W,WSTOP -> W
            else -> S
        }
    }

    fun isStop() = this == stop()

    fun invert() : SkyDirection
    {
        return when(this)
        {
            N, NSTOP -> S
            E, ESTOP -> W
            S, SSTOP -> N
            W, WSTOP -> E
            NE -> SW
            SE -> NW
            SW -> NE
            NW -> SE
        }
    }

    val x : Int get() = when(this)
    {
        N, NSTOP -> 0
        NE, E, ESTOP, SE -> 1
        S, SSTOP -> 0
        SW, W, WSTOP, NW -> -1
    }

    val y : Int get() = when(this)
    {
        NW, N, NSTOP, NE -> 1
        E, ESTOP -> 0
        SE, S, SSTOP, SW -> -1
        W, WSTOP -> 0
    }
}

/** North, East, South, West */
enum class Compass4 { N, E, S, W }

/** North, North-East, East, South-East, South, South-West, West, North-West */
enum class Compass8 { N, NE, E, SE, S, SW, W, NW }

enum class MoveDirection { N, E, S, W, NONE }