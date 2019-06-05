/**
 * Copyright (C) 2019 Georg Eckert - All Rights Reserved
 */

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
import ktx.actors.plus
import ktx.log.info
import ktx.scene2d.table
import kotlin.Exception

/**
 * GuardoSphereChoiceWidget
 *
 * @author Georg Eckert 2017
 */
class GuardoSphereChoiceWidget
(
        private val skin: Skin,
        private val sphere: GuardoSphere,
        private val team: Team
)
    : Group()
{
    private val slotButtonTable : Table // holds the slot buttons
    private val slotImageTable  : Table // holds the slot background images
    private val buttonGroup   = ButtonGroup<Button>()
    private val buttonGrid    = ArrayMap<Int, ArrayMap<Int, Button>>() // 7 x 5 grid
    private val teamButtonBar = ArrayMap<Int, Button>()

    private var activeSlot = 0
    private var activeArea = Area.GUARDOSPHERE
    var currentPage = 0
        private set

    var sphereCallback: (Int) -> Unit
    var teamCallback:   (Int) -> Unit

    init
    {
        buttonGroup.setMaxCheckCount(1)
        buttonGroup.setMinCheckCount(0)

        // Create dummy callback
        sphereCallback = { println("$TAG: dummy sphere callback") }
        teamCallback   = { println("$TAG: dummy team callback")   }

        // Setup Layout
        setSize(WIDTH, HEIGHT)

        val background = Image(skin.getDrawable("guardosphere-frame"))
        background.setSize(WIDTH, 180f)
        background.setPosition(0f, HEIGHT, Align.topLeft)

        val backgroundTeam = Image(skin.getDrawable("guardosphere-frame"))
        backgroundTeam.setSize(WIDTH, 32f+16f)
        backgroundTeam.setPosition(0f,0f, Align.bottomLeft)

        slotButtonTable = table {

            setSize(7*32f, 5*32f + 16f + 32f)
            setPosition(14f, 10f)
            align(Align.bottomLeft)
        }

        slotImageTable = table {

            setSize(7*32f, 5*32f + 16f + 32f)
            setPosition(14f, 10f)
            align(Align.bottomLeft)
        }

        // Setup Hierarchy
        this+background
        this+backgroundTeam
        this+slotImageTable
        this+slotButtonTable

        refresh(0)
    }

    fun refresh(page: Int = currentPage)
    {
        currentPage = page

        slotImageTable.clear()
        slotButtonTable.clear()
        buttonGroup.clear()
        buttonGrid.clear()

        // Create 7 x 5 grid for storing buttons
        for(row in 0..4) buttonGrid[row] = ArrayMap()


        // Fill grid with buttons
        var slot = page*35

        for(row in 0..4)
        {
            slotImageTable.row()
            slotButtonTable.row()

            for(col in 0..6)
            {
                if(slot < GuardoSphere.capacity)
                {
                    val guardian = sphere[slot]
                    val slotButton = GuardoSphereButton(skin, guardian)

                    val slotImage = ImageButton(skin, if(guardian == null) "button-gs-empty" else "button-gs")
                    slotImage.isDisabled = true

                    slotImageTable.add(slotImage).width(32f).height(32f)
                    slotButtonTable.add(slotButton).width(32f).height(32f)
                    buttonGroup.add(slotButton)
                    buttonGrid[row][col] = slotButton

                    slotButton.addListener(GestureListener(this, slot, slotButton, page, IntVec2(col, row), Area.GUARDOSPHERE))

                    if(slot == activeSlot && activeArea == Area.GUARDOSPHERE) slotButton.isChecked = true

                    slot++
                }
            }
        }

        // Add button row for team
        slotImageTable.row()
        slotImageTable.add(Actor()).height(16f)
        slotButtonTable.row()
        slotButtonTable.add(Actor()).height(16f)

        // Team entries
        slotImageTable.row()
        slotButtonTable.row()
        for(col in 0..6)
        {
            if(col < team.maximumTeamSize)
            {
                val slotImage = ImageButton(skin, if(col < team.size) "button-gs" else "button-gs-empty")
                slotImage.isDisabled = true
                slotImageTable.add(slotImage).size(32f, 32f)
            }

            if(col < team.size)
            {
                val guardian = team[col]
                val teamSlotButton = GuardoSphereButton(skin, guardian)

                slotButtonTable.add(teamSlotButton).size(32f, 32f)
                buttonGroup.add(teamSlotButton)
                teamButtonBar[col] = teamSlotButton

                teamSlotButton.addListener(GestureListener(this, col, teamSlotButton, -1, IntVec2(col, 0), Area.TEAM))

                if(col == activeSlot && activeArea == Area.TEAM) teamSlotButton.isChecked = true
            }
        }
    }

    fun swapButtonsOnGrid(cellA: IntVec2, cellB: IntVec2, pageA: Int, pageB: Int)
    {
        // Swap guardians
        val slotA = gridToSphere(cellA, pageA)
        val slotB = gridToSphere(cellB, pageB)

        sphere.swap(slotA, slotB)
    }

    /**
     * Swaps the buttons and Guardians of the given slots, if possible.
     * @return whether swap was successful
     */
    fun swapSphereWithTeam(gridCell: IntVec2, teamSlot: Int)
    {
        val sphereSlot = gridToSphere(gridCell, currentPage)
        GuardoSphere.teamSphereSwap(sphere, sphereSlot, team, teamSlot)
    }

    companion object
    {
        private const val TAG = "GuardoSphereChoiceWidget"
        private const val WIDTH = 252f
        private const val HEIGHT = 6*32f + 16f + 16f + 4f

        private enum class Area { TEAM, GUARDOSPHERE, ILLEGAL }

        private fun vectorToGrid(x: Float, y: Float) : IntVec2
        {
            val grid = IntVec2(
                    (x.toInt()+16) / 32,    // Column
                    5 - (y.toInt()) / 32    // Row
            )

            if(grid.y > 5) grid.y = 5

            return grid
        }

        private fun gridToVector(cell: IntVec2) : Vector2
        {
            if(cell.y < 5) return Vector2(cell.x * 32f, (5.5f - cell.y) * 32f)
            else           return Vector2(cell.x * 32f, (5.0f - cell.y) * 32f)
        }

        private fun gridToSphere(cell: IntVec2, page: Int) : Int
        {
            return (page*35 + cell.y*7 + cell.x)
        }

        private fun getArea(cell: IntVec2) : Area
        {
            if(cell.y == 5 && cell.x in (0..6))      return Area.TEAM
            if(cell.y in (0..4) && cell.x in (0..6)) return Area.GUARDOSPHERE
            return                                   Area.ILLEGAL
        }
    }

    private class GestureListener
    (
            private val choiceWidget: GuardoSphereChoiceWidget,
            private var slot: Int,
            private val target: Button,
            private val page: Int,
            private val cell: IntVec2,
            private val area: Area
    )
        : ActorGestureListener()
    {
        override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int)
        {
            target.isChecked = true

            val team   = choiceWidget.team
            val sphere = choiceWidget.sphere

            // show guardian details
            when(area)
            {
                Area.TEAM         -> choiceWidget.teamCallback.invoke(slot)
                Area.GUARDOSPHERE -> choiceWidget.sphereCallback.invoke(slot)
                Area.ILLEGAL      -> {}// Do Nothing
            }
        }

        override fun pan(event: InputEvent?, x: Float, y: Float, deltaX: Float, deltaY: Float)
        {
            target.x += deltaX
            target.y += deltaY
        }

        override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int)
        {
            val dropCell = vectorToGrid(target.x, target.y)
            val dropArea = getArea(dropCell)
            var dropValid = true

            val team = choiceWidget.team
            val sphere = choiceWidget.sphere

            val dropSlot = when(dropArea)
            {
                Area.TEAM         -> dropCell.x
                Area.GUARDOSPHERE -> gridToSphere(dropCell, choiceWidget.currentPage)
                Area.ILLEGAL      -> -1
            }

            // Illegal Swap
            if(area == Area.ILLEGAL || dropArea == Area.ILLEGAL)
            {
                dropValid = false
            }

            // Team-Team-Swap
            if(area == Area.TEAM && dropArea == Area.TEAM)
            {
                try                 { team.swap(slot, dropSlot) }
                catch(e: Exception) { info(TAG) {"${e.message}"}; dropValid = false }
            }

            // Sphere-Sphere-Swap
            if(area == Area.GUARDOSPHERE && dropArea == Area.GUARDOSPHERE)
            {
                try                 { choiceWidget.swapButtonsOnGrid(cell, dropCell, page, page) }
                catch(e: Exception) { info(TAG) {"${e.message}"}; dropValid = false }
            }

            // Sphere-Team-Swap
            if((area == Area.TEAM && dropArea == Area.GUARDOSPHERE) || (area == Area.GUARDOSPHERE && dropArea == Area.TEAM))
            {
                try
                {
                    val teamSlot   = if(area == Area.TEAM)         slot else dropSlot
                    val sphereSlot = if(area == Area.GUARDOSPHERE) slot else dropSlot

                    when
                    {
                        (teamSlot < team.size) ->
                            GuardoSphere.teamSphereSwap(sphere, sphereSlot, team, teamSlot)
                        (teamSlot >= team.size && team.size < team.maximumTeamSize) ->
                            GuardoSphere.fromSphereToTeam(sphere, sphereSlot, team)
                        else ->
                            dropValid = false
                    }
                }
                catch(e: Exception) { info(TAG) {"${e.message}"}; dropValid = false }
            }

            if(dropValid)
            {
                choiceWidget.activeSlot = dropSlot
                choiceWidget.activeArea = dropArea
            }

            if(page >= 0) choiceWidget.refresh(page)
            else choiceWidget.refresh()
        }
    }
}
