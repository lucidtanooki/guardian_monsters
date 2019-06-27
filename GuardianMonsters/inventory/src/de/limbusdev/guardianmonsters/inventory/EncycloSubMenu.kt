package de.limbusdev.guardianmonsters.inventory

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.monsters.SpeciesDescription
import de.limbusdev.guardianmonsters.media.IMediaManager
import de.limbusdev.guardianmonsters.scene2d.*
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener
import de.limbusdev.utils.extensions.f
import ktx.style.get

/**
 * EncycloSubMenu
 *
 * @author Georg Eckert 2017
 */

class EncycloSubMenu(skin: Skin) : AInventorySubMenu()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    private lateinit var guardianImgMeta0   : Image
    private lateinit var guardianImgMeta1   : Image
    private lateinit var guardianImgMeta2   : Image
    private lateinit var guardianImgMeta3   : Image
    private lateinit var metaFormButtonGroup: ButtonGroup<TextButton>
    private lateinit var metaFormButton0    : TextButton
    private lateinit var metaFormButton1    : TextButton
    private lateinit var metaFormButton2    : TextButton
    private lateinit var metaFormButton3    : TextButton
    private lateinit var next               : ImageButton
    private lateinit var previous           : ImageButton
    private var currentSpeciesID            : Int = 0

    private lateinit var name               : Label
    private lateinit var description        : Label


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        layout(skin)
        initialize(1)

        metaFormButton0.replaceOnButtonClick { guardianImgMeta0.isVisible = metaFormButton0.isChecked }
        metaFormButton1.replaceOnButtonClick { guardianImgMeta1.isVisible = metaFormButton1.isChecked }
        metaFormButton2.replaceOnButtonClick { guardianImgMeta2.isVisible = metaFormButton2.isChecked }
        metaFormButton3.replaceOnButtonClick { guardianImgMeta3.isVisible = metaFormButton3.isChecked }

        next.replaceOnButtonClick {

            try                  { initialize((currentSpeciesID + 1) % 300) }
            catch (e: Exception) { e.printStackTrace();  initialize(1) }
        }

        previous.replaceOnButtonClick {

            try                  { initialize((currentSpeciesID - 1) % 300) }
            catch (e: Exception) { e.printStackTrace(); initialize(1) }
        }

        setDebug(InventoryDebugger.SCENE2D_DEBUG, true)
    }

    override fun refresh() {}

    private fun initialize(speciesID: Int)
    {
        currentSpeciesID = speciesID

        val media = Services.Media()
        val desc = GuardiansServiceLocator.species.getSpeciesDescription(speciesID)
        val metaForms = desc.metaForms.size

        val nameBuilder = StringBuilder()

        for (i in 0 until metaForms)
        {
            if (i > 0) { nameBuilder.append(" > ") }
            nameBuilder.append(Services.I18N().getLocalizedGuardianName(speciesID, i))
        }
        name.setText(nameBuilder)

        description.setText(Services.I18N().getLocalizedGuardianDescription(speciesID))

        guardianImgMeta0.remove()
        guardianImgMeta1.remove()
        guardianImgMeta2.remove()
        guardianImgMeta3.remove()

        metaFormButton0.remove()
        metaFormButton1.remove()
        metaFormButton2.remove()
        metaFormButton3.remove()

        guardianImgMeta0.color = Color.GRAY
        guardianImgMeta1.color = Color.GRAY
        guardianImgMeta2.color = Color.GRAY
        guardianImgMeta3.color = Color.GRAY

        when (metaForms)
        {
            4 -> {
                guardianImgMeta3.drawable = TextureRegionDrawable(media.getMonsterSprite(speciesID, 3))
                guardianImgMeta2.drawable = TextureRegionDrawable(media.getMonsterSprite(speciesID, 2))
                guardianImgMeta1.drawable = TextureRegionDrawable(media.getMonsterSprite(speciesID, 1))
                guardianImgMeta0.drawable = TextureRegionDrawable(media.getMonsterSprite(speciesID, 0))
                guardianImgMeta3.setPosition(positions[3][3][0], positions[3][3][1], Align.bottom)
                guardianImgMeta2.setPosition(positions[3][2][0], positions[3][2][1], Align.bottom)
                guardianImgMeta1.setPosition(positions[3][1][0], positions[3][1][1], Align.bottom)
                guardianImgMeta0.setPosition(positions[3][0][0], positions[3][0][1], Align.bottom)
                addActor(guardianImgMeta3)
                addActor(guardianImgMeta2)
                addActor(guardianImgMeta1)
                addActor(guardianImgMeta0)
            }
            3 -> {
                guardianImgMeta2.drawable = TextureRegionDrawable(media.getMonsterSprite(speciesID, 2))
                guardianImgMeta1.drawable = TextureRegionDrawable(media.getMonsterSprite(speciesID, 1))
                guardianImgMeta0.drawable = TextureRegionDrawable(media.getMonsterSprite(speciesID, 0))
                guardianImgMeta2.setPosition(positions[2][2][0] + leftBorder, positions[2][2][1], Align.bottom)
                guardianImgMeta1.setPosition(positions[2][1][0] + leftBorder, positions[2][1][1], Align.bottom)
                guardianImgMeta0.setPosition(positions[2][0][0] + leftBorder, positions[2][0][1], Align.bottom)
                addActor(guardianImgMeta2)
                addActor(guardianImgMeta1)
                addActor(guardianImgMeta0)
            }
            2 -> {
                guardianImgMeta1.drawable = TextureRegionDrawable(media.getMonsterSprite(speciesID, 1))
                guardianImgMeta0.drawable = TextureRegionDrawable(media.getMonsterSprite(speciesID, 0))
                guardianImgMeta1.setPosition(positions[1][1][0] + leftBorder, positions[1][1][1], Align.bottom)
                guardianImgMeta0.setPosition(positions[1][0][0] + leftBorder, positions[1][0][1], Align.bottom)
                addActor(guardianImgMeta1)
                addActor(guardianImgMeta0)
            }
            else -> {
                guardianImgMeta0.drawable = TextureRegionDrawable(media.getMonsterSprite(speciesID, 0))
                guardianImgMeta0.setPosition(positions[2][1][0] + leftBorder, positions[2][1][1], Align.bottom)
                addActor(guardianImgMeta0)
            }
        }

        if(metaForms > 3)
        {
            addActor(metaFormButton3)
            metaFormButton3.isChecked = true
            guardianImgMeta3.isVisible = true
        }
        if(metaForms > 2)
        {
            addActor(metaFormButton2)
            metaFormButton2.isChecked = true
            guardianImgMeta2.isVisible = true
        }
        if(metaForms > 1)
        {
            addActor(metaFormButton1)
            addActor(metaFormButton0)
            metaFormButton1.isChecked = true
            metaFormButton0.isChecked = true
            guardianImgMeta1.isVisible = true
            guardianImgMeta0.isVisible = true
        }
    }

    override fun layout(skin: Skin)
    {
        // Creates a page based layout
        val bgImg = makeImage(

                drawable = skin["label-bg-paper"],
                layout   = ImgLayout(Constant.WIDTHf/2-4, Constant.HEIGHTf-4-35, Constant.WIDTHf-2, 2f, Align.bottomRight),
                parent   = this
        )

        guardianImgMeta0 = Image()
        guardianImgMeta0.setup(ImgLayout(128f, 128f, 2f, 16f), this)

        guardianImgMeta1 = Image()
        guardianImgMeta1.setup(ImgLayout(128f, 128f, 64+2f, 32f), this)

        guardianImgMeta2 = Image()
        guardianImgMeta2.setup(ImgLayout(128f, 128f, 96+2f, 48f), this)

        guardianImgMeta3 = Image()
        guardianImgMeta3.setup(ImgLayout(128f, 128f, 96+2f, 48f), this)

        metaFormButtonGroup = ButtonGroup()
        metaFormButton0 = TextButton("1", skin, "default-toggleable")
        metaFormButton1 = TextButton("2", skin, "default-toggleable")
        metaFormButton2 = TextButton("3", skin, "default-toggleable")
        metaFormButton3 = TextButton("4", skin, "default-toggleable")
        metaFormButton0.setSize(24f, 24f)
        metaFormButton1.setSize(24f, 24f)
        metaFormButton2.setSize(24f, 24f)
        metaFormButton3.setSize(24f, 24f)
        metaFormButton0.setPosition(Constant.WIDTHf / 2 + 8 + 28 * 0, 10, Align.bottomLeft)
        metaFormButton1.setPosition(Constant.WIDTHf / 2 + 8 + 28 * 1, 10, Align.bottomLeft)
        metaFormButton2.setPosition(Constant.WIDTHf / 2 + 8 + 28 * 2, 10, Align.bottomLeft)
        metaFormButton3.setPosition(Constant.WIDTHf / 2 + 8 + 28 * 3, 10, Align.bottomLeft)

        next     = makeImageButton(skin["button-next"], PositionXYA(Constant.WIDTHf-8, 10f, Align.bottomRight), this)
        previous = makeImageButton(skin["button-previous"], PositionXYA(Constant.WIDTHf-8-18, 10f, Align.bottomRight), this)

        name = makeLabel(skin["default"], "", LabelLayout2D(96f, 40f, Constant.WIDTHf/2+8, Constant.HEIGHTf-48, Align.left, Align.left, false), this)

        description = Label("", skin, "default")
        description.width = Constant.WIDTHf / 2 - 16
        description.setAlignment(Align.topLeft, Align.topLeft)
        description.setPosition(Constant.WIDTHf / 2 + 8, Constant.HEIGHT - 64)
        description.setWrap(true)
        addActor(description)
    }

    companion object
    {
        private const val leftBorder = 64 + 2
        private const val possibleArea = Constant.WIDTH / 2 - 128
        private const val row = 24
        private val positions = arrayOf(

                /* 1 meta form  */ arrayOf(intArrayOf(possibleArea / 2, row * 0 + 2)),
                /* 2 meta forms */ arrayOf(intArrayOf(possibleArea * 0, row * 0 + 2), intArrayOf(possibleArea * 1, row * 1 + 2)),
                /* 3 meta forms */ arrayOf(intArrayOf(possibleArea * 0, row * 0 + 2), intArrayOf(possibleArea / 2, row * 1 + 2), intArrayOf(possibleArea * 1, row * 2 + 2)),
                /* 4 meta forms */ arrayOf(intArrayOf(possibleArea * 0, row * 0 + 2), intArrayOf(possibleArea / 3, row * 1 + 2), intArrayOf(possibleArea / 3 * 2, row * 2 + 2), intArrayOf(possibleArea * 1, row * 3 + 2))
        )
    }
}
