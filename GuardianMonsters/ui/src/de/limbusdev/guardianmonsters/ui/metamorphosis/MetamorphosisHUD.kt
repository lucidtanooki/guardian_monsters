package de.limbusdev.guardianmonsters.ui.metamorphosis

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.utils.Align
import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.scene2d.AnimatedImage
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.AHUD
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.guardianmonsters.ui.widgets.ParticleEffectActor
import ktx.actors.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.run as runAction

/**
 * MetamorphosisHUD
 *
 * @author Georg Eckert 2017
 */

class MetamorphosisHUD
(
        skin: Skin,
        speciesID: Int,
        formerMetaForm: Int,
        newMetaForm: Int
)
    : AHUD(skin)
{
    private val animation: AnimatedImage
    private val imgBefore: Image
    private val imgAfter: Image
    private val okButton: Button
    private val label: Label

    init
    {
        val bundle = Services.getL18N().General()
        val media = Services.getMedia()

        val monsterNames = arrayOf(

                Services.getL18N().getLocalizedGuardianName(speciesID, formerMetaForm),
                Services.getL18N().getLocalizedGuardianName(speciesID, newMetaForm)
        )

        val messages = arrayOf(

                bundle.format("monster_metamorphs", monsterNames[0]),
                bundle.format("monster_metamorph_complete", monsterNames[0], monsterNames[1])
        )

        // Actor Creation
        val background  = media.getMetamorphosisBackground()
        animation       = media.getMetamorphosisAnimation()
        imgBefore       = Image(media.getMonsterSprite(speciesID, formerMetaForm))
        imgAfter        = Image(media.getMonsterSprite(speciesID, newMetaForm))
        okButton        = ImageButton(skin, "burgund-close")
        label           = Label(messages[0], skin, "burgund")
        label.setSize(labelWidth, labelHeight)

        val particleActor = ParticleEffectActor("metamorphosis")
        particleActor.setPosition(particlesX, particlesY)

        // Listeners
        okButton.onClick()
        {
            val sequence = muteAudio() then fadeOut(.5f) then popScreen()
            stage+=sequence
        }

        // Layout
        background  .setPosition(bgX, bgY, bgAlign)
        animation   .setPosition(animationX, animationY, animationAlign)
        imgBefore   .setPosition(startImgX, startImgY, startImgAlign)
        imgAfter    .setPosition(afterImgX, afterImgY, afterImgAlign)
        okButton    .setPosition(okButtonX, okButtonY, okButtonAlign)
        label       .setPosition(labelX, labelY, labelAlign)

        val showAnimation = runAction()
        {
            label.remove()
            stage+=animation
            playMetamorphosisSFX()
            stage+=label
        }
        val removeAnimation = runAction()
        {
            animation.remove()
            imgBefore.remove()
            label.remove()
            stage+=imgAfter
            stage+=animation
            stage+=label
        }
        val playVictorySFX = runAction()
        {
            playVictorySFX()
            label.txt = messages[1]
        }
        val addOkButton = runAction { stage+=okButton }

        val metaAction =
                delay(4f)       then
                muteAudio()     then
                delay(1f)       then
                showAnimation   then
                delay(2f)       then
                removeAnimation then
                delay(3f)       then
                playVictorySFX  then
                delay(5.5f)     then
                fadeInMusic()   then
                addOkButton

        // Adding actors to stage
        stage+=background
        stage+=particleActor
        stage+=imgBefore
        stage+=metaAction
        stage+=label

        particleActor.start()
    }

    override fun reset() {}

    override fun show()
    {
        startBGMusic()
    }

    override fun hide()
    {
        super.hide()
        stopBGMusic()
    }

    companion object
    {
        const val particlesX = 214f
        const val particlesY = 136f

        const val bgX = 0f
        const val bgY = 0f
        const val bgAlign = Align.bottomLeft

        const val animationX = Constant.RES_X/2f - 128
        const val animationY = Constant.RES_Y/2f - 128 + 30
        const val animationAlign = Align.bottomLeft

        const val startImgX = Constant.RES_X/2f - 64
        const val startImgY = Constant.RES_Y/2f - 34
        const val startImgAlign = Align.bottomLeft

        const val afterImgX = Constant.RES_X/2f - 64
        const val afterImgY = Constant.RES_Y/2f - 34
        const val afterImgAlign = Align.bottomLeft

        const val okButtonX = Constant.RES_X - 16f
        const val okButtonY = 18f
        const val okButtonAlign = Align.bottomRight

        const val labelX = 4f
        const val labelY = 4f
        const val labelAlign = Align.bottomLeft
        const val labelWidth = 420f
        const val labelHeight = 64f

        fun startBGMusic()          = Services.getAudio().playLoopMusic(AssetPath.Audio.Music.METAMORPHOSIS)
        fun stopBGMusic()           = Services.getAudio().stopMusic(AssetPath.Audio.Music.METAMORPHOSIS)
        fun playVictorySFX()        = Services.getAudio().playMusic(AssetPath.Audio.Music.VICTORY_FANFARE)
        fun playMetamorphosisSFX()  = Services.getAudio().playSound(AssetPath.Audio.SFX.METAMORPHOSIS)

        fun muteAudio(): Action     = Services.getAudio().getMuteAudioAction(AssetPath.Audio.Music.METAMORPHOSIS)
        fun fadeInMusic(): Action   = Services.getAudio().getFadeInMusicAction(AssetPath.Audio.Music.METAMORPHOSIS)
        fun popScreen(): Action     = runAction { Services.getScreenManager().popScreen() }
    }
}
