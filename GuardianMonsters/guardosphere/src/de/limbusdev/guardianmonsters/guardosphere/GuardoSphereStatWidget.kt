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
import ktx.actors.plusAssign
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
        val background = Image(skin,"guardosphere-frame")
        background.setSize(WIDTH, HEIGHT)
        background.setPosition(0f, 0f, Align.bottomLeft)
        this+=background


        // ................................................ stat labels
        var value: Label
        var key: Image

        val labels = arrayOf("hp", "mp", "exp", "pstr", "pdef", "mstr", "mdef", "speed")

        for (i in labels.indices)
        {
            val yAnchor = HEIGHT - PADDING - i*(STAT_IMG_SIZE + STAT_PADDING)
            key = Image(skin,"stats-symbol-${labels[i]}")
            key.setSize(16f, 16f)
            key.setPosition(PADDING, yAnchor, Align.topLeft)
            this+=key

            value = Label("0", skin, "white")
            value.setPosition(PADDING + 20f, yAnchor - 1, Align.topLeft)
            value.width = WIDTH / 2
            this+=value
            valueLabels[labels[i]] = value
        }


        // ................................................ equipment images
        for (bg in 0..3)
        {
            val bgl = Label("", skin, "sphere")
            bgl.setSize(36f, 36f)
            bgl.setPosition(WIDTH - PADDING, HEIGHT - PADDING - bg*(37f), Align.topRight)
            this+=bgl
        }

        elementGroup.setSize(WIDTH-PADDING*2, 22f)
        elementGroup.setPosition(PADDING, PADDING, Align.bottomLeft)

        // Add actors to this group
        this+=elementGroup
        this+=equipmentGroup
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Public methods
    fun initialize(m: AGuardian?)
    {
        if(m == null) reset()
        else updateDetails(m)
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Private methods
    private fun reset()
    {
        for(l in valueLabels.values()) l.txt = "0"
        elementGroup.clear()
        equipmentGroup.clear()
    }

    private fun updateDetails(m: AGuardian)
    {
        val stats = m.individualStatistics
        val descr = m.speciesDescription


        // ................................................ update stats
        valueLabels["hp"].txt    = "${stats.hp}  / ${stats.hpMax}"
        valueLabels["mp"].txt    = "${stats.mp}  / ${stats.mPmax}"
        valueLabels["exp"].txt   = "${stats.exp} / ${stats.expToNextLevel + stats.exp}"
        valueLabels["pstr"].txt  = "${stats.pStrMax}"
        valueLabels["pdef"].txt  = "${stats.pDefMax}"
        valueLabels["mstr"].txt  = "${stats.mStrMax}"
        valueLabels["mdef"].txt  = "${stats.mDefMax}"
        valueLabels["speed"].txt = "${stats.speedMax}"


        // ................................................ update elements
        elementGroup.clear()

        for(e in descr.getElements(m.abilityGraph.currentForm))
        {
            val elem = e.toString().toLowerCase()
            var elemName = Services.getL18N().Elements().get("element_$elem")

            elemName =
                    if(elemName.length < 7) elemName
                    else elemName.substring(0, 6)

            val l = Label(elemName, skin, "elem-$elem")
            elementGroup += l
        }


        // ................................................ update equipment
        equipmentGroup.clear()

        val equipmentNames = arrayOf(
                stats.head?.name,
                stats.hands?.name,
                stats.body?.name,
                stats.feet?.name)

        for((i, name) in equipmentNames.withIndex())
        {
            if(name != null)
            {
                val img = Image(skin, name)
                img.setSize(32f, 32f)
                img.setPosition(WIDTH - PADDING - 2f, HEIGHT - PADDING - 2 - i*37, Align.topRight)
                equipmentGroup += img
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