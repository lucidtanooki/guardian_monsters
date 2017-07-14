package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.HeroComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.HeroEntity;
import de.limbusdev.guardianmonsters.enums.SkyDirection;
import de.limbusdev.guardianmonsters.utils.geometry.IntVec2;
import de.limbusdev.guardianmonsters.fwmengine.world.ui.HUD;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.EntityFamilies;
import de.limbusdev.guardianmonsters.Constant;


/**
 * The InputSystem extends {@link EntitySystem} and implements an{@link InputProcessor}. It enters
 * all catched input into the hero's InputComponent so it can be processed by other systems later.
 * Additionally it moves the hero step by step.
 * Created by georg on 22.11.15.
 */
public class InputSystem extends EntitySystem implements InputProcessor {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> speakingEntities;

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
        hero = engine.getEntitiesFor(Family.all(HeroComponent.class).get()).first();

        // Speaking: Signs, People and so on
        speakingEntities = engine.getEntitiesFor(EntityFamilies.living);

    }

    public void update(float deltaTime) {
        // Unblock talking entities if hero isn't talking anymore
        for(Entity e : speakingEntities) {
            if (Components.path.get(e).talking) {
                if (!Components.input.get(hero).talking) {
                    Components.path.get(e).talking = false;
                }
            }
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        /*
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
        }*/
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        /*// If none of the arrow keys is pressed
        if(!Gdx.input.isKeyPressed(Input.Keys.UP) &&
                !Gdx.input.isKeyPressed(Input.Keys.DOWN) &&
                !Gdx.input.isKeyPressed(Input.Keys.LEFT) &&
                !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            Components.input.get(hero).touchDown = false;
            return true;
        }*/
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
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
     * Checks the tiles right next (top, right, bottom, left) if there are entities the character
     * can interact with
     * @return
     */
    public Entity checkForNearInteractiveObjects(PositionComponent pos, SkyDirection dir) {
        Entity nearEntity=null;
        IntVec2 checkGridCell = new IntVec2(pos.onGrid.x,pos.onGrid.y);

        switch(dir) {
            case N: checkGridCell.y+=1;break;
            case S: checkGridCell.y-=1;break;
            case E: checkGridCell.x+=1;break;
            case W: checkGridCell.x-=1;break;
            default: break;
        }

        if(Constant.DEBUGGING_ON)
            System.out.println("Grid cell to be checked: ("+checkGridCell.x+"|"+checkGridCell.y+")");

        for(Entity e : this.getEngine().getEntitiesFor(Family.all(PositionComponent.class).get())) {

            if (Components.position.get(e) != null && !(e instanceof HeroEntity)) {
                PositionComponent p = Components.position.get(e);

                if(Constant.DEBUGGING_ON)
                    System.out.println("Grid Cell of tested Entity: ("+p.onGrid.x+"|"+p.onGrid.y+")");

                // Is there an entity?
                if (p.onGrid.x == checkGridCell.x && p.onGrid.y == checkGridCell.y)
                    nearEntity = e;
            }
        }

        return nearEntity;
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
        int tileCenter = Constant.TILE_SIZE/2;

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
        if(target.dst(entX+ Constant.TILE_SIZE/2,entY+ Constant.TILE_SIZE/2) >
                2* Constant.TILE_SIZE) move = true;
        else
            move = false;
        return move;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
