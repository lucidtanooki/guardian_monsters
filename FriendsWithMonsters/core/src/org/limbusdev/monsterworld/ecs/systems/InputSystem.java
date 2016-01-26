package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.ecs.components.ColliderComponent;
import org.limbusdev.monsterworld.ecs.components.Components;
import org.limbusdev.monsterworld.ecs.components.ConversationComponent;
import org.limbusdev.monsterworld.ecs.components.HeroComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.components.TitleComponent;
import org.limbusdev.monsterworld.enums.SkyDirection;
import org.limbusdev.monsterworld.geometry.IntVector2;
import org.limbusdev.monsterworld.screens.HUD;
import org.limbusdev.monsterworld.utils.GlobalSettings;

/**
 * The InputSystem extends {@link EntitySystem} and implements an{@link InputProcessor}. It enters
 * all catched input into the hero's InputComponent so it can be processed by other systems later.
 * Additionally it moves the hero step by step.
 * Created by georg on 22.11.15.
 */
public class InputSystem extends EntitySystem implements InputProcessor {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> speakingEntities;
    private ImmutableArray<Entity> signEntities;

    private Viewport viewport;
    private HUD hud;
    private Entity hero;
    private boolean keyboard;
    private SkyDirection lastDirKey;
    /* ........................................................................... CONSTRUCTOR .. */
    public InputSystem(Viewport viewport, HUD hud) {
        this.viewport = viewport;
        this.hud = hud;
        keyboard = false;
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        // Hero
        hero = engine.getEntitiesFor(Family.all(
                HeroComponent.class).get()).first();

        // Speaking: Signs, People and so on
        speakingEntities = engine.getEntitiesFor(Family
                .all(ConversationComponent.class)
                .exclude(TitleComponent.class).get());

        // Signs
        signEntities = engine.getEntitiesFor(Family.all(
                TitleComponent.class,
                ConversationComponent.class
        ).get());
    }

    public void update(float deltaTime) {
        // If screen is touched continue movement
        if(Components.input.get(hero).touchDown) {
            Vector2 pos = new Vector2(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(pos);
            PositionComponent heroPos = Components.position.get(hero);

            if(!Components.input.get(hero).moving) {
                // Touch
                if(!keyboard) {
                    Components.input.get(hero).skyDir
                            = decideMovementDirection(heroPos.x, heroPos.y, pos.x, pos.y);
                    Components.input.get(hero).startMoving = decideIfToMove(heroPos.x, heroPos.y, pos);
                } else {
                    Components.input.get(hero).skyDir = lastDirKey;
                    Components.input.get(hero).startMoving = true;
                }
            }

        }
    }

    @Override
    public boolean keyDown(int keycode) {
        // If the pressed key is one of the arrow keys
        SkyDirection typedDir = null;
        switch (keycode) {
            case Input.Keys.UP:
                typedDir = SkyDirection.N;
                break;
            case Input.Keys.DOWN:
                typedDir = SkyDirection.S;
                break;
            case Input.Keys.LEFT:
                typedDir = SkyDirection.W;
                break;
            case Input.Keys.RIGHT:
                typedDir = SkyDirection.E;
                break;
            default:
                break;
        }
        keyboard = true;

        if (typedDir != null) {
            Components.input.get(hero).touchDown = true;
            lastDirKey = typedDir;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        // If none of the arrow keys is pressed
        if(!Gdx.input.isKeyPressed(Input.Keys.UP) &&
                !Gdx.input.isKeyPressed(Input.Keys.DOWN) &&
                !Gdx.input.isKeyPressed(Input.Keys.LEFT) &&
                !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            Components.input.get(hero).touchDown = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        keyboard = false;

        // Unproject touch position
        Vector2 touchPos = new Vector2(screenX, screenY);
        viewport.unproject(touchPos);

        boolean touchedSpeaker, touchedSign;
        touchedSign = touchedSpeaker = false;

        // Loop through entities with text and test weather they're near enough
        for(Entity e : speakingEntities) {
            ColliderComponent coll = Components.collision.get(e);
            IntVector2 touchedAt = new IntVector2(MathUtils.round(touchPos.x),MathUtils.round(touchPos.y));
            float dist = touchPos.dst(
                    Components.collision.get(hero).collider.x
                            + GlobalSettings.TILE_SIZE/2,
                    Components.collision.get(hero).collider.y
                            + GlobalSettings.TILE_SIZE/2
            );
            if(coll.collider.contains(touchedAt) && dist < GlobalSettings.TILE_SIZE*1.5) {
                System.out.print("Touched speaker\n");
                touchedSpeaker = true;
                hud.openConversation(Components.conversation.get(e).text);
            }
        }

        for(Entity e : signEntities) {
            ColliderComponent coll = Components.getColliderComponent(e);
            IntVector2 touchedAt = new IntVector2(MathUtils.round(touchPos.x),MathUtils.round(touchPos.y));
            float dist = touchPos.dst(
                    Components.collision.get(hero).collider.x
                            + GlobalSettings.TILE_SIZE/2,
                    Components.collision.get(hero).collider.y
                            + GlobalSettings.TILE_SIZE/2
            );
            if(coll.collider.contains(touchedAt) && dist < GlobalSettings.TILE_SIZE*1.5) {
                System.out.print("Touched sign\n");
                touchedSign = true;
                hud.openSign(
                        Components.title.get(e).text,
                        Components.conversation.get(e).text);
            }
        }
        if(touchedSpeaker || touchedSign) Components.getInputComponent(hero).talking = true;
        else touchDragged(screenX, screenY, pointer);

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Stop Hero Movement
        Components.input.get(hero).touchDown = false;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Components.input.get(hero).touchDown = true;
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

    /**
     * Returns the movement direction from given start and target position
     * @param entX  Start Direction X
     * @param entY  Start Direction Y
     * @param targetX
     * @param targetY
     * @return Main Direction
     */
    public SkyDirection decideMovementDirection(int entX, int entY, float targetX, float targetY) {
        SkyDirection dir;
        int tileCenter = GlobalSettings.TILE_SIZE/2;

        if(Math.abs(targetY - (entY + tileCenter)) > Math.abs(targetX - (entX+tileCenter))) {
            // Vertical Movement
            if(targetY > (entY+tileCenter)) {
                // Hero moving north
                dir = SkyDirection.N;
            } else {
                // Hero moving south
                dir = SkyDirection.S;
            }
        } else {
            // Horizontal Movement
            if(targetX > (entX+tileCenter)) {
                // Hero moving east
                dir = SkyDirection.E;
            } else {
                // Hero moving west
                dir = SkyDirection.W;
            }
        }

        return dir;
    }

    /**
     * Move only if touch appears far enough from hero
     * @param entX
     * @param entY
     * @param target
     * @return
     */
    public boolean decideIfToMove(int entX, int entY, Vector2 target) {
        boolean move;
        if(target.dst(entX+GlobalSettings.TILE_SIZE/2,entY+GlobalSettings.TILE_SIZE/2) >
                2*GlobalSettings.TILE_SIZE) move = true;
        else
            move = false;
        return move;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
