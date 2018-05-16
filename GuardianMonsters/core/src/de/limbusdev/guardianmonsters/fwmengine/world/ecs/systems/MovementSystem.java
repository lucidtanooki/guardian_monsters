package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.TimeUtils;

import de.limbusdev.guardianmonsters.Constant;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.EntityComponentSystem;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.ColliderComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.Components;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.HeroComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.InputComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PositionComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.fwmengine.world.model.MonsterArea;
import de.limbusdev.guardianmonsters.fwmengine.world.model.WarpPoint;
import de.limbusdev.guardianmonsters.guardians.battle.BattleFactory;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.utils.geometry.IntRect;
import de.limbusdev.utils.geometry.IntVec2;


/**
 * Copyright (c) Georg Eckert 2017
 */
public class MovementSystem extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private Entity hero;
    private ArrayMap<Integer,Array<WarpPoint>> warpPoints;
    private ArrayMap<Integer,Array<Rectangle>> healFields;
    private EntityComponentSystem ecs;

    /* ........................................................................... CONSTRUCTOR .. */

    public MovementSystem(
        EntityComponentSystem ecs,
        ArrayMap<Integer,Array<WarpPoint>> warpPoints,
        ArrayMap<Integer,Array<Rectangle>> healFields)
    {
        this.ecs = ecs;
        this.warpPoints = warpPoints;
        this.healFields = healFields;
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
        for (WarpPoint w : warpPoints.get(pos.layer)) {
            if (heroArea.contains(w.x, w.y)) {
                System.out.println("Changing to Map " + w.targetID);
                ecs.changeGameArea(w.targetID, w.targetWarpPointID);
            }
        }
    }

    public void checkHeal() {
        PositionComponent pos = Components.position.get(hero);
        Rectangle heroArea = new Rectangle(pos.x, pos.y, pos.width, pos.height);

        // Check whether hero enters warp area
        for (Rectangle h : healFields.get(pos.layer)) {
            if (heroArea.contains(h.x+h.width/2,h.y+h.height/2)) {
                // Heal Team
                System.out.println("Entered Healing Area");
                TeamComponent tc = Components.team.get(hero);
                boolean teamHurt = false;
                for(AGuardian m : tc.team.values())
                    if(m.getIndividualStatistics().getHP() < m.getIndividualStatistics().getHPmax()) {
                        m.getIndividualStatistics().setHP(m.getIndividualStatistics().getHPmax());
                    }
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
                    position.nextY = position.y + Constant.TILE_SIZE;
                    break;
                case W:
                    position.nextX = position.x - Constant.TILE_SIZE;
                    position.nextY = position.y;
                    break;
                case E:
                    position.nextX = position.x + Constant.TILE_SIZE;
                    position.nextY = position.y;
                    break;
                default:
                    position.nextX = position.x;
                    position.nextY = position.y - Constant.TILE_SIZE;
                    break;
            }

            // Check whether movement is possible or blocked by a collider
            IntVec2 nextPos = new IntVec2(0,0);
            for(IntRect r : ecs.gameArea.getColliders().get(position.layer)) {
                nextPos.x = position.nextX + Constant.TILE_SIZE / 2;
                nextPos.y = position.nextY + Constant.TILE_SIZE / 2;
                if (r.contains(nextPos)) return;
            }
            for(IntRect r : ecs.gameArea.getMovingColliders().get(position.layer)) {
                nextPos.x = position.nextX + Constant.TILE_SIZE / 2;
                nextPos.y = position.nextY + Constant.TILE_SIZE / 2;
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
        if(input.moving && TimeUtils.timeSinceMillis(position.lastPixelStep) > Constant.ONE_STEPDURATION_MS) {

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
                checkHeal();
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
                for(MonsterArea ma : ecs.gameArea.getMonsterAreas().get(position.layer)) {
                    if (ma.contains(new IntVec2(
                            position.x + Constant.TILE_SIZE / 2,
                            position.y + Constant.TILE_SIZE / 2))
                            && MathUtils.randomBoolean(ma.teamSizeProbabilities.get(0))) {

                        System.out.print("Monster appeared!\n");

                        //............................................................. START BATTLE
                        // TODO change min and max levels
                        input.inBattle = true;
                        ArrayMap<Integer, Float> guardianProbabilities = new ArrayMap<>();
                        for(int i=0; i<ma.monsters.size; i++) {
                            guardianProbabilities.put(ma.monsters.get(i), ma.monsterProbabilities.get(i));
                        }
                        Team oppTeam = BattleFactory.getInstance().createOpponentTeam(
                            guardianProbabilities,ma.teamSizeProbabilities,1,1
                        );
                        ecs.hud.battleScreen.init(Components.team.get(ecs.hero).team, oppTeam);
                        Services.getScreenManager().pushScreen(ecs.hud.battleScreen);
                        //............................................................. START BATTLE

                        // Stop when in a battle
                        if(input.touchDown) input.startMoving = false;
                    }
                }
            }
        }
    }
    //............................................................................ GETTERS & SETTERS
}
