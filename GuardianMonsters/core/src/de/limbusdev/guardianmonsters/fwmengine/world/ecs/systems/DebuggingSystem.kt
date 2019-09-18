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
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private var entities: ImmutableArray<Entity>? = null

    // --------------------------------------------------------------------------------------------- METHODS
    override fun addedToEngine(engine: Engine)
    {
        entities = engine.getEntitiesFor(Family.all(Transform::class.java).get())
    }

    override fun update(deltaTime: Float)
    {
        // TODO
    }

    /**
     * Render all components with a position component
     * @param shpr
     */
    fun render(shpr: ShapeRenderer)
    {
        shpr.begin(ShapeRenderer.ShapeType.Line)
        shpr.color = Color.WHITE

        for(gameObject in CoreSL.world.getAllWith("ColliderComponent"))
        {
            val collider = gameObject.get<ColliderComponent>()
            if(collider != null)
            {
                val transform = gameObject.transform.asRectangle
                shpr.rect(transform.xf, transform.yf, transform.widthf, transform.heightf)
            }
        }

        shpr.end()
    }
}
