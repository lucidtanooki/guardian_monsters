package de.limbusdev.guardianmonsters.fwmengine.menus.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.show as showActor
import com.badlogic.gdx.scenes.scene2d.actions.Actions.hide as hideActor
import com.badlogic.gdx.scenes.scene2d.actions.Actions.run as runThis
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport

import de.limbusdev.guardianmonsters.Constant
import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.fwmengine.managers.SaveGameManager
import de.limbusdev.guardianmonsters.fwmengine.world.ui.WorldScreen
import de.limbusdev.guardianmonsters.scene2d.*
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.widgets.CreditsScreenWidget
import de.limbusdev.guardianmonsters.ui.widgets.StartScreenWidget
import ktx.actors.then
import ktx.style.get


/**
 * @author Georg Eckert 2017
 */
class MainMenuScreen : Screen
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    // Scene2D.ui
    private val black: Image

    private lateinit var stage      : Stage

    private lateinit var startMenu  : Group
    private lateinit var introScreen: Group
    private lateinit var logoScreen : StartScreenWidget
    private lateinit var credits    : CreditsScreenWidget

    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        val skin = Services.UI().defaultSkin

        black = makeImage(skin["black"], Scene2DLayout(Constant.WIDTHf, Constant.HEIGHTf))

        setUpIntro(skin)
        setUpUI(skin)
        setUpStartMenu(skin)

        stage.addActor(black)
        stage.addActor(introScreen)
    }

    /* ............................................................................... METHODS .. */
    override fun show()
    {
        introScreen.addAction(fadeIn(1f) then delay(1f) then fadeOut(1f) then hideActor())
        black.addAction(delay(3f) then fadeOut(1f) then hideActor())
    }

    override fun render(delta: Float)
    {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // UI
        stage.act(delta)
        stage.draw()
    }

    override fun resize(width: Int, height: Int)
    {
        stage.viewport.update(width, height)
    }

    override fun pause()
    {
        // TODO
    }

    override fun resume()
    {
        // TODO
    }

    override fun hide()
    {
        // TODO
    }

    override fun dispose()
    {
        // TODO
    }


    fun setUpUI(skin: Skin)
    {
        // Scene2D
        stage = Stage(FitViewport(Constant.WIDTHf, Constant.HEIGHTf))
        Gdx.input.inputProcessor = stage

        logoScreen = StartScreenWidget(skin)

        logoScreen.startButton.replaceOnButtonClick {

            logoScreen.addAction(fadeOut(1f) then hideActor())
            startMenu.addAction(alpha(0f) then showActor() then delay(1f) then fadeIn(1f))
        }

        stage.addActor(logoScreen)
    }

    fun setUpStartMenu(skin: Skin)
    {
        startMenu = Group()

        // .................................................................................. IMAGES
        val bg = makeImage(skin["black"], Scene2DLayout(Constant.WIDTHf, Constant.HEIGHTf), startMenu)
        bg.addAction(alpha(.75f))

        val mon = Image(Services.Media().getMonsterSprite(100, 0))
        mon.setup(PositionXYA(Constant.WIDTHf-8, 8f, Align.bottomRight))
        startMenu.addActor(mon)

        // ................................................................................. BUTTONS
        val i18n = Services.I18N().General()

        // ............................................................................ START BUTTON
        var label = i18n.get("main_menu_start_new")
        val buttonStart = TextButton(label, skin, "button-96x32")

        buttonStart.replaceOnButtonClick {

            stage.addAction(fadeOut(1f) then runThis {

                SaveGameManager.newSaveGame()
                Services.ScreenManager().pushScreen(WorldScreen(25, 1, false))
            })
        }

        label = i18n.get("main_menu_load_saved")
        val buttonContinue = TextButton(label, skin, "button-96x32")

        buttonContinue.replaceOnButtonClick {

            stage.addAction(fadeOut(1f) then runThis {

                val state = SaveGameManager.loadSaveGame()
                Services.ScreenManager().pushScreen(WorldScreen(state.map, 1, true))
            })
        }

        // .......................................................................... CREDITS BUTTON
        credits = CreditsScreenWidget(skin)

        val buttonCredtis = TextButton(i18n.get("main_menu_credits"), skin, "button-96x32")

        buttonCredtis.replaceOnButtonClick {

            startMenu.addAction(fadeOut(1f) then hideActor() then delay(20f) then showActor() then fadeIn(1f))
            stage.addActor(credits)
            credits.start(20f)
        }

        // Layout
        val tableButtons = Table()
        tableButtons.top().left()
        tableButtons.debug = Constant.DEBUGGING_ON
        tableButtons.setSize(96f, Constant.HEIGHTf)
        tableButtons.setPosition(4f, Constant.HEIGHTf - 4, Align.topLeft)

        if (SaveGameManager.doesGameSaveExist())
        {
            tableButtons.add(buttonContinue).size(96f, 32f).spaceBottom(2f)
            tableButtons.row()
        }

        tableButtons.add(buttonStart).size(96f, 32f).spaceBottom(2f)
        tableButtons.row()
        tableButtons.add(buttonCredtis).size(96f, 32f).spaceBottom(2f)

        startMenu.addActor(tableButtons)
        startMenu.isVisible = false
        stage.addActor(startMenu)
    }


    fun setUpIntro(skin: Skin)
    {
        val logos = Services.Media().getTextureAtlas(AssetPath.Spritesheet.LOGOS)
        this.introScreen = Group()
        val bg = makeImage(skin["black"], Scene2DLayout(Constant.WIDTHf, Constant.HEIGHTf), introScreen)

        val logo = Image(logos.findRegion("limbusdevIntro"))
        logo.setup(PositionXYA(Constant.WIDTHf/2, Constant.HEIGHTf/2, Align.center), introScreen)
    }
}
