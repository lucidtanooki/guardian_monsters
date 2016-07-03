package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import org.limbusdev.monsterworld.ecs.EntityComponentSystem;
import org.limbusdev.monsterworld.ecs.components.ColliderComponent;
import org.limbusdev.monsterworld.ecs.components.Components;
import org.limbusdev.monsterworld.ecs.components.HeroComponent;
import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.components.TeamComponent;
import org.limbusdev.monsterworld.geometry.IntRectangle;
import org.limbusdev.monsterworld.geometry.IntVector2;
import org.limbusdev.monsterworld.geometry.WarpPoint;
import org.limbusdev.monsterworld.model.BattleFactory;
import org.limbusdev.monsterworld.model.MonsterArea;
import org.limbusdev.monsterworld.utils.GlobPref;

/**
 * Created by georg on 23.11.15.
 */
public class MovementSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private Entity hero;
    private Array<WarpPoint> warpPoints;
    private EntityComponentSystem ecs;
    /* ........................................................................... CONSTRUCTOR .. */
    public MovementSystem(EntityComponentSystem ecs, Array<WarpPoint> warpPoints) {
        this.ecs = ecs;
        this.warpPoints = warpPoints;
    }
    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        hero = engine.getEntitiesFor(Family.all(HeroComponent.class).get()).first();
    }

    public void update(float deltaTime) {
        // Update Hero
        checkWarp();
        updateHero();
    }

    /**
     * Check whether hero enters warp area
     */
    public void checkWarp() {
        PositionComponent pos = Components.position.get(hero);
        Rectangle heroArea = new Rectangle(pos.x, pos.y, pos.width, pos.height);

        // Check whether hero enters warp area
        for (WarpPoint w : warpPoints) {
            if (heroArea.contains(w.x, w.y)) {
                System.out.println("Changing to Map " + w.targetID);
                ecs.changeGameArea(w.targetID, w.targetWarpPointID);
            }
        }
    }

    public void updateHero() {
        // Only move hero, when player is not speaking to an entity
        if (!Components.input.get(hero).talking) {
            makeOneStep(
                    Components.position.get(hero),
                    Components.input.get(hero),
                    Components.collision.get(hero));
        }
    }

    /**
     * Moves the entity by 1 tile
     * @param position
     * @param input
     * @param collider
     */
    public void makeOneStep(PositionComponent position, InputComponent input,
                            ColliderComponent collider) {

        // Initialize Hero Movement
        if(input.startMoving && TimeUtils.timeSinceMillis(input.firstTip) > 100 &&
                input.touchDown) {

            // Define potential next position according to the input direction
            switch(input.skyDir) {
                case N:
                    position.nextX = position.x;
                    position.nextY = position.y + GlobPref.TILE_SIZE;
                    break;
                case W:
                    position.nextX = position.x - GlobPref.TILE_SIZE;
                    position.nextY = position.y;
                    break;
                case E:
                    position.nextX = position.x + GlobPref.TILE_SIZE;
                    position.nextY = position.y;
                    break;
                default:
                    position.nextX = position.x;
                    position.nextY = position.y - GlobPref.TILE_SIZE;
                    break;
            }

            // Check whether movement is possible or blocked by a collider
            IntVector2 nextPos = new IntVector2(0,0);
            for(IntRectangle r : ecs.gameArea.getColliders()) {
                nextPos.x = position.nextX + GlobPref.TILE_SIZE / 2;
                nextPos.y = position.nextY + GlobPref.TILE_SIZE / 2;
                if (r.contains(nextPos)) return;
            }
            for(IntRectangle r : ecs.gameArea.getMovingColliders()) {
                nextPos.x = position.nextX + GlobPref.TILE_SIZE / 2;
                nextPos.y = position.nextY + GlobPref.TILE_SIZE / 2;
                if (!collider.equals(r) && r.contains(nextPos)) return;
            }

            // Update Collider Position
            collider.collider.x = position.nextX;
            collider.collider.y = position.nextY;
            position.lastPixelStep = TimeUtils.millis();    // remember time of this iteration

            input.moving = true;        // entity is moving right now
            input.startMoving = false;  // because entity now started moving
        }


        // If entity is already moving, and last incremental step has completed (long enough ago)
        if(input.moving && TimeUtils.timeSinceMillis(position.lastPixelStep) > GlobPref.ONE_STEPDURATION_MS) {

            switch(input.skyDir) {
                case N: position.y += 1;break;
                case W: position.x -= 1;break;
                case E: position.x += 1;break;
                default:position.y -= 1;break;
            }
            position.lastPixelStep = TimeUtils.millis();

            // Check if movement is complete
            boolean movementComplete=false;
            switch (input.skyDir) {
                case N:
                case S:
                    movementComplete = (position.y == position.nextY);
                    break;
                case W:
                case E:
                    movementComplete = (position.x == position.nextX);
                default:
                    break;
            }
            if(movementComplete) {
                input.moving = false;
                // Update Grid Position of Hero
                switch(input.skyDir) {
                    case N: position.onGrid.y+=1;break;
                    case S: position.onGrid.y-=1;break;
                    case E: position.onGrid.x+=1;break;
                    case W: position.onGrid.x-=1;break;
                    default: break;
                }
                System.out.println("Position on Grid: ("+position.onGrid.x+"|"+position.onGrid.y+")");
            }

            // Movement completed
            if(!input.moving) {
                // Continue movement when button is pressed
                if(input.touchDown) {
                    input.startMoving = true;
                    input.skyDir = input.nextInput;
                }

                // Check whether hero can get attacked by monsters
                for(MonsterArea ma : ecs.gameArea.getMonsterAreas()) {
                    if (ma.contains(new IntVector2(
                            position.x + GlobPref.TILE_SIZE / 2,
                            position.y + GlobPref.TILE_SIZE / 2))
                            && MathUtils.randomBoolean(ma.attackProbabilities.get(0))) {

                        System.out.print("Monster appeared!\n");
                        /* ......................................................... START BATTLE */
                        input.inBattle = true;
                        TeamComponent oppTeam = BattleFactory.getInstance().createOpponentTeam(ma);
                        ecs.hud.battleScreen.init(Components.team.get(ecs.hero), oppTeam);
                        ecs.hud.game.setScreen(ecs.hud.battleScreen);
                        /* ......................................................... START BATTLE */
                        // Stop when in a battle
                        if(input.touchDown) input.startMoving = false;
                    }
                }
            }
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
