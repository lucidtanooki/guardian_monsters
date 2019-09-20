package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import de.limbusdev.guardianmonsters.CoreSL
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ColliderComponent

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Transform


/**
 * Renders entities collider box
 *
 * @author Georg Eckert 2015-11-23
 */
class DebuggingSystem : EntitySystem()
{
    /**
     * Render all components with a position component
     * @param shpr
     */
    fun render(shpr: ShapeRenderer)
    {
        shpr.begin(ShapeRenderer.ShapeType.Line)
        shpr.color = Color.WHITE

        val collidableObjects = CoreSL.world.getAllWith("ColliderComponent")
        for(gameObject in collidableObjects)
        {
            val collider = gameObject.get<ColliderComponent>()
            if(collider != null)
            {
                val transform = collider.asRectangle
                shpr.rect(transform.xf, transform.yf, transform.widthf, transform.heightf)
            }
        }

        shpr.end()
    }
}
