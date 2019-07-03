package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import com.badlogic.ashley.core.Component

/**
 * ConversationComponent
 *
 * @author Georg Eckert 2015-12-02
 */
class ConversationComponent(var text: String, var name: String = "") : Component
