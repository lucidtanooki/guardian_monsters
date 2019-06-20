/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <limbusdev.games@gmail.com>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.actions.Actions.run as runThis
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.ArrayMap

import de.limbusdev.guardianmonsters.assets.paths.AssetPath
import de.limbusdev.guardianmonsters.battle.AbilityMediaDB
import de.limbusdev.guardianmonsters.battle.AnimationType
import de.limbusdev.guardianmonsters.guardians.Side
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics.StatusEffect
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.utils.extensions.set
import de.limbusdev.utils.geometry.IntVec2
import ktx.actors.plusAssign


/**
 * HINT: Don't forget calling the initialize() method
 *
 * @author Georg Eckert 2016
 */
class BattleAnimationWidget
(
        private val onHitAnimationComplete  : () -> Unit,
        private val onDieing                : () -> Unit,
        private val onDoingNothing          : () -> Unit
)
    : BattleWidget()
{
    // .................................................................................. Properties
    private val observers                   : Array<WidgetObserver>  = Array()

    private var attackAnimationRunning            : Boolean = false
    private var replacingDefeatedAnimationRunning : Boolean = false

    private val zSortedGuardianSprites      : Array<BattleGuardianWidget> = Array()
    private val guardianSprites             : ArrayMap<Side, ArrayMap<Int, BattleGuardianWidget>>
    private val occupiedPositions           : ArrayMap<Side, ArrayMap<Int, Boolean>>
    private val statusEffectIndicators      : ArrayMap<Side, ArrayMap<Int, Animation<TextureRegion>>>


    // ................................................................................ Constructors
    init
    {
        setBounds(0f, 0f, 0f, 0f)

        guardianSprites = ArrayMap();
        guardianSprites[Side.LEFT] = ArrayMap()
        guardianSprites[Side.RIGHT] = ArrayMap()

        occupiedPositions = ArrayMap()
        occupiedPositions[Side.LEFT] = ArrayMap()
        occupiedPositions[Side.RIGHT] = ArrayMap()

        // Status Effect Animations
        statusEffectIndicators= ArrayMap()
        statusEffectIndicators[Side.LEFT] = ArrayMap()
        statusEffectIndicators[Side.RIGHT] = ArrayMap()

        // Initialize all status indicators to healthy
        for (i in 0..2)
        {
            setStatusEffect(i, Side.LEFT, StatusEffect.HEALTHY)
            setStatusEffect(i, Side.RIGHT, StatusEffect.HEALTHY)
        }
    }


    // .............................................................................. Initialization
    fun initialize(battleSystem: BattleSystem)
    {
        clear()
        zSortedGuardianSprites.clear()
        addGuardianAnimationsFor(battleSystem.queue.combatTeamLeft,  Side.LEFT)
        addGuardianAnimationsFor(battleSystem.queue.combatTeamRight, Side.RIGHT)
    }

    private fun addGuardianAnimationsFor(team: ArrayMap<Int, AGuardian>, side: Side)
    {
        // Reset sprites and widgets
        occupiedPositions[side].clear()
        guardianSprites[side].clear()

        // The following checks are necessary, since [BattleSystem] does not apply any restrictions
        // on team size, to keep the battle system flexible and generic.

        // Not more than 3 monsters can join a fight
        val maxTeamSize = if (team.size > 3) 3 else team.size // How many Guardians may join the fight
        var actualTeamSize = 0                                // How many Guardians have actually been added
        var counter = 0                                       // How many Guardians have been checked

        // Check all Guardians in the given Combat Team, if they are fit to fight.
        // If so, add their sprite to the Battle Animation Widget.
        while (actualTeamSize < maxTeamSize && counter < team.size)
        {
            // Get Guardian from the team, and check if it can fight
            val guardian = team.get(counter)
            if (guardian.individualStatistics.isFit)
            {
                // Add Guardian to the actual team
                setUpGuardianSprite(guardian, actualTeamSize, side)
                occupiedPositions[side][actualTeamSize] = true
                actualTeamSize++
            }
            counter++
        }

        check(actualTeamSize > 0) { "$TAG: The actual team size must be > 0. Check that at least 1 Guardian ist fit." }

        // Correct Image Depth Sorting
        if(occupiedPositions[side].containsKey(2)) { addActor(guardianSprites[side][2]) }
        if(occupiedPositions[side].containsKey(1)) { addActor(guardianSprites[side][1]) }
        if(occupiedPositions[side].containsKey(0)) { addActor(guardianSprites[side][0]) }
    }


    // ............................................................................... Actor Methods
    override fun draw(batch: Batch?, parentAlpha: Float)
    {
        updateGuardianSpriteZOrder()
        super.draw(batch, parentAlpha)
    }


    // ..................................................................................... Methods
    /**
     * Adds Guardian sprite at the given battle arena position
     * @param position
     * @param side
     */
    private fun setUpGuardianSprite(guardian: AGuardian, position: Int, side: Side)
    {
        // Initialize Guardian Widget and bind it to the Guardian
        val guardianWidget = BattleGuardianWidget(guardian.speciesID, guardian.currentForm, side)
        guardian.addObserver(guardianWidget)

        // Add Guardian widget to this widget
        guardianSprites[side][position] = guardianWidget
        zSortedGuardianSprites.add(guardianWidget)

        // Layout Guardian widget
        setSpritePosition(guardianWidget, side, position)
    }

    /** Run the self defense animation and call the animation complete callback. */
    fun animateSelfDefense() { onHitAnimationComplete.invoke() }

    /** Run the item usage animation on call the doing nothing callback. */
    fun animateItemUsage() { onDoingNothing.invoke() }

    /** Dummy animation. Useful if no animation is needed. */
    fun animateIdle() { onDoingNothing.invoke()}

    /**
     * Animate an ability of the given monster
     * @param attPos    position of attacker
     * @param defPos    position of defender
     * @param attSide   side of attacker
     * @param defSide   side of target
     */
    fun animateAttack
    (
            attPos: Int,
            defPos: Int,
            attSide: Side,
            defSide: Side,
            ability: Ability
    ) {
        attackAnimationRunning = true

        // Get position of attacking monster
        val startPos = getSpritePosition(attSide, attPos)

        // Get position of target monster
        val endPos = getSpritePosition(defSide, defPos)

        guardianSprites[attSide][attPos] += createAnimationSequence(ability, startPos, endPos, defPos, attSide, defSide)
    }

    /** Adds an area attack animation action sequence to the attacking Guardian Widget */
    fun animateAreaAttack(attPos: Int, attSide: Side, defSide: Side, ability: Ability)
    {
        guardianSprites[attSide][attPos] += createAreaAttackAnimationSequence(ability, attSide, defSide)
    }

    /** Animates the impact of the attack */
    private fun animateAttackImpact(defPos: Int, side: Side)
    {
        guardianSprites[side][defPos] += impactAction
    }

    /** Animates the impact of an area attack for a whole side. */
    private fun animateAreaAttackImpact(side: Side)
    {
        guardianSprites[side].values().forEach { defenderSprite -> defenderSprite += impactAction }
    }

    fun animateMonsterKO(pos: Int, side: Side)
    {
        guardianSprites[side][pos].die(onDieing)
    }

    fun animateGuardianSubstitution
    (
            pos: Int,
            side: Side,
            onSubstitutionComplete: () -> Unit,
            substituted: AGuardian,
            substitute: AGuardian
    ) {
        val substitutesID = substitute.speciesID
        val substitutesForm = substitute.currentForm
        val guardianWidget = guardianSprites[side][pos]
        substitute.addObserver(guardianWidget)
        guardianWidget.substitute(substitutesID, substitutesForm, side) { onSubstitutionComplete.invoke() }
    }

    fun animateReplacingDefeatedGuardian
    (
            pos: Int,
            side: Side,
            onSubstitutionComplete: () -> Unit,
            substituted: AGuardian,
            substitute: AGuardian
    ) {
        val substitutesID = substitute.speciesID
        val substitutesForm = substitute.currentForm
        val guardianWidget = guardianSprites[side][pos]
        substitute.addObserver(guardianWidget)
        guardianWidget.replaceDefeated(substitutesID, substitutesForm, side) { onSubstitutionComplete.invoke() }
    }

    fun animateBanning
    (
            pos: Int,
            side: Side,
            guardianToBeBanned: AGuardian,
            onBanningTrialComplete: () -> Unit
    ) {
        guardianSprites[side][pos].animateBan(onBanningTrialComplete)
    }

    fun animateBanningFailure
    (
            pos: Int,
            side: Side,
            guardianToBeBanned: AGuardian,
            onBanningFailureComplete: () -> Unit = {}
    ) {
        guardianSprites[side][pos].animateBanFailure(onBanningFailureComplete)
    }

    /**
     * Takes an ability and starts the animation
     * @param ability
     */
    private fun animateAttackOfType(ability: Ability, origin: IntVec2, target: IntVec2)
    {
        val direction = when(origin.x > target.x)
        {
            true  -> Side.LEFT
            false -> Side.RIGHT
        }

        val anim = Services.getMedia().getAttackAnimation(ability.name) as Animation<TextureRegion>
        val sra = SelfRemovingAnimation(anim)
        anim.frameDuration = .1f
        // Ability direction
        when(direction)
        {
            Side.LEFT  -> sra.setSize(128f, 128f)
            Side.RIGHT -> sra.setSize(-128f, 128f) // flipped animation
        }
        sra.setAlign(Align.bottom)

        val abilityMedia = AbilityMediaDB.getInstance().getAbilityMedia(ability.name)

        when (abilityMedia.animationType)
        {
            AnimationType.MOVING_HOR ->
            {
                anim.frameDuration = 1f / anim.keyFrames.size
                sra.setPosition(origin.xf, origin.yf, Align.bottom)
                sra += moveToAligned(target.xf, target.yf, Align.bottom, 1f, Interpolation.linear)
            }
            AnimationType.MOVING_VERT ->
            {
                anim.frameDuration = 1f / anim.keyFrames.size
                sra.setPosition(target.xf, (target.yf + 128), Align.bottom)
                sra += moveToAligned(target.xf, target.yf, Align.bottom, 1f, Interpolation.pow2In)
            }
            AnimationType.CONTACT ->
            {
                sra.setPosition(target.xf, target.yf, Align.bottom)
            }
            else  -> // CONTACTLESS
            {
                sra.setPosition(target.xf, target.yf, Align.bottom)
            }
        }

        addActor(sra)
    }

    private fun animateAreaAttack(ability: Ability, defSide: Side)
    {
        val anim = Services.getMedia().getAttackAnimation(ability.name) as Animation<TextureRegion>
        val sra = SelfRemovingAnimation(anim)
        anim.frameDuration = .1f
        // Ability direction
        when(defSide)
        {
            Side.LEFT  -> sra.setSize(256f, 192f)
            Side.RIGHT -> sra.setSize(-256f, 192f) // flipped animation
        }
        sra.setAlign(Align.bottom)

        val target = when(defSide)
        {
            Side.LEFT  -> IntVec2(128, 100)
            Side.RIGHT -> IntVec2(Constant.WIDTH + 64, 100)
        }

        sra.setPosition(target.xf, target.yf, Align.bottom)

        addActor(sra)
    }


    // .............................................................................. Helper Methods
    /** Changes the status effect animation of a battle field slot. */
    private fun setStatusEffect(slot: Int, side: Side, effect: StatusEffect)
    {
        statusEffectIndicators[side][slot] = Services.getMedia().getStatusEffectAnimation(effect)
    }

    /** Changes the Guardian sprite's z indices. */
    private fun updateGuardianSpriteZOrder()
    {
        zSortedGuardianSprites.sort(BattleGuardianWidget.ZComparator())
        zSortedGuardianSprites.forEach { img -> img.zIndex = zSortedGuardianSprites.indexOf(img, true) }
    }

    /** Returns the coordinates at which a sprite should be placed, according to it's side and position. */
    private fun getSpritePosition(side: Side, position: Int) : IntVec2
    {
        return when(side)
        {
            Side.LEFT -> // Hero Side
            {
                when(position)
                {
                    2    -> SpritePositionLeft.TOP
                    1    -> SpritePositionLeft.BOT
                    else -> SpritePositionLeft.MID
                }
            }
            Side.RIGHT -> // Opponent Side
            {
                when(position)
                {
                    2    -> SpritePositionRight.TOP
                    1    -> SpritePositionRight.BOT
                    else -> SpritePositionRight.MID
                }
            }
        }
    }

    /** Sets the given [BattleGuardianWidget]'s position according to the wanted side and position. */
    private fun setSpritePosition(sprite: BattleGuardianWidget, side: Side, position: Int)
    {
        val spritePosition = getSpritePosition(side, position)
        sprite.setPosition(spritePosition.xf, spritePosition.yf, Align.bottom)
    }


    // ............................................................. Action Sequence Factory Methods
    /**
     * Assembles the animation action sequence
     * @param ability
     * @param origin    position of the attacks origin
     * @param target    position of the attacks target
     * @return
     */
    private fun createAnimationSequence
    (
            ability   : Ability,
            origin    : IntVec2,
            target    : IntVec2,
            targetPos : Int,
            side      : Side,
            defSide   : Side
    )
        : Action
    {
        val abilityMedia = AbilityMediaDB.getInstance().getAbilityMedia(ability.name)

        // ...................................................... Setup Actions
        // Short delay before ability starts
        val delayAction       = delay(.5f)
        val horMovingAttDelay = delay(1f)

        // Moves actor from origin to target
        val moveToTargetAction = moveToAligned(target.xf, target.yf, Align.bottom, .6f, Interpolation.pow2In)

        // Moves actor back from target to origin
        val moveToOriginAction = moveToAligned(origin.xf, origin.yf, Align.bottom, .4f, Interpolation.pow2In)

        // Plays the attacks sound
        val path = AssetPath.Audio.SFX.BATTLE().getValue(abilityMedia.sfxType.toString().toUpperCase())[0]
        val playSFXAction           = runThis { Services.getAudio().playSound(path) }

        // Plays the ability animation
        val attackAnimationAction   = runThis { animateAttackOfType(ability, origin, target) }

        // Runs the callback handler
        val callbackAction          = runThis { onHitAnimationComplete.invoke() }

        // Animates the impact of the ability on the target
        val animateImpactAction     = runThis { animateAttackImpact(targetPos, defSide) }

        return when (abilityMedia.animationType)
        {
            AnimationType.CONTACT     -> sequence(

                    delayAction,
                    moveToTargetAction,
                    attackAnimationAction,
                    playSFXAction,
                    animateImpactAction,
                    callbackAction,
                    moveToOriginAction
            )
            AnimationType.MOVING_HOR  -> sequence(

                    delayAction,
                    attackAnimationAction,
                    horMovingAttDelay,
                    playSFXAction,
                    animateImpactAction,
                    callbackAction
            )
            AnimationType.MOVING_VERT -> sequence(

                    delayAction,
                    attackAnimationAction,
                    playSFXAction,
                    animateImpactAction,
                    callbackAction
            )
            else /* CONTACTLESS */    -> sequence(

                    delayAction,
                    attackAnimationAction,
                    playSFXAction,
                    animateImpactAction,
                    callbackAction
            )
        }
    }

    private fun createAreaAttackAnimationSequence(ability: Ability, side: Side, defSide: Side) : Action
    {
        val abilityMedia = AbilityMediaDB.getInstance().getAbilityMedia(ability.name)
        val path = AssetPath.Audio.SFX.BATTLE().getValue(abilityMedia.sfxType.toString().toUpperCase())[0]

        return sequence(

                delay(.5f) ,                                     // Short delay before ability starts
                runThis { animateAreaAttack(ability, defSide) }, // Plays the ability animation
                runThis { Services.getAudio().playSound(path) }, // Plays the attacks sound
                runThis { animateAreaAttackImpact(defSide)    }, // Animates the impact on the target
                runThis { onHitAnimationComplete.invoke()     }  // Runs the callback handler
        )
    }


    // ............................................................................... Inner Objects
    private object SpritePositionLeft
    {
        val MID = IntVec2(32 + 64 + 64, 100 + 24)
        val BOT = IntVec2(32 + 16 + 64, 100)
        val TOP = IntVec2(32 + 112 + 64, 100 + 48)
    }

    private object SpritePositionRight
    {
        val MID = IntVec2(640 + 32 - SpritePositionLeft.MID.x, SpritePositionLeft.MID.y)
        val BOT = IntVec2(640 + 32 - SpritePositionLeft.BOT.x, SpritePositionLeft.BOT.y)
        val TOP = IntVec2(640 + 32 - SpritePositionLeft.TOP.x, SpritePositionLeft.TOP.y)
    }

    companion object
    {
        const val TAG = "BattleAnimationWidget"

        val impactAction : Action get() = sequence(
            moveBy(0f, 15f, .1f, Interpolation.bounceIn),
            moveBy(0f, -15f, .1f, Interpolation.bounceIn))
    }
}
