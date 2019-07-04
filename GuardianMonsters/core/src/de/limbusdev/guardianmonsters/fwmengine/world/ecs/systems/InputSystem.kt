package de.limbusdev.guardianmonsters.fwmengine.world.ecs.systems

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntitySystem
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.Viewport

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.enums.SkyDirection
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.*
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.EntityFamilies
import de.limbusdev.guardianmonsters.fwmengine.world.ecs.entities.HeroEntity
import de.limbusdev.guardianmonsters.fwmengine.world.ui.HUD
import de.limbusdev.guardianmonsters.utils.getComponent
import de.limbusdev.utils.geometry.IntVec2
import de.limbusdev.utils.logDebug
import kotlin.math.abs


/**
 * The InputSystem extends [EntitySystem] and implements an[InputProcessor]. It enters
 * all catched input into the hero's InputComponent so it can be processed by other systems later.
 * Additionally it moves the hero step by step.
 * Created by georg on 22.11.15.
 */
class InputSystem
(
        private val viewport: Viewport,
        private val hud     : HUD
)
    : EntitySystem(), InputProcessor
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    companion object { const val TAG = "InputSystem" }

    private lateinit var speakingEntities: ImmutableArray<Entity>
    private lateinit var hero            : Entity

    private var lastDirKey = SkyDirection.S
    private val keyboard   = false


    // --------------------------------------------------------------------------------------------- METHODS
    override fun addedToEngine(engine: Engine)
    {
        // hero
        hero = engine.getEntitiesFor(Family.all(HeroComponent::class.java).get()).first()

        // Speaking: Signs, People and so on
        speakingEntities = engine.getEntitiesFor(EntityFamilies.living)
    }

    override fun update(deltaTime: Float)
    {
        // Unblock talking entities if hero isn't talking anymore
        for (speaker in speakingEntities)
        {
            if(speaker.getComponent<PathComponent>()?.talking == true)
            {
                if(hero.getComponent<InputComponent>()?.talking == false)
                {
                    speaker.getComponent<PathComponent>()?.talking = false
                }
            }
        }
    }

    /**
     * Checks the tiles right next (top, right, bottom, left) if there are entities the character
     * can interact with
     * @return
     */
    fun checkForNearInteractiveObjects(pos: PositionComponent, dir: SkyDirection): Entity?
    {
        val checkGridCell = IntVec2(pos.onGrid)

        checkGridCell += when (dir)
        {
            SkyDirection.N  -> IntVec2(0,1)
            SkyDirection.S  -> IntVec2(0,-1)
            SkyDirection.E  -> IntVec2(1,0)
            SkyDirection.W  -> IntVec2(-1,0)
            else            -> IntVec2(0,0)
        }

        logDebug(TAG) { "Grid cell to be checked: $checkGridCell" }

        for (e in engine.getEntitiesFor(Family.all(PositionComponent::class.java).get()))
        {
            val positionComponent = e.getComponent<PositionComponent>()
            if (positionComponent != null && e !is HeroEntity)
            {
                logDebug(TAG) { "Grid Cell of tested Entity: ${positionComponent.onGrid}" }

                // Is there an entity?
                if (positionComponent.onGrid == checkGridCell) { return e }
            }
        }

        return null
    }

    /**
     * Returns the movement direction from given start and target position
     * @param entX  Start Direction X
     * @param entY  Start Direction Y
     * @param targetX
     * @param targetY
     * @return Main Direction
     */
    fun decideMovementDirection(entX: Int, entY: Int, targetX: Float, targetY: Float): SkyDirection
    {
        val tileCenter = Constant.TILE_SIZE / 2

        return if (abs(targetY - (entY + tileCenter)) > abs(targetX - (entX + tileCenter)))
        {
            // Vertical Movement
            if (targetY > entY + tileCenter) { SkyDirection.N }  // hero moving north
            else                             { SkyDirection.S }  // hero moving south
        }
        else
        {
            // Horizontal Movement
            if (targetX > entX + tileCenter) { SkyDirection.E }  // hero moving east
            else                             { SkyDirection.W }  // hero moving west
        }
    }

    /**
     * Move only if touch appears far enough from hero
     * @param entX
     * @param entY
     * @param target
     * @return
     */
    fun decideIfToMove(entX: Int, entY: Int, target: Vector2): Boolean
    {
        return target.dst((entX + Constant.TILE_SIZEf / 2), (entY + Constant.TILE_SIZEf / 2)) > 2 * Constant.TILE_SIZEf
    }






    ////////////////////////////////////////////////////////////////////////////////////////////////
    // KEYBOARD INPUT - Inactive

    override fun keyDown(keycode: Int): Boolean
    {
        // TODO re-enable keyboard steering (not very important)
//        // If the pressed key is one of the arrow keys
//        SkyDirection typedDir = null;
//        switch (keycode) {
//            case Input.Keys.UP:
//                typedDir = SkyDirection.N;
//                break;
//            case Input.Keys.DOWN:
//                typedDir = SkyDirection.S;
//                break;
//            case Input.Keys.LEFT:
//                typedDir = SkyDirection.W;
//                break;
//            case Input.Keys.RIGHT:
//                typedDir = SkyDirection.E;
//                break;
//            default:
//                break;
//        }
//        keyboard = true;
//
//        if (typedDir != null) {
//            Components.input.get(hero).touchDown = true;
//            lastDirKey = typedDir;
//        }
        return false
    }

    override fun keyUp(keycode: Int): Boolean
    {
//        // If none of the arrow keys is pressed
//        if(!Gdx.input.isKeyPressed(Input.Keys.UP) &&
//                !Gdx.input.isKeyPressed(Input.Keys.DOWN) &&
//                !Gdx.input.isKeyPressed(Input.Keys.LEFT) &&
//                !Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
//            Components.input.get(hero).touchDown = false;
//            return true;
//        }
        return false
    }

    override fun keyTyped(character: Char) = false

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int) = false

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int) = false

    override fun mouseMoved(screenX: Int, screenY: Int) = false

    override fun scrolled(amount: Int) = false
}
