package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup
import com.badlogic.gdx.utils.Align

import java.util.Observable
import java.util.Observer

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian
import de.limbusdev.guardianmonsters.guardians.monsters.ISpeciesDescriptionService
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.utils.extensions.f

/**
 * Widget for displaying monster status in battle: HP, MP, EXP, Name, Level
 * HINT: Don't forget calling the init() method
 *
 * @author Georg Eckert 2016
 */
class MonsterStateWidget
/**
 *
 * @param showExp  whether exp bar shall be shown
 */
(
        showExp: Boolean
)
    : WidgetGroup(), Observer
{
    // .................................................................................. Properties
    private val hpBar       : ProgressBar
    private val mpBar       : ProgressBar
    private val epBar       : ProgressBar
    private val nameLabel   : Label
    private val levelLabel  : Label
    private val hudBgImg    : Image
    private val hudRingImg  : Image
    private val hudNameImg  : Image

    private val skin = Services.getUI().battleSkin


    // ................................................................................ Constructors
    init
    {
        hudBgImg = Image(skin.getDrawable("monStateUIbg2"))
        hudBgImg.setPosition(0f, 0f, Align.bottomLeft)

        hudRingImg = Image(skin.getDrawable("monStateWidgetRing"))
        hudRingImg.setPosition(Constant.COL * 12f, 0f, Align.bottomLeft)

        hudNameImg = Image(skin.getDrawable("monStateUIname"))
        hudNameImg.setPosition(0f, 0f, Align.bottomLeft)

        val ls = Label.LabelStyle()
        ls.background = skin.getDrawable("invis")
        ls.font = skin.getFont("font16")
        ls.fontColor = Color.WHITE

        nameLabel = Label("Guardian", ls)
        nameLabel.width = 96f
        nameLabel.height = 24f
        nameLabel.setPosition(24f, 2f)

        hpBar = ProgressBar(0f, 100f, 1f, false, skin, "hp")
        mpBar = ProgressBar(0f, 100f, 1f, false, skin, "mp")
        epBar = ProgressBar(0f, 100f, 1f, false, skin, "ep")

        hpBar.setPosition(120f, 13f, Align.bottomLeft)
        hpBar.setSize(96f, 9f)
        hpBar.value = 0f
        mpBar.setPosition(120f, 5f, Align.bottomLeft)
        mpBar.setSize(92f, 11f)
        mpBar.value = 0f
        epBar.setPosition(4f, 2f, Align.bottomLeft)
        epBar.setSize(100f, 4f)
        epBar.value = 0f

        hpBar.setAnimateInterpolation(Interpolation.linear)
        hpBar.setAnimateDuration(1f)
        mpBar.setAnimateInterpolation(Interpolation.linear)
        mpBar.setAnimateDuration(.5f)
        epBar.setAnimateInterpolation(Interpolation.linear)
        epBar.setAnimateDuration(.1f)

        ls.font = skin.getFont("font16w")
        levelLabel = Label("0", ls)
        levelLabel.setPosition(110f, 13f, Align.center)

        // Sorting
        if (showExp) { this.addActor(epBar) }
        this.addActor(hudBgImg)
        this.addActor(hudNameImg)
        this.addActor(hpBar)
        this.addActor(mpBar)
        this.addActor(hudRingImg)
        this.addActor(nameLabel)
        this.addActor(levelLabel)

        this.setBounds(0f, 0f, 220f, 32f)
    }


    // .............................................................................. Initialization
    /**
     * Initializes the widget to show a monsters status values
     * @param guardian
     */
    fun initialize(guardian: AGuardian)
    {
        val species = GuardiansServiceLocator.species
        refresh(guardian)
        val speciesID = guardian.speciesID
        val metaForm = guardian.abilityGraph.currentForm
        val name = species.getCommonNameById(speciesID, metaForm)
        nameLabel.setText(Services.getL18N().Guardians().get(name))
        guardian.addObserver(this)
    }

    fun refresh(guardian: AGuardian)
    {
        val statistics = guardian.individualStatistics
        this.hpBar.value = statistics.hpFraction.f()
        this.mpBar.value = statistics.mpFraction.f()
        this.epBar.value = statistics.expFraction.f()
        this.levelLabel.setText(statistics.level.toString())

        if (statistics.isKO)
        {
            //addAction(Actions.sequence(Actions.alpha(0, 2), Actions.visible(false)));
            for (actor in children)
            {
                actor.addAction(Actions.color(Color.GRAY, 2f))
            }
        }
        else
        {
            for (actor in children)
            {
                actor.addAction(Actions.color(Color.WHITE, 2f))
            }
        }
    }


    // ..................................................................................... Methods
    override fun act(delta: Float)
    {
        super.act(delta)
        hpBar.act(delta)
        mpBar.act(delta)
        epBar.act(delta)
        nameLabel.act(delta)
        levelLabel.act(delta)
        hudBgImg.act(delta)
        hudRingImg.act(delta)
        hudNameImg.act(delta)
    }

    override fun update(o: Observable, arg: Any?)
    {
        if (o is AGuardian) { refresh(o) }
    }
}
