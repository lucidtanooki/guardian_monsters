package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.ecs.components.ColliderComponent;
import org.limbusdev.monsterworld.ecs.components.Components;
import org.limbusdev.monsterworld.ecs.components.ConversationComponent;
import org.limbusdev.monsterworld.ecs.components.HeroComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.components.TitleComponent;
import org.limbusdev.monsterworld.ecs.entities.HeroEntity;
import org.limbusdev.monsterworld.enums.SkyDirection;
import org.limbusdev.monsterworld.geometry.IntVector2;
import org.limbusdev.monsterworld.screens.HUD;
import org.limbusdev.monsterworld.utils.EntityFamilies;
import org.limbusdev.monsterworld.utils.GlobalSettings;

import javax.xml.bind.annotation.XmlElementDecl;

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
    private Circle joyStickArea, joyStick;
    /* ........................................................................... CONSTRUCTOR .. */
    public InputSystem(Viewport viewport, HUD hud) {
        this.viewport = viewport;
        this.hud = hud;
        keyboard = false;
        this.joyStickArea = new Circle(98,98,90);
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        // Hero
        hero = engine.getEntitiesFor(Family.all(
                HeroComponent.class).get()).first();

        // Speaking: Signs, People and so on
        speakingEntities = engine.getEntitiesFor(EntityFamilies.living);

    }

    public void update(float deltaTime) {

        // Unblock talking entities if hero isn't talking anymore
        for(Entity e : speakingEntities)
                if(Components.path.get(e).talking)
                    if(!Components.input.get(hero).talking)
                        Components.path.get(e).talking = false;


        // If screen is touched continue movement
        if(Components.input.get(hero).touchDown) {
            Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);

            if(GlobalSettings.JoyStick) {
                hud.stage.getCamera().unproject(touchPos);
                // Use Display-Joystick
                if(joyStickArea.contains(touchPos.x, touchPos.y)) {
                    hud.updateJoyStick(touchPos.x, touchPos.y);
                    if (!Components.input.get(hero).moving) {
                        Components.input.get(hero).skyDir
                                = decideMovementDirection(98, 98, touchPos.x, touchPos.y);
                        Components.input.get(hero).startMoving = decideIfToMove(98, 98,
                                new Vector2(touchPos.x, touchPos.y));
                    }
                } else {
                    // Reset JoyStick Position
                    hud.resetJoyStick();
                }
            } else {
                viewport.unproject(touchPos);
                PositionComponent heroPos = Components.position.get(hero);
                if (!Components.input.get(hero).moving) {
                    // Touch
                    if (!keyboard) {
                        Components.input.get(hero).skyDir
                                = decideMovementDirection(heroPos.x, heroPos.y, touchPos.x, touchPos.y);
                        Components.input.get(hero).startMoving = decideIfToMove(heroPos.x, heroPos.y,
                                new Vector2(touchPos.x, touchPos.y));
                    } else {
                        Components.input.get(hero).skyDir = lastDirKey;
                        Components.input.get(hero).startMoving = true;
                    }
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

        touchDragged(screenX, screenY, pointer);

        // Unproject touch position
        Vector2 touchPos = new Vector2(screenX, screenY);
        viewport.unproject(touchPos);

        boolean touchedSpeaker, touchedSign;
        touchedSign = touchedSpeaker = false;

        // Check touch distance to hero
        PositionComponent heroPos = Components.position.get(hero);
        float distHeroTouch = touchPos.dst(heroPos.getCenter().x, heroPos.getCenter().y);

        // If touch ist near enough
        if (distHeroTouch < GlobalSettings.TILE_SIZE * 1.5 &&
                distHeroTouch > GlobalSettings.TILE_SIZE/2) {

            Entity touchedEntity = checkForNearInteractiveObjects(
                    Components.position.get(hero),
                    decideMovementDirection(heroPos.x, heroPos.y, touchPos.x, touchPos.y));

            // If there is an entity near enough
            if (touchedEntity != null) {

                // Living Entity
                if (EntityFamilies.living.matches(touchedEntity)) {
                    System.out.print("Touched speaker\n");
                    touchedSpeaker = true;
                    Components.path.get(touchedEntity).talking=true;
                    SkyDirection talkDir;
                    switch(Components.input.get(hero).skyDir) {
                        case N: talkDir = SkyDirection.SSTOP;break;
                        case S: talkDir = SkyDirection.NSTOP;break;
                        case W: talkDir = SkyDirection.ESTOP;break;
                        case E: talkDir = SkyDirection.WSTOP;break;
                        default: talkDir = SkyDirection.SSTOP;
                    }
                    Components.path.get(touchedEntity).talkDir = talkDir;
                    hud.openConversation(Components.conversation.get(touchedEntity).text);
                }

                // Sign Entity
                if (EntityFamilies.signs.matches(touchedEntity)) {
                    System.out.print("Touched sign\n");
                    touchedSign = true;
                    hud.openSign(
                            Components.title.get(touchedEntity).text,
                            Components.conversation.get(touchedEntity).text);
                }
            }
        }
        if (touchedSpeaker || touchedSign) Components.getInputComponent(hero).talking = true;

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // Stop Hero Movement
        Components.input.get(hero).touchDown = false;
        if(GlobalSettings.JoyStick) hud.resetJoyStick();
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
     * Checks the tiles right next (top, right, bottom, left) if there are entities the character
     * can interact with
     * @return
     */
    public Entity checkForNearInteractiveObjects(PositionComponent pos, SkyDirection dir) {

        Entity nearEntity=null;
        IntVector2 checkGridCell = new IntVector2(pos.onGrid.x,pos.onGrid.y);

        switch(dir) {
            case N: checkGridCell.y+=1;break;
            case S: checkGridCell.y-=1;break;
            case E: checkGridCell.x+=1;break;
            case W: checkGridCell.x-=1;break;
            default: break;
        }

        if(GlobalSettings.DEBUGGING_ON)
            System.out.println("Grid cell to be checked: ("+checkGridCell.x+"|"+checkGridCell.y+")");

        for(Entity e : this.getEngine().getEntitiesFor(Family.all(PositionComponent.class).get())) {

            if (Components.position.get(e) != null && !(e instanceof HeroEntity)) {
                PositionComponent p = Components.position.get(e);

                if(GlobalSettings.DEBUGGING_ON)
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
