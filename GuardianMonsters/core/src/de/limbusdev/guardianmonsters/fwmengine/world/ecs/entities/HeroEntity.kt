package de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities

import com.badlogic.ashley.core.Entity

/**
 * There is nothing special about this class. Actually it's just the same like [Entity],
 * but with entity `instanceof` HeroEntity it is pretty simple to find out whether an entity is
 * the one of your hero.
 *
 *
 * @author Georg Eckert 2015-11-23
 */
class HeroEntity : Entity()
