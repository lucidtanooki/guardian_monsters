/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.run as runThis
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align

import java.util.Comparator
import java.util.Observable
import java.util.Observer

import de.limbusdev.guardianmonsters.guardians.Side
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics.StatusEffect
import de.limbusdev.guardianmonsters.services.Services

import ktx.actors.plusAssign
import ktx.actors.then

/**
 * BattleGuardianWidget
 *
 * @author Georg Eckert 2018
 */

class BattleGuardianWidget
(
        index: Int,
        metaForm: Int,
        private val side: Side
)
    : BattleWidget(), Observer
{
    // .................................................................................. Properties
    private val guardianImage             : Image = Image()
    private val statusEffectAnimation     : AnimatedImage
    private var currentlyShowingTombstone : Boolean = false
    private var drawable                  : TextureRegionDrawable? = null


    // ................................................................................ Constructors
    init
    {
        // Setup Guardian Sprite
        guardianImage.setSize(128f, 128f)
        guardianImage.setPosition(0f, 0f, Align.bottom)
        addActor(guardianImage)

        // Setup Status Effect Animation
        val anim = Services.getMedia().getStatusEffectAnimation(StatusEffect.HEALTHY)
        statusEffectAnimation = AnimatedImage(anim)
        addActor(statusEffectAnimation)
        statusEffectAnimation.setPosition(0f, 96f, Align.top)

        initialize(index, metaForm, side)

        currentlyShowingTombstone = false
    }


    // .............................................................................. Initialization
    private fun initialize(index: Int, metaForm: Int, side: Side)
    {
        val guardianSprite = Services.getMedia().getMonsterSprite(index, metaForm)

        // Flip sprite, if on the left and not already flipped
        guardianSprite.flip((side == Side.LEFT), false)

        // Set Guardian Sprite
        drawable = TextureRegionDrawable(guardianSprite)
        guardianImage.drawable = drawable
    }


    // ..................................................................................... Methods
    /** Sets a new status effect animation. */
    private fun setStatusEffect(statusEffect: StatusEffect)
    {
        statusEffectAnimation.animation = Services.getMedia().getStatusEffectAnimation(statusEffect)
    }

    /** Runs a substitution animation (e.g. when swapping a Guardian with another). */
    fun substitute(index: Int, metaForm: Int, side: Side, onSubstitutionAnimationComplete: () -> Unit)
    {
        val sra = SelfRemovingAnimation(Services.getMedia().getBanningAnimation())
        sra.setPosition(0f, 0f, Align.bottom)
        addActor(sra)

        val setupAnimation = runThis {

            val anim2 = Services.getMedia().getSummoningAnimation()
            val sra2 = SelfRemovingAnimation(anim2)
            sra2.setPosition(0f, 0f, Align.bottom)
            addActor(sra2)
        }

        guardianImage.addAction(

                delay(1f)
                then visible(false)
                then setupAnimation
                then delay(1f)
                then runThis { initialize(index, metaForm, side) }
                then visible(true)
                then delay(1f)
                then runThis(onSubstitutionAnimationComplete)
        )
    }

    /** Runs a replacement animation. */
    fun replaceDefeated(index: Int, metaForm: Int, side: Side, onReplacingAnimationComplete: () -> Unit)
    {
        val anim = Services.getMedia().getSummoningAnimation()
        val sra = SelfRemovingAnimation(anim)

        sra.setPosition(0f, 0f, Align.bottom)

        guardianImage.addAction(

                fadeOut(1f)
                then createShowTombstoneAction()
                then fadeIn(1f)
                then runThis { addActor(sra) }
                then delay(1f)
                then runThis { initialize(index, metaForm, side) }
                then delay(1f)
                then runThis(onReplacingAnimationComplete)
        )
    }

    fun animateBan(onBanAnimationComplete: () -> Unit)
    {
        val anim = Services.getMedia().getBanningAnimation()
        val sra = SelfRemovingAnimation(anim)
        sra.setPosition(0f, 0f, Align.bottom)
        addActor(sra)
        guardianImage.addAction(

                delay(1f)
                then visible(false)
                then delay(1f)
                then runThis(onBanAnimationComplete)
        )
    }

    fun animateBanFailure(onBanFailureAnimationComplete: () -> Unit)
    {
        val anim = Services.getMedia().getSummoningAnimation()
        val sra = SelfRemovingAnimation(anim)
        sra.setPosition(0f, 0f, Align.bottom)
        addActor(sra)
        guardianImage.addAction(

                delay(1f)
                then visible(true)
                then delay(1f)
                then runThis(onBanFailureAnimationComplete)
        )
    }

    fun die(onDieAnimationComplete: () -> Unit)
    {
        when(side)
        {
            Side.LEFT ->
            {
                guardianImage.addAction(

                        alpha(0f, 2f)
                        then visible(false)
                        then createShowTombstoneAction()
                        then runThis { currentlyShowingTombstone = true }
                        then visible(true)
                        then alpha(1f, 2f)
                        then runThis(onDieAnimationComplete)
                )
            }
            Side.RIGHT -> guardianImage += (fadeOut(2f) then visible(false))
        }
    }

    private fun createShowTombstoneAction() : Action
    {
        return runThis {

            val tombStoneDrawable = Services.getUI().battleSkin.getRegion("tomb-stone")
            tombStoneDrawable.flip(side == Side.RIGHT, false)
            guardianImage.drawable = TextureRegionDrawable(tombStoneDrawable)
        }
    }

    override fun update(observable: Observable, o: Any?)
    {
        if (observable !is AGuardian) { return }

        setStatusEffect(observable.individualStatistics.statusEffect)
        if (currentlyShowingTombstone && observable.individualStatistics.isFit)
        {
            guardianImage.addAction(

                    alpha(0f, 2f)
                    then runThis { guardianImage.drawable = drawable }
                    then runThis { currentlyShowingTombstone = false }
                    then alpha(1f, 2f)
            )
        }
    }

    class ZComparator : Comparator<BattleGuardianWidget>
    {
        override fun compare(t0: BattleGuardianWidget, t1: BattleGuardianWidget): Int
        {
            return when
            {
                t0.y > t1.y -> SMALLER
                t0.y < t1.y -> BIGGER
                else        -> EQUAL
            }
        }

        companion object
        {
            const val SMALLER = -1
            const val BIGGER = 1
            const val EQUAL = 0
        }
    }
}
