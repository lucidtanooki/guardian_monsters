package de.limbusdev.guardianmonsters.guardians.monsters

/**
 * BaseStat contains all base values. BaseStats are stats of a monster at level 1 without any
 * additional stuff. On creating a new monster, the BaseStat values are copied over.
 *
 * The Basic Status Values (BaseStats) are:
 *
 * HP   ..  Health Points
 * MP   ..  Magic Points
 * PStr ..  Physical Strength
 * PDef ..  Physical Defense
 * MStr ..  Magical Strength
 * MDef ..  Magical Defense
 * Speed
 *
 * Base Stats are common to all Guardians of the same species, and therefore are part of the
 * [SpeciesDescription].
 *
 * @author Georg Eckert 2016
 */

class CommonStatistics(

        HP: Int = 50,
        MP: Int = 50,
        PStr: Int = 50,
        PDef: Int = 50,
        MStr: Int = 50,
        MDef: Int = 50,
        Speed: Int = 50

) : Statistics(HP, MP, PStr, PDef, MStr, MDef, Speed)
{
    fun getBaseHP(): Int { return HP }

    fun getBaseMP(): Int { return MP }

    fun getBasePStr(): Int { return PStr }

    fun getBasePDef(): Int { return PDef }

    fun getBaseMStr(): Int { return MStr }

    fun getBaseMDef(): Int { return MDef }

    fun getBaseSpeed(): Int { return Speed }
}