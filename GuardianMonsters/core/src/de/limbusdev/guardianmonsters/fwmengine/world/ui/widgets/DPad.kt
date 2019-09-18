package de.limbusdev.guardianmonsters.fwmengine.world.ui.widgets

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.ArrayMap
import de.limbusdev.guardianmonsters.enums.Compass4
import de.limbusdev.guardianmonsters.enums.MoveDirection
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.extensions.set
import ktx.style.get

class DPad(skin: Skin = Services.UI().defaultSkin) : Image(skin.get<Drawable>("dpad_idle"))
{
    private var touchPosition: Vector2 = Vector2()

    private val dPadImgs = ArrayMap<MoveDirection, TextureRegion>()
    private val dPadArea: Rectangle
    private val dPadCenter: Vector2
    private val dPadCenterDist: Vector2
    var isTouched = false
        private set

    init
    {
        val borderDist = 8f
        this.dPadArea = Rectangle(
                borderDist,
                borderDist,
                112f,
                112f)
        this.dPadCenter = dPadArea.getCenter(Vector2())
        this.dPadCenterDist = Vector2()

        dPadImgs[MoveDirection.NONE] = skin["dpad_idle"]
        dPadImgs[MoveDirection.N]    = skin["dpad_up"]
        dPadImgs[MoveDirection.E]    = skin["dpad_right"]
        dPadImgs[MoveDirection.S]    = skin["dpad_down"]
        dPadImgs[MoveDirection.W]    = skin["dpad_left"]

        setSize(dPadArea.width, dPadArea.height)
    }

    data class TouchResult(val valid: Boolean, val direction: Compass4)

    fun touchDown(touchPosition: Vector2) : TouchResult
    {
        if (dPadArea.contains(touchPosition))
        {
            // Touch within digital pad constraints
            // decide direction
            dPadCenterDist.x = Math.abs(dPadCenter.x - touchPosition.x)
            dPadCenterDist.y = Math.abs(dPadCenter.y - touchPosition.y)

            val compass: Compass4 = when
            {
                dPadCenterDist.x >  dPadCenterDist.y && touchPosition.x <  dPadCenter.x -> Compass4.W
                dPadCenterDist.x >  dPadCenterDist.y && touchPosition.x >= dPadCenter.x -> Compass4.E
                dPadCenterDist.x <= dPadCenterDist.y && touchPosition.y <  dPadCenter.y -> Compass4.S
                else                                                                    -> Compass4.N
            }

            val region: TextureRegion = when (compass)
            {
                Compass4.N -> dPadImgs[MoveDirection.N]
                Compass4.E -> dPadImgs[MoveDirection.E]
                Compass4.S -> dPadImgs[MoveDirection.S]
                Compass4.W -> dPadImgs[MoveDirection.W]
            }

            drawable = TextureRegionDrawable(region)

            isTouched = true
            return TouchResult(true, compass)
        }

        return TouchResult(false, Compass4.S)
    }

    fun touchUp()
    {
        drawable = TextureRegionDrawable(dPadImgs[MoveDirection.NONE])
        isTouched = false
    }

    fun isValidTouch(touchPosition: Vector2) = dPadArea.contains(touchPosition)
}