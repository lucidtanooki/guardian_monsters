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
 * Created by Georg Eckert 2016
 */

class BaseStat(
        val ID: Int = 0,
        val baseHP: Int = 300,
        val baseMP: Int = 100,
        val basePStr: Int = 10,
        val basePDef: Int = 10,
        val baseMStr: Int = 10,
        val baseMDef: Int = 10,
        val baseSpeed: Int = 10
) {}
