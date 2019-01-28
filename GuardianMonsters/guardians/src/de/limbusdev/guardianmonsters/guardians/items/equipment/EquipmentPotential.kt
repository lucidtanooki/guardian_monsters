package de.limbusdev.guardianmonsters.guardians.items.equipment

/**
 * EquipmentPotential
 *
 *
 * Holds positive and negative values to show how much a given [Equipment] would improve
 * the various monster status values
 *
 * @author Georg Eckert 2017
 */

class EquipmentPotential
(
        var hp: Int,
        var mp: Int,
        var speed: Int,
        var exp: Int,
        var pstr: Int,
        var pdef: Int,
        var mstr: Int,
        var mdef: Int
)
