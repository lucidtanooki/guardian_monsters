package de.limbusdev.guardianmonsters.fwmengine.guardosphere

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ArrayMap
import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere
import de.limbusdev.utils.extensions.set
import de.limbusdev.utils.geometry.IntVec2
import ktx.actors.minus
import ktx.actors.plus
import ktx.scene2d.table

/**
 * GuardoSphereChoiceWidget
 *
 * @author Georg Eckert 2017
 */
class GuardoSphereChoiceWidget
(
        private val skin: Skin,
        private val sphere: GuardoSphere,
        private val buttonGroup: ButtonGroup<Button>
)
    : Group()
{
    private val table: Table
    private val buttonGrid: ArrayMap<Int, ArrayMap<Int, Button>> // 7 x 5 grid

    var callback: (Int) -> Unit

    init
    {
        // Create 7 x 5 grid for storing buttons
        buttonGrid = ArrayMap()
        for(row in 0..4)
        {
            buttonGrid[row] = ArrayMap()
        }

        // Create dummy callback
        callback = {println("$TAG: dummy callback")}

        // Setup Layout
        setSize(WIDTH, HEIGHT)

        val background = Image(skin.getDrawable("guardosphere-frame"))
        background.setSize(WIDTH,HEIGHT)
        background.setPosition(0f, 0f, Align.bottomLeft)


        table = table {

            setSize(7*32f, 5*32f)
            setPosition(14f, 10f)
            align(Align.bottomLeft)
        }

        // Setup Hierarchy
        this+background
        this+table

        refresh(0)
    }

    fun refresh(page: Int)
    {
        for(row in buttonGrid.values())
        {
            for(cell in row.values())
            {
                table-cell
            }
        }

        buttonGroup.clear()
        table.clear()

        var key = page*35
        for(row in 0..4)
        {
            table.row()

            for(col in 0..6)
            {
                val guardian = sphere[key]
                val monsterButton = GuardoSphereButton(skin, guardian)

                table.add<ImageButton>(monsterButton).width(32f).height(32f)
                buttonGroup.add(monsterButton)
                buttonGrid[row][col] = monsterButton

                monsterButton.addListener(object : ActorGestureListener()
                {
                    var startPosition = IntVec2(0,0)

                    override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int)
                    {
                        super.touchUp(event, x, y, pointer, button)
                        var field = CoordinatesToGridPosition(monsterButton.x, monsterButton.y)
                        val dropAt = GridPositionToCoordinates(field.y, field.x)
                        monsterButton.x = dropAt.x
                        monsterButton.y = dropAt.y
                        val moveTargetTo = GridPositionToCoordinates(startPosition.y, startPosition.x)
                        buttonGrid[field.y][field.x].setPosition(moveTargetTo.x, moveTargetTo.y)
                    }

                    override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int)
                    {
                        super.touchDown(event, x, y, pointer, button)
                        callback.invoke(key)
                        startPosition = CoordinatesToGridPosition(monsterButton.x, monsterButton.y)
                        println(startPosition.toString())
                    }

                    override fun pan(event: InputEvent?, x: Float, y: Float, deltaX: Float, deltaY: Float)
                    {
                        monsterButton.x += deltaX
                        monsterButton.y += deltaY
                    }
                })

                key++
            }
        }
    }

    private fun CoordinatesToGridPosition(x: Float, y: Float) : IntVec2
    {
        return IntVec2(
                    (x.toInt()+16) / 32, // Column
                4 - (y.toInt()+16) / 32) // Row
    }

    private fun GridPositionToCoordinates(row: Int, col: Int) : Vector2
    {
        return Vector2(
                col*32f,
                (4 - row)*32f
        )
    }

    companion object
    {
        private const val TAG = "GuardoSphereChoiceWidget"
        private const val WIDTH = 252f
        private const val HEIGHT = 180f
    }
}
