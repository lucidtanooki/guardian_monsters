package de.limbusdev.guardianmonsters.guardosphere

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ArrayMap
import de.limbusdev.guardianmonsters.guardians.monsters.GuardoSphere
import de.limbusdev.guardianmonsters.guardians.monsters.Team
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
        private val team: Team,
        private val buttonGroup: ButtonGroup<Button>
)
    : Group()
{
    private val table: Table
    private val buttonGrid = ArrayMap<Int, ArrayMap<Int, Button>>() // 7 x 5 grid
    private val teamButtonBar = ArrayMap<Int, Button>()

    var sphereCallback: (Int) -> Unit
    var teamCallback: (Int) -> Unit

    init
    {
        // Create 7 x 5 grid for storing buttons
        for(row in 0..4)
        {
            buttonGrid[row] = ArrayMap()
        }

        // Create dummy callback
        sphereCallback = {println("$TAG: dummy sphere callback")}
        teamCallback = {println("$TAG: dummy team callback")}

        // Setup Layout
        setSize(WIDTH, HEIGHT)

        val background = Image(skin.getDrawable("guardosphere-frame"))
        background.setSize(WIDTH, 180f)
        background.setPosition(0f, HEIGHT, Align.topLeft)

        val backgroundTeam = Image(skin.getDrawable("guardosphere-frame"))
        backgroundTeam.setSize(WIDTH, 32f+16f)
        backgroundTeam.setPosition(0f,0f, Align.bottomLeft)


        table = table {

            setSize(7*32f, 5*32f + 16f + 32f)
            setPosition(14f, 10f)
            align(Align.bottomLeft)
        }

        // Setup Hierarchy
        this+background
        this+backgroundTeam
        this+table

        refresh(0)
    }

    private fun refresh(page: Int)
    {
        for(row in buttonGrid.values())
        {
            for(cell in row.values())
            {
                table-cell
                buttonGroup.remove(cell)
            }
        }

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

                monsterButton.addListener(GestureListener(this, key, monsterButton, page, IntVec2(col, row)))

                key++
            }
        }

        table.row().height(16f)
        table.add(Actor()).height(16f)

        // Team entries
        table.row()
        for(col in 0..6)
        {
            if(col < team.size)
            {
                val guardian = team[col]
                val guardianButton = GuardoSphereButton(skin, guardian)
                teamButtonBar[col] = guardianButton
                buttonGroup.add(guardianButton)
                table.add(guardianButton).size(32f, 32f)
                guardianButton.addListener(GestureListener(this, col, guardianButton, -1, IntVec2(col, 0), true))
            }
        }
    }

    fun swapButtonsOnGrid(cellA: IntVec2, cellB: IntVec2, pageA: Int, pageB: Int) : Boolean
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
        val slotA = gridToSphere(cellA.y, cellA.x, pageA)
        val slotB = gridToSphere(cellB.y, cellB.x, pageB)

        sphere.swap(slotA, slotB)

        // Assign new listeners
        buttonA.clearListeners()
        buttonA.addListener(GestureListener(this, slotB, buttonA, pageB, cellB))
        buttonB.clearListeners()
        buttonB.addListener(GestureListener(this, slotA, buttonB, pageA, cellA))

        return true
    }

    /**
     * Swaps the buttons and Guardians of the given slots, if possible.
     * @return whether swap was successful
     */
    fun swapSphereWithTeam(gridCell: IntVec2, teamSlot: Int) : Boolean
    {
        val sphereSlot = gridToSphere(gridCell.y, gridCell.x,0)
        val swapValid = GuardoSphere.isSwapValid(sphere, sphereSlot, team, teamSlot)
        val moveToTeamValid = GuardoSphere.isSphereToTeamMoveValid(sphere, sphereSlot, team)

        if(!swapValid && !moveToTeamValid) return false
        if(teamSlot >= team.size) return GuardoSphere.fromSphereToTeam(sphere, sphereSlot, team)

        // Get buttons
        val gridButton = buttonGrid[gridCell.y][gridCell.x]
        val teamButton = teamButtonBar[teamSlot]

        // Set new grid entries
        buttonGrid[gridCell.y][gridCell.x] = teamButton
        teamButtonBar[teamSlot] = gridButton

        // Move buttons to new positions
        val newPositionGrid = gridToVector(5, teamSlot)
        val newPositionTeam = gridToVector(gridCell.y, gridCell.x)
        gridButton.setPosition(newPositionGrid.x, newPositionGrid.y)
        teamButton.setPosition(newPositionTeam.x, newPositionTeam.y)

        // Swap guardians
        GuardoSphere.teamSphereSwap(sphere, sphereSlot, team, teamSlot)

        // Assign new listeners
        gridButton.clearListeners()
        gridButton.addListener(GestureListener(this, teamSlot, gridButton, -1, IntVec2(teamSlot, 0), true))
        teamButton.clearListeners()
        teamButton.addListener(GestureListener(this, sphereSlot, teamButton, 0, gridCell, false))

        return true
    }

    fun swapInTeam(teamSlotA: Int, teamSlotB: Int) : Boolean
    {
        // Get buttons
        val buttonA = teamButtonBar[teamSlotA]
        val buttonB = teamButtonBar[teamSlotB]

        // Set new grid entries
        teamButtonBar[teamSlotA] = buttonB
        teamButtonBar[teamSlotB] = buttonA

        // Move buttons to new positions
        val newPositionA = gridToVector(5, teamSlotB)
        val newPositionB = gridToVector(5, teamSlotA)
        buttonA.setPosition(newPositionA.x, newPositionA.y)
        buttonB.setPosition(newPositionB.x, newPositionB.y)

        // Swap guardians
        team.swap(teamSlotA, teamSlotB)

        // Assign new listeners
        buttonA.clearListeners()
        buttonA.addListener(GestureListener(this, teamSlotB, buttonA, -1, IntVec2(teamSlotB, 0), true))
        buttonB.clearListeners()
        buttonB.addListener(GestureListener(this, teamSlotA, buttonB, -1, IntVec2(teamSlotA, 0), true))

        return true
    }

    companion object
    {
        private const val TAG = "GuardoSphereChoiceWidget"
        private const val WIDTH = 252f
        private const val HEIGHT = 6*32f + 16f + 16f + 4f

        private fun vectorToGrid(x: Float, y: Float) : IntVec2 {

            val grid = IntVec2(
                    (x.toInt()+16) / 32,    // Column
                    5 - (y.toInt()) / 32    // Row
            )

            if(grid.y > 5) grid.y = 5

            return grid
        }

        private fun gridToVector(row: Int, col: Int) : Vector2 {

            var position : Vector2

            if(row < 5) position = Vector2(col * 32f, (5.5f - row) * 32f)
            else        position = Vector2(col * 32f, (5f - row) * 32f)

            return position
        }

        private fun gridToSphere(row: Int, col: Int, page: Int) : Int {

            return (page*35 + row*7 + col)
        }

        private fun isTeamArea(col: Int, row: Int) = (row == 5 && col in (0..6))

        private fun isSphereArea(col: Int, row: Int) = (row in (0..4) && col in (0..6))
    }

    class GestureListener
    (
            private val choiceWidget: GuardoSphereChoiceWidget,
            private var sphereSlot: Int,
            private val target: Button,
            private val page: Int,
            private val cell: IntVec2,
            private val isTeamButton: Boolean = false
    )
        : ActorGestureListener()
    {
        private var panValid = false

        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int)
        {
            target.isChecked = true

            // Check, if touched cell is movable:
            //  - not last team member
            //  - not empty slot

            val team = choiceWidget.team
            val sphere = choiceWidget.sphere

            panValid = (isTeamButton && team.size >= sphereSlot) ||
                       (!isTeamButton && sphere.isOccupied(sphereSlot))

            // show guardian details
            if(isTeamButton) choiceWidget.teamCallback.invoke(sphereSlot)
            else             choiceWidget.sphereCallback.invoke(sphereSlot)
        }

        override fun pan(event: InputEvent?, x: Float, y: Float, deltaX: Float, deltaY: Float)
        {
            if(!panValid) return
            target.x += deltaX
            target.y += deltaY
        }

        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int)
        {
            if(!panValid) return
            panValid = false



            val droppedAtCell = vectorToGrid(target.x, target.y)
            val droppedInTeam = isTeamArea(droppedAtCell.x, droppedAtCell.y)
            val droppedInSphere = isSphereArea(droppedAtCell.x, droppedAtCell.y)
            var dropValid = (droppedInSphere || droppedInTeam)

            // Sphere-Team-Swap
            if(droppedInSphere && isTeamButton)
                dropValid = choiceWidget.swapSphereWithTeam(droppedAtCell, sphereSlot)
            // Sphere-Sphere-Swap
            if(droppedInSphere && !isTeamButton)
                dropValid = choiceWidget.swapButtonsOnGrid(cell, droppedAtCell, page, page)
            // Team-Team-Swap
            if(droppedInTeam && isTeamButton)
                dropValid = choiceWidget.swapInTeam(cell.x, droppedAtCell.x)

            if(!dropValid)
            {
                val startVector = gridToVector(cell.y, cell.x)
                target.setPosition(startVector.x, startVector.y)
            }
        }
    }
}
