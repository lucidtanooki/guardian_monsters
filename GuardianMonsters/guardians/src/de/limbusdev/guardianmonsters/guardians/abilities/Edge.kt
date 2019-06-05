package de.limbusdev.guardianmonsters.guardians.abilities

/**
 * An **Edge** connects two @see Node objects.
 *
 * @author Georg Eckert 2017
 */

class Edge
{
    // ............................................................................... Inner Classes
    enum class Orientation { HORIZONTAL, VERTICAL, UPLEFT, UPRIGHT }

    // .................................................................................. Properties
    var from: Node
    var to:   Node
    var orientation: Orientation

    val xLength: Int get() = Math.abs(from.x - to.x)

    val yLength: Int get() = Math.abs(from.y - to.y)

    // ................................................................................ Constructors
    /**
     * For Serialization only!
     */
    constructor()
    {
        from = Node(0,0,0);
        to = Node(0,0,0)
        orientation = Orientation.HORIZONTAL
    }

    constructor(from: Node, to: Node)
    {
        this.from = from
        this.to = to

        val sameCol = from.x == to.x
        val sameRow = from.y == to.y
        val upRight = from.x < to.x && from.y < to.y || from.x > to.x && from.y > to.y

        orientation = when
        {
            sameCol -> Orientation.VERTICAL
            sameRow -> Orientation.HORIZONTAL
            upRight -> Orientation.UPRIGHT
            else    -> Orientation.UPLEFT
        }
    }

    // ...................................................................................... Equals
    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Edge

        return (from == other.from && to == other.to)
                || (from == other.to && to == other.from)
                && orientation == other.orientation
    }

    override fun hashCode() = 31 * (to.hashCode() + from.hashCode()) * 31 + orientation.hashCode()
}