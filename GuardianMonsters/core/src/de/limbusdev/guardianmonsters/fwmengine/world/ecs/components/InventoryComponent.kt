package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

import de.limbusdev.guardianmonsters.guardians.items.Inventory

/**
 * InventoryComponent
 *
 * @author Georg Eckert 2017-02-17
 */

class InventoryComponent(var inventory: Inventory) : LimbusBehaviour()
