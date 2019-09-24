package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject

object GameObjectZComparator : Comparator<LimbusGameObject>
{
    val SMALLER = -1
    val BIGGER = 1
    val EQUAL = 0

    override fun compare(object1: LimbusGameObject, object2: LimbusGameObject): Int
    {
        return when
        {
            object1.transform.y > object2.transform.y -> SMALLER
            object1.transform.y < object2.transform.y -> BIGGER
            else -> EQUAL
        }
    }
}
