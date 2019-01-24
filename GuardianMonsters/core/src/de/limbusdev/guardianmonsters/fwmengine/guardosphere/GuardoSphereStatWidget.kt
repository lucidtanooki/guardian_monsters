package de.limbusdev.guardianmonsters.fwmengine.guardosphere

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ArrayMap
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.extensions.set
import ktx.actors.plus
import ktx.actors.txt

class GuardoSphereStatWidget(private val skin: Skin) : Group()
{
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Properties
    // .................................................... private
    private val valueLabels: ArrayMap<String, Label>
    private val elementGroup: HorizontalGroup
    private val equipmentGroup: Group

    // .................................................... static
    companion object
    {
        private const val WIDTH = 152f
        private const val HEIGHT = 180f
    }

    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Initializer
    init
    {
        setSize(WIDTH, HEIGHT)
        val background = Image(skin.getDrawable("guardosphere-frame"))
        background.setSize(WIDTH, HEIGHT)
        background.setPosition(0f, 0f, Align.bottomLeft)
        this+background

        val offX = 16f
        val offY = 200f - 16f
        val gap = 18f

        valueLabels = ArrayMap()
        var value: Label
        val labels = arrayOf("hp", "mp", "exp", "pstr", "pdef", "mstr", "mdef", "speed")
        var key: Image

        for (i in labels.indices)
        {
            key = Image(skin.getDrawable("stats-symbol-${labels[i]}"))
            key.setSize(16f, 16f)
            key.setPosition(offX, offY - gap * (i + 1), Align.topLeft)
            this+key

            value = Label("0", skin, "white")
            value.setPosition(offX + 20f, offY - gap * (i + 1), Align.topLeft)
            this+value
            valueLabels[labels[i]] = value
        }

        for (bg in 0..3)
        {
            val bgl = Label("", skin, "sphere")
            bgl.setSize(36f, 36f)
            bgl.setPosition(100f, offY - 14 - bg * 38, Align.topLeft)
            this+bgl
        }

        elementGroup = HorizontalGroup()
        elementGroup.setSize(140f, 20f)
        elementGroup.setPosition(6f, 6f, Align.bottomLeft)
        equipmentGroup = Group()

        // Add actors to this group
        this+elementGroup
        this+equipmentGroup
    }


    fun initialize(m: AGuardian)
    {
        val species = GuardiansServiceLocator.getSpecies()
        valueLabels["hp"].txt = "${m.individualStatistics.hp} / ${m.individualStatistics.hPmax}"
        valueLabels["mp"].txt = "${m.individualStatistics.mp} / ${m.individualStatistics.mPmax}"
        valueLabels["exp"].txt = "${m.individualStatistics.exp} / ${m.individualStatistics.exPtoNextLevel + m.individualStatistics.exp}"
        valueLabels["pstr"].txt = "${m.individualStatistics.pStrMax}"
        valueLabels["pdef"].txt = "${m.individualStatistics.pDefMax}"
        valueLabels["mstr"].txt = "${m.individualStatistics.mStrMax}"
        valueLabels["mdef"].txt = "${m.individualStatistics.mDefMax}"
        valueLabels["speed"].txt = "${m.individualStatistics.speedMax}"

        elementGroup.clear()
        for(e in m.speciesDescription.getElements(0))
        { // TODO currentForm
            val elem = e.toString().toLowerCase()
            var elemName = Services.getL18N().Elements().get("element_$elem")

            elemName =
                    if (elemName.length < 7) elemName
                    else elemName.substring(0, 6)

            val l = Label(elemName, skin, "elem-$elem")
            elementGroup+l
        }

        equipmentGroup.clear()
        if (m.individualStatistics.hasHeadEquipped())
        {
            val img = Image(skin.getDrawable(m.individualStatistics.head.name))
            img.setSize(32f, 32f)
            img.setPosition(102f, (178 - 2).toFloat(), Align.topLeft)
            equipmentGroup+img
        }
        if (m.individualStatistics.hasHandsEquipped())
        {
            val img = Image(skin.getDrawable(m.individualStatistics.hands.name))
            img.setSize(32f, 32f)
            img.setPosition(102f, (178 - 2 - 38).toFloat(), Align.topLeft)
            equipmentGroup+img
        }
        if (m.individualStatistics.hasBodyEquipped())
        {
            val img = Image(skin.getDrawable(m.individualStatistics.body.name))
            img.setSize(32f, 32f)
            img.setPosition(102f, (178 - 2 - 38 * 2).toFloat(), Align.topLeft)
            equipmentGroup+img
        }
        if (m.individualStatistics.hasFeetEquipped())
        {
            val img = Image(skin.getDrawable(m.individualStatistics.feet.name))
            img.setSize(32f, 32f)
            img.setPosition(102f, (178 - 2 - 38 * 3).toFloat(), Align.topLeft)
            equipmentGroup+img
        }
    }
}