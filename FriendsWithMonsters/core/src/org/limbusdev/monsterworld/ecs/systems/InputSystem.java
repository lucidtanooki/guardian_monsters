package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.enums.SkyDirection;
import org.limbusdev.monsterworld.utils.GlobalSettings;

/**
 * Created by georg on 22.11.15.
 */
public class InputSystem extends EntitySystem implements InputProcessor {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;

    private ComponentMapper<InputComponent> im
            = ComponentMapper.getFor(InputComponent.class);
    private ComponentMapper<PositionComponent> pom
            = ComponentMapper.getFor(PositionComponent.class);

    private Vector2 directionVector;

    private Viewport viewport;
    /* ........................................................................... CONSTRUCTOR .. */
    public InputSystem(Viewport viewport) {
        this.directionVector = new Vector2();
        this.viewport = viewport;
        Gdx.input.setInputProcessor(this);
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        entities = engine.getEntitiesFor(Family.all(
                InputComponent.class,
                PositionComponent.class).get());
    }

    public void update(float deltaTime) {
        for (Entity entity : entities) {
            InputComponent input = im.get(entity);
            PositionComponent position = pom.get(entity);

            makeOneStep(position, input, deltaTime);
        }
    }

    public void makeOneStep(PositionComponent position, InputComponent input, float deltaTime) {
        if(input.startMoving) {
            input.moving = true;
            input.startMoving = false;

            // Define direction
            input.touchPos.x = Gdx.input.getX();
            input.touchPos.y = Gdx.input.getY();
            viewport.unproject(input.touchPos);

            // calculate characters main moving direction for sprite choosing
            if(new Rectangle(position.x, position.y, 1, 1).contains(input.touchPos.x, input
                    .touchPos.y)) return;

            if(Math.abs(input.touchPos.x - position.x+.5)
                    > Math.abs(input.touchPos.y - position.y+.5)) {
                if(input.touchPos.x > position.x+.5) input.skyDir = SkyDirection.E;
                else input.skyDir = SkyDirection.W;
            } else {
                if(input.touchPos.y > position.y+.5) input.skyDir = SkyDirection.N;
                else input.skyDir = SkyDirection.S;
            }

            switch(input.skyDir) {
                case N: position.nextX=position.x;position.nextY = position.y + 16;break;
                case W: position.nextX=position.x - 16;position.nextY = position.y;break;
                case E: position.nextX=position.x + 16;position.nextY = position.y;break;
                default:position.nextX=position.x;position.nextY = position.y - 16;break;
            }
            position.lastPixelStep = TimeUtils.millis();
        }

        if(input.moving && TimeUtils.timeSinceMillis(position.lastPixelStep) > GlobalSettings.ONE_STEPDURATION_MS) {
            switch(input.skyDir) {
                case N: position.y += 1;break;
                case W: position.x -= 1;break;
                case E: position.x += 1;break;
                default:position.y -= 1;break;
            }
            position.lastPixelStep = TimeUtils.millis();

            switch (input.skyDir) {
                case N: if(position.y == position.nextY) input.moving = false;break;
                case E: if(position.x == position.nextX) input.moving = false;break;
                case W: if(position.x == position.nextX) input.moving = false;break;
                default: if(position.y == position.nextY) input.moving = false;break;
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touchDragged(screenX, screenY, pointer);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (Entity entity : entities) {
            InputComponent input = im.get(entity);
            input.startMoving = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        for (Entity entity : entities) {
            InputComponent input = im.get(entity);
            if(!input.moving) {
                input.startMoving = true;
            }
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
