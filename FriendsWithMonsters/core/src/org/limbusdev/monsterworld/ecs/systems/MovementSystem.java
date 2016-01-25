package org.limbusdev.monsterworld.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;

import org.limbusdev.monsterworld.ecs.EntityComponentSystem;
import org.limbusdev.monsterworld.ecs.components.CharacterSpriteComponent;
import org.limbusdev.monsterworld.ecs.components.ColliderComponent;
import org.limbusdev.monsterworld.ecs.components.Components;
import org.limbusdev.monsterworld.ecs.components.HeroComponent;
import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.components.TeamComponent;
import org.limbusdev.monsterworld.ecs.entities.HeroEntity;
import org.limbusdev.monsterworld.enums.SkyDirection;
import org.limbusdev.monsterworld.geometry.IntRectangle;
import org.limbusdev.monsterworld.geometry.IntVector2;
import org.limbusdev.monsterworld.geometry.WarpPoint;
import org.limbusdev.monsterworld.model.BattleFactory;
import org.limbusdev.monsterworld.model.MonsterArea;
import org.limbusdev.monsterworld.utils.GlobalSettings;

/**
 * Created by georg on 23.11.15.
 */
public class MovementSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private Entity hero;
    private Array<WarpPoint> warpPoints;
    private EntityComponentSystem ecs;
    private Viewport viewport;
    /* ........................................................................... CONSTRUCTOR .. */
    public MovementSystem(EntityComponentSystem ecs, Array<WarpPoint> warpPoints, Viewport viewport) {
        this.ecs = ecs;
        this.warpPoints = warpPoints;
        this.viewport = viewport;
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
        if (!Components.input.get(hero).talking)
            makeOneStep(
                    Components.position.get(hero),
                    Components.input.get(hero),
                    Components.collision.get(hero));
    }

    /**
     * Moves the entity by 1 tile
     * @param position
     * @param input
     * @param collider
     */
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
            if(Math.abs(input.touchPos.x - (position.x+ GlobalSettings.TILE_SIZE/2))
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
            for(IntRectangle r : ecs.gameArea.getColliders()) {
                nextPos.x = position.nextX + GlobalSettings.TILE_SIZE / 2;
                nextPos.y = position.nextY + GlobalSettings.TILE_SIZE / 2;
                if (r.contains(nextPos)) return;
            }
            for(IntRectangle r : ecs.gameArea.getMovingColliders()) {
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
                for(MonsterArea ma : ecs.gameArea.getMonsterAreas()) {
                    if (ma.contains(new IntVector2(
                            position.x + GlobalSettings.TILE_SIZE / 2,
                            position.y + GlobalSettings.TILE_SIZE / 2))
                            && MathUtils.randomBoolean(ma.attackProbabilities.get(0))) {

                        System.out.print("Monster appeared!\n");
                        /* ......................................................... START BATTLE */
                        input.inBattle = true;
                        TeamComponent oppTeam = BattleFactory.getInstance().createOpponentTeam(ma);
                        ecs.hud.battleScreen.init(Components.team.get(ecs.hero), oppTeam);
                        ecs.hud.game.setScreen(ecs.hud.battleScreen);
                        /* ......................................................... START BATTLE */

                    }
                }
                // Go on if finger is still on screen
                if (Gdx.input.isTouched())
                    input.startMoving = true;
            }
        }
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
