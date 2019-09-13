package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusBehaviour

import de.limbusdev.guardianmonsters.model.gamestate.GameState


/**
 * SaveGameComponent
 *
 * @author Georg Eckert 2015-12-03
 */
class SaveGameComponent(var gameState: GameState, override val defaultJson: String = "") : LimbusBehaviour(), Component