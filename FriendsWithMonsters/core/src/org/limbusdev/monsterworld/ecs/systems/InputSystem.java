package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.ecs.components.DynamicBodyComponent;
import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.enums.SkyDirection;

/**
 * Created by georg on 22.11.15.
 */
public class InputSystem extends EntitySystem implements InputProcessor {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> entities;
    private float elapsedTime = 0;

    private ComponentMapper<DynamicBodyComponent> pm
            = ComponentMapper.getFor(DynamicBodyComponent.class);
    private ComponentMapper<InputComponent> im
            = ComponentMapper.getFor(InputComponent.class);

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
                DynamicBodyComponent.class,
                InputComponent.class).get());
    }

    public void update(float deltaTime) {
        elapsedTime += deltaTime;
        for (Entity entity : entities) {
            DynamicBodyComponent body = pm.get(entity);
            InputComponent input = im.get(entity);

            if(input.moving) move(input.touchPos, body, input);
                /* Keyboard Input */
//                switch(input.direction) {
//                    case NORTH: body.dynamicBody.setLinearVelocity(0,5);break;
//                    case SOUTH: body.dynamicBody.setLinearVelocity(0,-5);break;
//                    case EAST: body.dynamicBody.setLinearVelocity(5,0);break;
//                    case WEST: body.dynamicBody.setLinearVelocity(-5,0);break;
//                    default: body.dynamicBody.setLinearVelocity(0,0);break;
//                }
            else body.dynamicBody.setLinearVelocity(0,0);
        }
    }

    public void move(Vector3 touchPos, DynamicBodyComponent body, InputComponent input) {
        touchPos.x = Gdx.input.getX();
        touchPos.y = Gdx.input.getY();
        viewport.unproject(touchPos);

        // calculate characters main moving direction for sprite choosing
        if(Math.abs(touchPos.x - body.dynamicBody.getPosition().x)
                > Math.abs(touchPos.y - body.dynamicBody.getPosition().y)) {
            if(touchPos.x > body.dynamicBody.getPosition().x) input.skyDir = SkyDirection.E;
            else input.skyDir = SkyDirection.W;
        } else {
            if(touchPos.y > body.dynamicBody.getPosition().y) input.skyDir = SkyDirection.N;
            else input.skyDir = SkyDirection.S;
        }

        switch(input.skyDir) {
            case N: directionVector.set(0,3);break;
            case W: directionVector.set(-3,0);break;
            case E: directionVector.set(3,0);break;
            default:directionVector.set(0, -3);break;
        }

        body.dynamicBody.setLinearVelocity(directionVector);
    }

    @Override
    public boolean keyDown(int keycode) {
        for (Entity entity : entities) {
            InputComponent input = im.get(entity);
            switch(keycode) {
                case Input.Keys.UP:     input.skyDir = SkyDirection.N;break;
                case Input.Keys.LEFT:   input.skyDir = SkyDirection.W;break;
                case Input.Keys.RIGHT:  input.skyDir = SkyDirection.E;break;
                case Input.Keys.DOWN:   input.skyDir = SkyDirection.S;break;
                case Input.Keys.A:
                    System.out.println("Attacking!");
                    break;
                default:break;
            }
            if(keycode != Input.Keys.A) input.moving = true;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (Entity entity : entities) {
            InputComponent input = im.get(entity);
            if (keycode == Input.Keys.A) {
                // TODO
            }
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return touchDragged(Gdx.input.getX(), Gdx.input.getY(),pointer);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (Entity entity : entities) {
            InputComponent input = im.get(entity);
            input.moving = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        for (Entity entity : entities) {
            InputComponent input = im.get(entity);
            input.touchPos.x = screenX;
            input.touchPos.y = screenY;
            viewport.unproject(input.touchPos);
            input.moving = true;
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
