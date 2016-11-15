package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


/**
 * Renders entities colider box
 * Created by georg on 23.11.15.
 */
public class DebuggingSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    /* ........................................................................... CONSTRUCTOR .. */
    public DebuggingSystem() {}
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent.class).get());
    }

    public void update(float deltaTime) {
        // TODO
    }

    /**
     * Render all components with a position component
     * @param shpr
     */
    public void render(ShapeRenderer shpr) {
        shpr.begin(ShapeRenderer.ShapeType.Line);
        shpr.setColor(Color.WHITE);

        for(Entity e : entities) {
            de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent p = de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components.position.get(e);
            shpr.rect(p.x, p.y, p.width, p.height);
        }

        shpr.end();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
