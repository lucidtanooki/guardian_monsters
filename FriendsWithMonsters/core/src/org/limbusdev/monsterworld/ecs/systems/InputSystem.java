package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.ecs.components.ColliderComponent;
import org.limbusdev.monsterworld.ecs.components.Components;
import org.limbusdev.monsterworld.ecs.components.ConversationComponent;
import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.components.TeamComponent;
import org.limbusdev.monsterworld.ecs.components.TitleComponent;
import org.limbusdev.monsterworld.enums.SkyDirection;
import org.limbusdev.monsterworld.geometry.IntRectangle;
import org.limbusdev.monsterworld.geometry.IntVector2;
import org.limbusdev.monsterworld.model.BattleFactory;
import org.limbusdev.monsterworld.model.MonsterArea;
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
    private ImmutableArray<Entity> entities;
    private ImmutableArray<Entity> speakingEntities;
    private ImmutableArray<Entity> signEntities;

    private Viewport viewport;
    private OutdoorGameArea gameArea;
    private HUD hud;
    private Entity hero;
    /* ........................................................................... CONSTRUCTOR .. */
    public InputSystem(Viewport viewport, OutdoorGameArea gameArea, HUD hud, Entity hero) {
        this.viewport = viewport;
        this.gameArea = gameArea;
        this.hud = hud;
        this.hero = hero;
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        // Hero
        entities = engine.getEntitiesFor(Family.all(
                InputComponent.class,
                PositionComponent.class,
                ColliderComponent.class).get());

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
        for (Entity entity : entities) {
            InputComponent input = Components.getInputComponent(entity);
            PositionComponent position = Components.getPositionComponent(entity);
            ColliderComponent collider = Components.getColliderComponent(entity);

            // Only move hero, when player is not speaking to an entity
            if(!input.talking) makeOneStep(position, input, collider);
        }
    }

    public void makeOneStep(PositionComponent position, InputComponent input,
                            ColliderComponent collider) {
        if(input.startMoving) {
            // Define direction
            input.touchPos.x = Gdx.input.getX();
            input.touchPos.y = Gdx.input.getY();
            viewport.unproject(input.touchPos);

            // calculate characters main moving direction for sprite choosing
            if(new Rectangle(position.x, position.y, position.width, position.height)
                    .contains(input.touchPos.x, input.touchPos.y)) return;

            // Define direction of movement
            if(Math.abs(input.touchPos.x - (position.x+GlobalSettings.TILE_SIZE/2))
                    > Math.abs(input.touchPos.y - (position.y+GlobalSettings.TILE_SIZE/2))) {
                if(input.touchPos.x > position.x+GlobalSettings.TILE_SIZE/2)
                    input.skyDir = SkyDirection.E;
                else input.skyDir = SkyDirection.W;
            } else {
                if(input.touchPos.y > position.y+GlobalSettings.TILE_SIZE/2)
                    input.skyDir = SkyDirection.N;
                else input.skyDir = SkyDirection.S;
            }

            // Define potential next position according to the input direction
            switch(input.skyDir) {
                case N: position.nextX=position.x;position.nextY = position.y + 16;break;
                case W: position.nextX=position.x - 16;position.nextY = position.y;break;
                case E: position.nextX=position.x + 16;position.nextY = position.y;break;
                default:position.nextX=position.x;position.nextY = position.y - 16;break;
            }

            //Check whether movement is possible or blocked by a collider
            IntVector2 nextPos = new IntVector2(0,0);
            for(IntRectangle r : gameArea.getColliders()) {
                nextPos.x = position.nextX + GlobalSettings.TILE_SIZE / 2;
                nextPos.y = position.nextY + GlobalSettings.TILE_SIZE / 2;
                if (r.contains(nextPos)) return;
            }
            for(IntRectangle r : gameArea.getMovingColliders()) {
                nextPos.x = position.nextX + GlobalSettings.TILE_SIZE / 2;
                nextPos.y = position.nextY + GlobalSettings.TILE_SIZE / 2;
                if (!collider.equals(r) && r.contains(nextPos)) return;
            }

            collider.collider.x = position.nextX;
            collider.collider.y = position.nextY;
            position.lastPixelStep = TimeUtils.millis();    // remember time of this step
            input.moving = true;
            input.startMoving = false;  // because entity now started moving
        }

        // If entity is already moving, and last step has completed (long enough ago)
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


            if(!input.moving) {

                // Check whether hero can get attacked by monsters
                for(MonsterArea ma : gameArea.getMonsterAreas()) {
                    if (ma.contains(new IntVector2(
                            position.x + GlobalSettings.TILE_SIZE / 2,
                            position.y + GlobalSettings.TILE_SIZE / 2))
                            && MathUtils.randomBoolean(ma.attackProbabilities.get(0))) {

                        System.out.print("Monster appeared!\n");
                        /* ......................................................... START BATTLE */
                        input.inBattle = true;
                        TeamComponent oppTeam = BattleFactory.getInstance().createOpponentTeam(ma);
                        hud.battleScreen.init(Components.team.get(hero), oppTeam);
                        hud.game.setScreen(hud.battleScreen);
                        /* ......................................................... START BATTLE */

                    }
                }
                // Go on if finger is still on screen
                if (Gdx.input.isTouched())
                    input.startMoving = true;
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
        Vector2 touchPos = new Vector2(screenX, screenY);
        viewport.unproject(touchPos);
        boolean touchedSpeaker, touchedSign;
        touchedSign = touchedSpeaker = false;

        // Loop through entities with text and test weather they're near enough
        for(Entity e : speakingEntities) {
            ColliderComponent coll = Components.getColliderComponent(e);
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

        if(!touchedSpeaker && !touchedSign) touchDragged(screenX, screenY, pointer);
        else Components.getInputComponent(hero).talking = true;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        for (Entity entity : entities) {
            InputComponent input = Components.getInputComponent(entity);
            input.startMoving = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        for (Entity entity : entities) {
            InputComponent input = Components.getInputComponent(entity);
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
