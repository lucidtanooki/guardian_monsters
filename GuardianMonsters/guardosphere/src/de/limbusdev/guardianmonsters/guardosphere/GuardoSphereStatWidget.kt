package de.limbusdev.guardianmonsters.guardosphere

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.ArrayMap
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.scene2d.lSetSize
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.extensions.set
import ktx.actors.plus
import ktx.actors.txt

class GuardoSphereStatWidget(private val skin: Skin) : Group()
{
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Properties
    // .................................................... private
    private val valueLabels = ArrayMap<String, Label>()
    private val elementGroup = HorizontalGroup()
    private val equipmentGroup = Group()


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Initializer
    init
    {
        // ................................................ background
        setSize(WIDTH, HEIGHT)
        val background = Image(skin.getDrawable("guardosphere-frame"))
        background.setSize(WIDTH, HEIGHT)
        background.setPosition(0f, 0f, Align.bottomLeft)
        this+background


        // ................................................ stat labels
        var value: Label
        var key: Image

        val labels = arrayOf("hp", "mp", "exp", "pstr", "pdef", "mstr", "mdef", "speed")

        for (i in labels.indices)
        {
            val yAnchor = HEIGHT - PADDING - i*(STAT_IMG_SIZE + STAT_PADDING)
            key = Image(skin.getDrawable("stats-symbol-${labels[i]}"))
            key.setSize(16f, 16f)
            key.setPosition(PADDING, yAnchor, Align.topLeft)
            this+key

            value = Label("0", skin, "white")
            value.setPosition(PADDING + 20f, yAnchor - 1, Align.topLeft)
            value.width = WIDTH / 2
            this+value
            valueLabels[labels[i]] = value
        }


        // ................................................ equipment images
        for (bg in 0..3)
        {
            val bgl = Label("", skin, "sphere")
            bgl.setSize(36f, 36f)
            bgl.setPosition(WIDTH - PADDING, HEIGHT - PADDING - bg*(37f), Align.topRight)
            this+bgl
        }

        elementGroup.setSize(WIDTH-PADDING*2, 22f)
        elementGroup.setPosition(PADDING, PADDING, Align.bottomLeft)

        // Add actors to this group
        this+elementGroup
        this+equipmentGroup
    }


    fun initialize(m: AGuardian?)
    {
        if(m != null)
        {
            val species = GuardiansServiceLocator.species
            valueLabels["hp"].txt = "${m.individualStatistics.hp} / ${m.individualStatistics.hpMax}"
            valueLabels["mp"].txt = "${m.individualStatistics.mp} / ${m.individualStatistics.mPmax}"
            valueLabels["exp"].txt = "${m.individualStatistics.exp} / ${m.individualStatistics.expToNextLevel + m.individualStatistics.exp}"
            valueLabels["pstr"].txt = "${m.individualStatistics.pStrMax}"
            valueLabels["pdef"].txt = "${m.individualStatistics.pDefMax}"
            valueLabels["mstr"].txt = "${m.individualStatistics.mStrMax}"
            valueLabels["mdef"].txt = "${m.individualStatistics.mDefMax}"
            valueLabels["speed"].txt = "${m.individualStatistics.speedMax}"

            elementGroup.clear()

            for(e in m.speciesDescription.getElements(0)) { // TODO currentForm
                val elem = e.toString().toLowerCase()
                var elemName = Services.getL18N().Elements().get("element_$elem")

                elemName =
                        if(elemName.length < 7) elemName
                        else elemName.substring(0, 6)

                val l = Label(elemName, skin, "elem-$elem")
                elementGroup + l
            }


            equipmentGroup.clear()

            var name: String?

            name = m.individualStatistics.head?.name
            if(name != null) {
                val img = Image(skin.getDrawable(name))
                img.setSize(32f, 32f)
                img.setPosition(102f, (178 - 2).toFloat(), Align.topLeft)
                equipmentGroup + img
            }

            name = m.individualStatistics.hands?.name
            if(name != null) {
                val img = Image(skin.getDrawable(name))
                img.setSize(32f, 32f)
                img.setPosition(102f, (178 - 2 - 38).toFloat(), Align.topLeft)
                equipmentGroup + img
            }

            name = m.individualStatistics.body?.name
            if(name != null) {
                val img = Image(skin.getDrawable(name))
                img.setSize(32f, 32f)
                img.setPosition(102f, (178 - 2 - 38 * 2).toFloat(), Align.topLeft)
                equipmentGroup + img
            }

            name = m.individualStatistics.feet?.name
            if(name != null) {
                val img = Image(skin.getDrawable(name))
                img.setSize(32f, 32f)
                img.setPosition(102f, (178 - 2 - 38 * 3).toFloat(), Align.topLeft)
                equipmentGroup + img
            }
        }
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Companion
    companion object
    {
        private const val TAG = "GuardoSphereStatWidget"
        private const val WIDTH = 152f
        private const val HEIGHT = 180f
        private const val PADDING = 6f
        private const val STAT_PADDING = 2f
        private const val STAT_IMG_SIZE = 16f
    }
}