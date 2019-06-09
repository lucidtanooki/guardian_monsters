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
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Action
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.Actions.delay
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
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.guardianmonsters.ui.Constant
import de.limbusdev.utils.extensions.set
import de.limbusdev.utils.geometry.IntVec2
import ktx.actors.then


/**
 * HINT: Don't forget calling the init() method
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
    private val leftPositionsOccupied       : ArrayMap<Int, Boolean> = ArrayMap()
    private val rightPositionsOccupied      : ArrayMap<Int, Boolean> = ArrayMap()

    private val monsterImgsLeft             : ArrayMap<Int, BattleGuardianWidget> = ArrayMap()
    private val monsterImgsRight            : ArrayMap<Int, BattleGuardianWidget> = ArrayMap()
    private val zSortedMonsterImgs          : Array<BattleGuardianWidget> = Array()
    private val statusEffectIndicatorsLeft  : ArrayMap<Int, Animation<TextureAtlas.AtlasRegion>>
    private val statusEffectIndicatorsRight : ArrayMap<Int, Animation<TextureAtlas.AtlasRegion>>

    var attackAnimationRunning              : Boolean = false
    var replacingDefeatedAnimationRunning   : Boolean = false

    private fun monsterImgs(side: Side) = when(side)
    {
        Side.LEFT  -> monsterImgsLeft
        Side.RIGHT -> monsterImgsRight
    }


    // ................................................................................ Constructors
    init
    {
        this.setBounds(0f, 0f, 0f, 0f)

        // Status Effect Animations
        this.statusEffectIndicatorsLeft = ArrayMap()
        this.statusEffectIndicatorsRight = ArrayMap()

        for (i in 0..2)
        {
            // left
            statusEffectIndicatorsLeft[i]  = Services.getMedia().getStatusEffectAnimation("healthy")
            // right
            statusEffectIndicatorsRight[i] = Services.getMedia().getStatusEffectAnimation("healthy")
        }
    }


    // .............................................................................. Initialization
    fun initialize(battleSystem: BattleSystem)
    {
        clear()
        zSortedMonsterImgs.clear()
        val queue = battleSystem.queue
        addMonsterAnimationsForTeam(queue.combatTeamLeft,  Side.LEFT)
        addMonsterAnimationsForTeam(queue.combatTeamRight, Side.RIGHT)
    }


    // ..................................................................................... Methods
    override fun draw(batch: Batch?, parentAlpha: Float)
    {
        sortMonsterSpritesByDepth()
        super.draw(batch, parentAlpha)
    }

    private fun sortMonsterSpritesByDepth()
    {
        zSortedMonsterImgs.sort(BattleGuardianWidget.ZComparator())
        zSortedMonsterImgs.forEach { img -> img.zIndex = zSortedMonsterImgs.indexOf(img, true) }
    }

    private fun addMonsterAnimationsForTeam(team: ArrayMap<Int, AGuardian>, side: Side)
    {
        val positions = when(side)
        {
            Side.LEFT  -> leftPositionsOccupied
            Side.RIGHT -> rightPositionsOccupied
        }

        positions.clear()
        monsterImgs(side).clear()

        // Not more than 3 monsters can join a fight
        val teamSize = if (team.size > 3) 3 else team.size
        var counter = 0
        var actualTeamSize = 0

        while (actualTeamSize < teamSize && counter < team.size)
        {
            val m = team.get(counter)
            if (m.individualStatistics.isFit)
            {
                // Add monster to team
                setUpMonsterSprite(
                        m,
                        m.speciesDescription.ID,
                        m.abilityGraph.currentForm,
                        actualTeamSize,
                        side
                )
                positions.put(actualTeamSize, true)
                actualTeamSize++
            }
            counter++
        }

        // Correct Image Depth Sorting
        if(positions.containsKey(2)) { addActor(monsterImgs(side)[2]) }
        if(positions.containsKey(1)) { addActor(monsterImgs(side)[1]) }
        if(positions.containsKey(0)) { addActor(monsterImgs(side)[0]) }
    }

    /**
     * Adds Monster sprite at the given battle arena position
     * @param id
     * @param pos
     * @param side
     */
    private fun setUpMonsterSprite(guardian: AGuardian, id: Int, metaForm: Int, pos: Int, side: Side)
    {
        val monImg = BattleGuardianWidget(id, metaForm, side)
        guardian.addObserver(monImg)

        val position2d: IntVec2 = when(side)
        {
            Side.LEFT -> // Hero Side
            {
                monsterImgsLeft[pos] = monImg
                when(pos)
                {
                    2    -> ImPos.HERO_TOP
                    1    -> ImPos.HERO_BOT
                    else -> ImPos.HERO_MID
                }
            }
            Side.RIGHT -> // Opponent Side
            {
                monsterImgsRight[pos] = monImg
                when(pos)
                {
                    2    -> ImPos.OPPO_TOP
                    1    -> ImPos.OPPO_BOT
                    else -> ImPos.OPPO_MID
                }
            }
        }

        zSortedMonsterImgs.add(monImg)

        monImg.setPosition(position2d.x.toFloat(), position2d.y.toFloat(), Align.bottom)
    }


    fun animateSelfDefense() { onHitAnimationComplete.invoke() }

    fun animateItemUsage() { onDoingNothing.invoke() }

    /**
     * Animate an ability of the given monster
     * @param attPos    position of attacker
     * @param defPos    position of defender
     * @param attSide      side of attacker
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

        // Get Position of attacking monster
        val startPos = when(attSide)
        {
            Side.LEFT -> when(attPos)
            {
                2    -> ImPos.HERO_TOP
                1    -> ImPos.HERO_BOT
                else -> ImPos.HERO_MID
            }
            Side.RIGHT -> when(attPos)
            {
                2    -> ImPos.OPPO_TOP
                1    -> ImPos.OPPO_BOT
                else -> ImPos.OPPO_MID
            }
        }

        // Get position of target monster
        val endPos = when(defSide)
        {
            Side.LEFT -> when(defPos)
            {
                2    -> ImPos.HERO_TOP
                1    -> ImPos.HERO_BOT
                else -> ImPos.HERO_MID
            }
            Side.RIGHT -> when(defPos)
            {
                2    -> ImPos.OPPO_TOP
                1    -> ImPos.OPPO_BOT
                else -> ImPos.OPPO_MID
            }
        }

        monsterImgs(attSide)[attPos].addAction(getAnimationSequence(ability, startPos, endPos, defPos, attSide, defSide))
    }

    fun animateAreaAttack(attPos: Int, attSide: Side, defSide: Side, ability: Ability)
    {
        monsterImgs(attSide)[attPos].addAction(getAreaAttackAnimationSequence(ability, attSide, defSide))
    }


    /**
     * Assembles the animation action sequence
     * @param ability
     * @param origin    position of the attacks origin
     * @param target    position of the attacks target
     * @return
     */
    private fun getAnimationSequence
    (
            ability: Ability,
            origin: IntVec2,
            target: IntVec2,
            targetPos: Int,
            side: Side,
            defSide: Side
    )
            : Action
    {
        val abilityMedia = AbilityMediaDB.getInstance().getAbilityMedia(ability.name)

        // Short delay before ability starts
        val delayAction       = delay(.5f)
        val horMovingAttDelay = delay(1f)

        // Moves actor from origin to target
        val moveToTargetAction = Actions.moveToAligned(target.xf, target.yf, Align.bottom, .6f, Interpolation.pow2In)

        // Moves actor back from target to origin
        val moveToOriginAction = Actions.moveToAligned(origin.xf, origin.yf, Align.bottom, .4f, Interpolation.pow2In)

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
            AnimationType.CONTACT ->
            {
                        delayAction             then
                        moveToTargetAction      then
                        attackAnimationAction   then
                        playSFXAction           then
                        animateImpactAction     then
                        callbackAction          then
                        moveToOriginAction
            }
            AnimationType.MOVING_HOR ->
            {
                        delayAction             then
                        attackAnimationAction   then
                        horMovingAttDelay       then
                        playSFXAction           then
                        animateImpactAction     then
                        callbackAction
            }
            AnimationType.MOVING_VERT ->
            {
                        delayAction             then
                        attackAnimationAction   then
                        playSFXAction           then
                        animateImpactAction     then
                        callbackAction
            }
            else -> // CONTACTLESS
            {
                        delayAction             then
                        attackAnimationAction   then
                        playSFXAction           then
                        animateImpactAction     then
                        callbackAction
            }
        }
    }

    private fun getAreaAttackAnimationSequence(ability: Ability, side: Side, defSide: Side) : Action
    {
        val abilityMedia = AbilityMediaDB.getInstance().getAbilityMedia(ability.name)

        val path = AssetPath.Audio.SFX.BATTLE().getValue(abilityMedia.sfxType.toString().toUpperCase())[0]

        return  delay(.5f)                                            then // Short delay before ability starts
                runThis { animateAreaAttackOfType(ability, defSide) } then // Plays the ability animation
                runThis { Services.getAudio().playSound(path) }       then // Plays the attacks sound
                runThis { animateAreaAttackImpact(defSide) }          then // Animates the impact on the target
                runThis { onHitAnimationComplete.invoke() }                // Runs the callback handler
    }

    /**
     * Animates the  impact of the attack
     * @param side
     * @param defPos
     */
    private fun animateAttackImpact(defPos: Int, side: Side)
    {
        val defIm = monsterImgs(side)[defPos]

        defIm.addAction(
                Actions.moveBy(0f, 15f, .1f, Interpolation.bounceIn) then
                Actions.moveBy(0f, -15f, .1f, Interpolation.bounceIn)
        )
    }

    /**
     * Animates the impact of an area attack for a whole side.
     * @param side
     */
    private fun animateAreaAttackImpact(side: Side)
    {
        for (defImg in monsterImgs(side).values())
        {
            defImg.addAction(
                    Actions.moveBy(0f, 15f, .1f, Interpolation.bounceIn) then
                    Actions.moveBy(0f, -15f, .1f, Interpolation.bounceIn)
            )
        }
    }

    fun animateMonsterKO(pos: Int, side: Side)
    {
        monsterImgs(side).get(pos).die(side, onDieing)
    }

    fun animateGuardianSubstitution
    (
            pos: Int,
            side: Side,
            substitutesID: Int,
            substitutesForm: Int,
            onSubstitutionComplete: () -> Unit,
            substituted: AGuardian,
            substitute: AGuardian
    ) {
        val guardianWidget = monsterImgs(side)[pos]
        substitute.addObserver(guardianWidget)
        guardianWidget.substitute(substitutesID, substitutesForm, side) { onSubstitutionComplete.invoke() }
    }

    fun animateReplacingDefeatedGuardian
    (
            pos: Int,
            side: Side,
            substitutesID: Int,
            substitutesForm: Int,
            onSubstitutionComplete: () -> Unit,
            substituted: AGuardian,
            substitute: AGuardian
    ) {
        val guardianWidget = monsterImgs(side)[pos]
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
        val guardianWidget = monsterImgs(side)[pos]
        guardianWidget.animateBan(onBanningTrialComplete)
    }

    fun animateBanningFailure
    (
            pos: Int,
            side: Side,
            guardianToBeBanned: AGuardian,
            callback: () -> Unit
    ) {
        val guardianWidget = monsterImgs(side)[pos]
        guardianWidget.animateBanFailure(callback)
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
                sra.setPosition(origin.x.toFloat(), origin.y.toFloat(), Align.bottom)
                sra.addAction(Actions.moveToAligned(target.x.toFloat(), target.y.toFloat(),
                        Align.bottom, 1f, Interpolation.linear))
            }
            AnimationType.MOVING_VERT ->
            {
                anim.frameDuration = 1f / anim.keyFrames.size
                sra.setPosition(target.x.toFloat(), (target.y + 128).toFloat(), Align.bottom)
                sra.addAction(Actions.moveToAligned(target.x.toFloat(), target.y.toFloat(),
                        Align.bottom, 1f, Interpolation.pow2In))
            }
            AnimationType.CONTACT ->
            {
                sra.setPosition(target.x.toFloat(), target.y.toFloat(), Align.bottom)
            }
            else  -> // CONTACTLESS
            {
                sra.setPosition(target.x.toFloat(), target.y.toFloat(), Align.bottom)
            }
        }

        addActor(sra)
    }

    private fun animateAreaAttackOfType(ability: Ability, defSide: Side)
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

        val abilityMedia = AbilityMediaDB.getInstance().getAbilityMedia(ability.name)

        val target = when(defSide)
        {
            Side.LEFT  -> IntVec2(128, 100)
            Side.RIGHT -> IntVec2(Constant.WIDTH + 64, 100)
        }

        sra.setPosition(target.x.toFloat(), target.y.toFloat(), Align.bottom)

        addActor(sra)
    }


    // ............................................................................... Inner Objects
    private object ImPos
    {
        val HERO_MID = IntVec2(32 + 64 + 64, 100 + 24)
        val HERO_BOT = IntVec2(32 + 16 + 64, 100)
        val HERO_TOP = IntVec2(32 + 112 + 64, 100 + 48)
        val OPPO_MID = IntVec2(640 + 32 - HERO_MID.x, HERO_MID.y)
        val OPPO_BOT = IntVec2(640 + 32 - HERO_BOT.x, HERO_BOT.y)
        val OPPO_TOP = IntVec2(640 + 32 - HERO_TOP.x, HERO_TOP.y)
    }
}
