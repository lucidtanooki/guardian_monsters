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

                monsterButton.addListener(GestureListener(this, key, monsterButton, page))

                key++
            }
        }
    }

    fun swapButtonsOnGrid(cellA: IntVec2, cellB: IntVec2, pageA: Int, pageB: Int)
    {
        // Get buttons
        val buttonA = buttonGrid[cellA.y][cellA.x]
        val buttonB = buttonGrid[cellB.y][cellB.x]

        // Set new grid entries
        buttonGrid[cellA.y][cellA.x] = buttonB
        buttonGrid[cellB.y][cellB.x] = buttonA

        // Move buttons to new positions
        val newPositionA = gridToVector(cellB.y, cellB.x)
        val newPositionB = gridToVector(cellA.y, cellA.x)
        buttonA.setPosition(newPositionA.x, newPositionA.y)
        buttonB.setPosition(newPositionB.x, newPositionB.y)

        // Swap guardians
        val guardianA = gridToSphere(cellA.y, cellA.x, pageA)
        val guardianB = gridToSphere(cellB.y, cellB.x, pageB)

        sphere.swap(guardianA, guardianB)

        // Assign new listeners
        buttonA.clearListeners()
        buttonA.addListener(GestureListener(this, guardianB, buttonA, pageB))
        buttonB.clearListeners()
        buttonB.addListener(GestureListener(this, guardianA, buttonB, pageA))
    }

    companion object
    {
        private const val TAG = "GuardoSphereChoiceWidget"
        private const val WIDTH = 252f
        private const val HEIGHT = 180f

        private fun vectorToGrid(x: Float, y: Float) : IntVec2
        {
            return IntVec2(
                    (x.toInt()+16) / 32, // Column
                    4 - (y.toInt()+16) / 32) // Row
        }

        private fun gridToVector(row: Int, col: Int) : Vector2
        {
            return Vector2(
                    col*32f,
                    (4 - row)*32f
            )
        }

        private fun gridToSphere(row: Int, col: Int, page: Int) : Int
        {
            return (page*35 + row*7 + col)
        }
    }

    class GestureListener
    (
            private val choiceWidget: GuardoSphereChoiceWidget,
            private var sphereSlot: Int,
            private val target: Button,
            private val page: Int
    )
        : ActorGestureListener()
    {
        private var startPosition = IntVec2(0,0)

        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int)
        {
            super.touchDown(event, x, y, pointer, button)

            startPosition = vectorToGrid(target.x, target.y)    // remember initial position on grid
            choiceWidget.callback.invoke(sphereSlot)            // show guardian details
        }

        override fun pan(event: InputEvent?, x: Float, y: Float, deltaX: Float, deltaY: Float)
        {
            target.x += deltaX
            target.y += deltaY
        }

        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int)
        {
            super.touchUp(event, x, y, pointer, button)

            val droppedAtCell = vectorToGrid(target.x, target.y)

            // Check, if Guardian was dropped inside the grid
            if(droppedAtCell.y in (0..4) && droppedAtCell.x in (0..6))
            {
                choiceWidget.swapButtonsOnGrid(startPosition, droppedAtCell, page, page)
            }
            else
            {
                val startVector = gridToVector(startPosition.y, startPosition.x)
                target.setPosition(startVector.x, startVector.y)
            }
        }
    }
}
