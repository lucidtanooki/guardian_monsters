package de.limbusdev.guardianmonsters.fwmengine.world.ecs

import com.badlogic.gdx.utils.Array

object World
{
    private val gdxGameObjects = Array<GdxGameObject>()

    fun update(deltaTime: Float)
    {
        for(gameObject in gdxGameObjects)
        {
            gameObject.update(deltaTime)
        }
    }
}