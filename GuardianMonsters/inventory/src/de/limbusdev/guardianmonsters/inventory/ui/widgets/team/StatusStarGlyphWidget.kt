package de.limbusdev.guardianmonsters.inventory.ui.widgets.team

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.scene2d.ImgLayout
import de.limbusdev.guardianmonsters.scene2d.makeImage
import de.limbusdev.utils.extensions.*
import de.limbusdev.utils.logWarning
import ktx.style.get

/**
 * Draws a monsters values as a five sides star glyph of PStrength, PDefense,
 * MStrength, MDefense, Speed. All values are given as a fraction of 999.
 *
 * @author Georg Eckert 2017
 */

class StatusStarGlyphWidget(skin: Skin) : Group()
{
    // --------------------------------------------------------------------------------------------- PROPERTIES
    companion object { const val TAG = "StatusStarGlyphWidget" }

    private val shpRend: ShapeRenderer
    private var points = ArrayMap<Int, Vector2>()
    private val center: Vector2
    private val plotColor = Color.valueOf("eb8931ff")
    private var initialized: Boolean = false
    private val starGlyphBG: Image


    // --------------------------------------------------------------------------------------------- CONSTRUCTORS
    init
    {
        setSize(100f, 80f)
        shpRend = ShapeRenderer()
        center = Vector2(50.1f, 30.2f)
        initialized = false

        starGlyphBG = makeImage(skin["statPentagram-symbols"], ImgLayout(100f, 80f), this)
    }


    // --------------------------------------------------------------------------------------------- METHODS

    // .............................................................................. Initialization
    fun initialize(guardian: AGuardian)
    {
        val pstr  = guardian.stats.pStrMax.f()  / guardian.stats.maxPossiblePStr  * 32f
        val pdef  = guardian.stats.pDefMax.f()  / guardian.stats.maxPossiblePDef  * 32f
        val mstr  = guardian.stats.mStrMax.f()  / guardian.stats.maxPossibleMStr  * 32f
        val mdef  = guardian.stats.mDefMax.f()  / guardian.stats.maxPossibleMDef  * 32f
        val speed = guardian.stats.speedMax.f() / guardian.stats.maxPossibleSpeed * 32f

        points[0] = Vector2(center.x,                   center.y + pstr)
        points[1] = Vector2(center.x + 18f.cos * pdef,  center.y + 18f.sin * pdef)
        points[2] = Vector2(center.x + 54f.cos * speed, center.y - 54f.sin * speed)
        points[3] = Vector2(center.x - 54f.cos * mstr,  center.y - 54f.sin * mstr)
        points[4] = Vector2(center.x - 18f.cos * mdef,  center.y + 18f.sin * mdef)

        initialized = true
    }


    // ................................................................................... Rendering
    override fun draw(batch: Batch, parentAlpha: Float)
    {
        if (!initialized) { logWarning(TAG) { "Not initialized." }; return }

        batch.end()     // Stop Batch Rendering before starting Shape Renderer

        shpRend.projectionMatrix = batch.projectionMatrix
        shpRend.transformMatrix  = batch.transformMatrix
        shpRend.translate(x, y, 0f)

        drawShape()     // Render star plot

        batch.begin()   // Re-enable Batch Rendering
        super.draw(batch, parentAlpha)
    }

    /** Call batch.end() before this and batch.begin() after it. */
    private fun drawShape()
    {
        shpRend.begin(ShapeRenderer.ShapeType.Filled)

        // Draw Pentagon from triangles
        shpRend.color = plotColor

        for(i in 0..4)
        {
            // Assemble Star Plot from Triangles
            shpRend.triangle(

                    center.x,            center.y,
                    points[i].x,         points[i].y,
                    points[(i+1) % 5].x, points[(i+1) % 5].y
            )
        }

        shpRend.end()
    }

}
