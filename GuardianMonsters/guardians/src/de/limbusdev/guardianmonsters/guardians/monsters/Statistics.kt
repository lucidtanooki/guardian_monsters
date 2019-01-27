package de.limbusdev.guardianmonsters.guardians.monsters

/**
 * Statistics
 *
 * The Status Values (Stats) are:
 *
 * HP   ..  Health Points
 * MP   ..  Magic Points
 * PStr ..  Physical Strength
 * PDef ..  Physical Defense
 * MStr ..  Magical Strength
 * MDef ..  Magical Defense
 * Speed
 *
 * @author Georg Eckert 2017
 */
open class Statistics(
        var HP: Int = 0,
        var MP: Int = 0,
        var PStr: Int = 0,
        var PDef: Int = 0,
        var MStr: Int = 0,
        var MDef: Int = 0,
        var Speed: Int = 0
)
{
    override fun toString(): String
    {
        return ("HP: $HP\tMP: $MP\tPStr: $PStr\tPDef: $PDef\t" +
                "MStr: $MStr\tMDef: $MDef\tSpeed: $Speed")
    }

    fun clone(): Statistics
    {
        return Statistics(HP, MP, PStr, PDef, MStr, MDef, Speed)
    }
}
