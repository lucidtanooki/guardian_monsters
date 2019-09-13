package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.LimbusGameObject

object GameObjectZComparator : Comparator<LimbusGameObject>
{
    val SMALLER = -1
    val BIGGER = 1
    val EQUAL = 0

    override fun compare(object1: LimbusGameObject, object2: LimbusGameObject): Int
    {
        val transform1 = object1.get<TransformComponent>()
        val transform2 = object2.get<TransformComponent>()

        if(transform1 == null || transform2 == null) { return EQUAL }

        return when
        {
            transform1.y > transform2.y -> SMALLER
            transform1.y < transform2.y -> BIGGER
            else -> EQUAL
        }
    }
}
