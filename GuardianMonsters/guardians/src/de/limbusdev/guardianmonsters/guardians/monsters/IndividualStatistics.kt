package de.limbusdev.guardianmonsters.guardians.monsters

import com.badlogic.gdx.math.MathUtils

import de.limbusdev.guardianmonsters.guardians.Constant
import de.limbusdev.guardianmonsters.guardians.StatCalculator
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityGraph
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment
import de.limbusdev.guardianmonsters.guardians.items.equipment.EquipmentPotential
import de.limbusdev.utils.MathTool


/**
 * Guardians Individual Statistic Component
 *
 * IndividualStatistics contains all statistic values of a [AGuardian]. The statistic values at
 * level 1 should be copied over from [CommonStatistics]
 *
 * The Status Values (Stats) are:
 *
 * HP   ..  Health Points
 * MP   ..  Magic Points
 * PStr ..  Physical Strength
 * PDef ..  Physical Defense
 * MStr ..  Magical Strength
 * MDef ..  Magical Defense
 * Speed
 *
 * Individual Base Stats (IndBaseStats) are different in every Guardian. They change randomly,
 * when the guardian levels up (0..2). They range from 0-15 for every Stat, except HP and MP,
 * there it's 0-63.
 *
 * Growth Stats determine how a Guardian ist developing. They range from 1..3 in every Battle.
 * And every Stat can get a maximum of 0..255.
 *
 * Development Mechanics based on:
 * http://howtomakeanrpg.com/a/how-to-make-an-rpg-levels.html
 * @author Georg Eckert 2016
 */

class IndividualStatistics
{
    // Late initialized properties
    private lateinit var core: AGuardian  // Core Object

    private lateinit var currentStatValues: Statistics  // current values of the various Stats (HP, MP, PStr, ...)
    private lateinit var fullStatValues: Statistics     // fully healed values of the various Stats, recalculate at level-up
    lateinit var indiBaseValues: Statistics             // individual base values of the various Stats, decided at birth
        private set
    lateinit var growthBaseValues: Statistics           // accumulated values earned during level-up
        private set

    var statusEffect: StatusEffect = StatusEffect.HEALTHY
        set(value)
        {
            field = value
            core.setStatisticsChanged()
            core.notifyObservers()
        }

    // ............................................................................................. SETTERS & GETTERS

    var level: Int = 0
        private set
    /**
     * returns the number of levels, which can be used to activate nodes in the [AbilityGraph]
     * @return
     */
    var abilityLevels: Int = 0
        private set
    var exp: Int = 0
        private set
    var remainingLevelUps: Int = 0
        private set          // How many levels have been reached without level-up

    var character: Int = 0   // for growth rates

    var hands: Equipment? = null
        private set
    var head: Equipment? = null
        private set
    var body: Equipment? = null
        private set
    var feet: Equipment? = null
        private set

    var latestLevelUpReport: LevelUpReport? = null
        private set

    // ............................................................................................. CALCULATED VALUES


    var hp: Int
        get() = currentStatValues.HP
        set(HP)
        {
            currentStatValues.HP = HP
            if(HP > hpMax) currentStatValues.HP = hpMax
            if(HP < 0)
            {
                currentStatValues.HP = 0
                statusEffect = StatusEffect.HEALTHY
            }

            core.setStatisticsChanged()
            core.notifyObservers()
        }

    var mp: Int
        get() = currentStatValues.MP
        set(MP)
        {
            currentStatValues.MP = MP

            if(MP > mPmax) currentStatValues.MP = mPmax
            if(MP < 0) currentStatValues.MP = 0

            core.setStatisticsChanged()
            core.notifyObservers()
        }

    var pStr: Int
        get() = currentStatValues.PStr
        set(PStr)
        {
            currentStatValues.PStr = PStr

            if(PStr > pStrMax * 1.5f) currentStatValues.PStr = MathUtils.floor(pStrMax * 1.5f)
            if(PStr < 1) currentStatValues.PStr = 1

            core.setStatisticsChanged()
            core.notifyObservers()
        }

    var pDef: Int
        get() = currentStatValues.PDef
        set(PDef)
        {
            currentStatValues.PDef = PDef

            if(PDef > pDefMax * 1.5f) MathUtils.floor(pDefMax * 1.5f)
            if(PDef < 1) currentStatValues.PDef = 1

            core.setStatisticsChanged()
            core.notifyObservers()
        }

    var mStr: Int
        get() = currentStatValues.MStr
        set(MStr)
        {
            currentStatValues.MStr = MStr

            if(MStr > mStrMax * 1.5f) currentStatValues.MStr = MathUtils.floor(mStrMax * 1.5f)
            if(MStr < 1) currentStatValues.MStr = 1

            core.setStatisticsChanged()
            core.notifyObservers()
        }

    var mDef: Int
        get() = currentStatValues.MDef
        set(MDef)
        {
            currentStatValues.MDef = MDef

            if(MDef > mDefMax * 1.5f) currentStatValues.MDef = MathUtils.floor(mDefMax * 1.5f)
            if(MDef < 1) currentStatValues.MDef = 1

            core.setStatisticsChanged()
            core.notifyObservers()
        }

    var speed: Int
        get() = currentStatValues.Speed
        set(Speed)
        {

            currentStatValues.Speed = Speed

            if(Speed > speedMax) currentStatValues.Speed = MathUtils.floor(speedMax * 1.5f)
            if(Speed < 1) currentStatValues.Speed = 1

            core.setStatisticsChanged()
            core.notifyObservers()
        }

    /**
     * Returns the highest value a guardian can reach, if it
     * receives the best values at every level up
     * @return
     */
    val maxPossibleHP: Int
        get() = StatCalculator.calculateHP(
                characterGrowthRates[character][StatType.HP],
                99,
                core.commonStatistics.getBaseHP(),
                indiBaseValues.HP + 50,
                255
        )

    val maxPossibleMP: Int
        get() = StatCalculator.calculateMP(
                characterGrowthRates[character][StatType.MP],
                99,
                core.commonStatistics.getBaseMP(),
                indiBaseValues.MP + 50,
                255
        )

    val maxPossiblePStr: Int
        get() = StatCalculator.calculateStat(
                characterGrowthRates[character][StatType.PSTR],
                99,
                core.commonStatistics.getBasePStr(),
                indiBaseValues.PStr + 50,
                255
        )

    val maxPossiblePDef: Int
        get() = StatCalculator.calculateStat(
                characterGrowthRates[character][StatType.PDEF],
                99,
                core.commonStatistics.getBasePDef(),
                indiBaseValues.PDef + 50,
                255
        )

    val maxPossibleMStr: Int
        get() = StatCalculator.calculateStat(
                characterGrowthRates[character][StatType.MSTR],
                99,
                core.commonStatistics.getBaseMStr(),
                indiBaseValues.MStr + 50,
                255
        )

    val maxPossibleMDef: Int
        get() = StatCalculator.calculateStat(
                characterGrowthRates[character][StatType.MDEF],
                99,
                core.commonStatistics.getBaseMDef(),
                indiBaseValues.MDef + 50,
                255
        )

    val maxPossibleSpeed: Int
        get() = StatCalculator.calculateStat(
                characterGrowthRates[character][StatType.SPEED],
                99,
                core.commonStatistics.getBaseSpeed(),
                indiBaseValues.Speed + 50,
                255
        )

    /**
     * @return maximum HP, taking [Equipment] into account
     */
    val hpMax: Int
        get()
        {
            var extFactor = 0f

            extFactor += hands?.addsHP ?: 0
            extFactor += body?.addsHP ?: 0
            extFactor += feet?.addsHP ?: 0
            extFactor += head?.addsHP ?: 0

            extFactor /= 100f

            return fullStatValues.HP + MathUtils.floor(fullStatValues.HP * extFactor)
        }

    /**
     * @return maximum MP, taking [Equipment] into account
     */
    val mPmax: Int
        get()
        {
            var extFactor = 0f

            extFactor += hands?.addsMP ?: 0
            extFactor += body?.addsMP ?: 0
            extFactor += feet?.addsMP ?: 0
            extFactor += head?.addsMP ?: 0

            extFactor /= 100f

            return fullStatValues.MP + MathUtils.floor(fullStatValues!!.MP * extFactor)
        }

    /**
     * @return maximum Pstr, taking [Equipment] into account
     */
    val pStrMax: Int
        get()
        {
            var extPStr = fullStatValues.PStr

            extPStr += hands?.addsPStr ?: 0
            extPStr += body?.addsPStr ?: 0
            extPStr += feet?.addsPStr ?: 0
            extPStr += head?.addsPStr ?: 0

            return extPStr
        }

    /**
     * @return maximum PDef, taking [Equipment] into account
     */
    val pDefMax: Int
        get()
        {
            var extPDef = fullStatValues.PDef

            extPDef += hands?.addsPDef ?: 0
            extPDef += body?.addsPDef ?: 0
            extPDef += feet?.addsPDef ?: 0
            extPDef += head?.addsPDef ?: 0

            return extPDef
        }

    /**
     * @return maximum MStr, taking [Equipment] into account
     */
    val mStrMax: Int
        get()
        {
            var extMStr = fullStatValues.MStr

            extMStr += hands?.addsMStr ?: 0
            extMStr += body?.addsMStr ?: 0
            extMStr += feet?.addsMStr ?: 0
            extMStr += head?.addsMStr ?: 0

            return extMStr
        }

    /**
     * @return maximum MDef, taking [Equipment] into account
     */
    val mDefMax: Int
        get()
        {
            var extMDef = fullStatValues.MDef

            extMDef += hands?.addsMDef ?: 0
            extMDef += body?.addsMDef ?: 0
            extMDef += feet?.addsMDef ?: 0
            extMDef += head?.addsMDef ?: 0

            return extMDef
        }

    /**
     * @return maximum Speed, taking [Equipment] into account
     */
    val speedMax: Int
        get() {
            var extSpeed = fullStatValues.Speed

            extSpeed += hands?.addsSpeed ?: 0
            extSpeed += body?.addsSpeed ?: 0
            extSpeed += feet?.addsSpeed ?: 0
            extSpeed += head?.addsSpeed ?: 0

            return extSpeed
        }

    val hpFractionToString: String
        get() = "${currentStatValues.HP} / $hpMax"

    val mpFractionToString: String
        get() = "${currentStatValues.MP} / $mPmax"


    val expFraction: Int
        get() = MathUtils.round(exp * 1f / StatCalculator.calcEXPtoReachLevel(level + 1) * 100f)

    /**
     * Returns HP fraction in %
     * @return
     */
    val hpFraction: Int
        get() = MathUtils.round(100f * currentStatValues.HP / hpMax)

    val mpFraction: Int
        get() = MathUtils.round(100f * currentStatValues.MP / mPmax)

    /**
     * Calculates how much EXP are still needed to reach the next level
     * @return
     */
    val expToNextLevel: Int
        get() = StatCalculator.calcEXPtoReachLevel(level) - exp

    val isFit: Boolean
        get() = currentStatValues.HP > 0

    val isKO: Boolean
        get() = !isFit



    // ............................................................................................. CONSTRUCTOR

    /**
     * For Serialization only!
     */
    constructor(
            core: AGuardian,
            level: Int,
            abilityLevels: Int,
            EXP: Int,
            character: Int,
            stats: Statistics,
            fullStatValues: Statistics,
            indiBaseValues: Statistics,
            growthBaseValues: Statistics,
            hands: Equipment,
            head: Equipment,
            body: Equipment,
            feet: Equipment) {
        this.core = core
        this.level = level
        this.remainingLevelUps = 0
        this.abilityLevels = abilityLevels
        this.exp = EXP
        this.character = character
        this.currentStatValues = stats
        this.fullStatValues = fullStatValues
        this.indiBaseValues = indiBaseValues
        this.growthBaseValues = growthBaseValues

        this.hands = hands
        this.head = head
        this.body = body
        this.feet = feet
        val nullStats = Statistics(0, 0, 0, 0, 0, 0, 0)
        latestLevelUpReport = LevelUpReport(nullStats, nullStats, 0, 0)
    }

    internal constructor(core: AGuardian, commonStatistics: CommonStatistics, level: Int, character: Int)
    {
        construct(core, commonStatistics, level, character)
    }

    /**
     * Creates an IndividualStatistics object, containing all information that is unique to the
     * submitted guardian.
     * @param core              the guardian, this object will belong to
     * @param commonStatistics  the common stats, which are equal for all guardians of this species
     * @param level             the level, the created guardian will have
     */
    internal constructor(core: AGuardian, commonStatistics: CommonStatistics, level: Int)
    {
        val character: Int
        // Choose a random character, to create really individual guardians
        when(MathUtils.random(0, 2))
        {
            2 -> character = Character.VIVACIOUS
            1 -> character = Character.PRUDENT
            else -> character = Character.BALANCED
        }
        construct(core, commonStatistics, level, character)
    }

    private fun construct(core: AGuardian, commonStatistics: CommonStatistics, level: Int, character: Int)
    {
        var commonStatistics = commonStatistics
        this.core = core
        this.character = character
        this.level = 0
        this.abilityLevels = -1
        this.remainingLevelUps = level
        this.statusEffect = StatusEffect.HEALTHY

        // ......................................................................................... base values
        this.growthBaseValues = Statistics(0, 0, 0, 0, 0, 0, 0)

        // Individual Base Values are determined once, and shall be never changed again
        this.indiBaseValues = Statistics(
                MathUtils.random(0, 63),
                MathUtils.random(0, 63),
                MathUtils.random(0, 15),
                MathUtils.random(0, 15),
                MathUtils.random(0, 15),
                MathUtils.random(0, 15),
                MathUtils.random(0, 15)
        )

        // Debugging
        if(Constant.DEBUGGING_ON)
        {
            commonStatistics = CommonStatistics(300, 50, 10, 11, 12, 13, 14)
            indiBaseValues = CommonStatistics(0, 0, 0, 0, 0, 0, 0)
            this.character = Character.BALANCED
        }

        // ......................................................................................... stats
        // Calculate the visible Stat values from the base values

        this.fullStatValues = StatCalculator.calculateAllStats(
                characterGrowthRates[character],
                level,
                core.commonStatistics,
                indiBaseValues,
                growthBaseValues
        )

        this.currentStatValues = fullStatValues.clone()

        // ......................................................................................... leveling
        for(i in 0 until level)
        {
            levelUp()
        }

        healCompletely()

        // set EXP according to level
        this.exp = StatCalculator.calcEXPtoReachLevel(level)

        val nullStats = Statistics(0, 0, 0, 0, 0, 0, 0)
        latestLevelUpReport = LevelUpReport(nullStats, nullStats, 0, 0)
    }

    // ............................................................................................. METHODS

    /**
     * Formula:
     * indiStats: raised at every LevelUp, randomly (HP, MP: 0..2, other: 0..1)
     * newMP = character * newLevel + 6 +  floor( (newLevel / 100) * (2*baseMP + indBaseMP + floor(growthMP/4))
     * other = floor(character * (5 + floor((newLevel/100) * (2*base+indi+floor(growth/4)))))
     * @return
     */
    fun levelUp(): LevelUpReport
    {
        // Raise growth base values randomly
        growthBaseValues = Statistics(
                growthBaseValues.HP + MathTool.dice(2, 3, 0),
                growthBaseValues.MP + MathTool.dice(2, 2, 0),
                growthBaseValues.PStr + MathTool.dice(1, 2, 0),
                growthBaseValues.PDef + MathTool.dice(1, 2, 0),
                growthBaseValues.MStr + MathTool.dice(1, 2, 0),
                growthBaseValues.MDef + MathTool.dice(1, 2, 0),
                growthBaseValues.Speed + MathTool.dice(1, 2, 0)
        )

        // Debugging
        if(Constant.DEBUGGING_ON)
        {
            growthBaseValues = CommonStatistics(0, 0, 0, 0, 0, 0, 0)
        }

        val commonBaseValues = core.commonStatistics
        val newLevel = this.level + 1

        // Calculate the Stats on the new level
        val newFullStatValues = StatCalculator.calculateAllStats(
                characterGrowthRates[character],
                newLevel,
                commonBaseValues,
                indiBaseValues,
                growthBaseValues
        )

        val report = LevelUpReport(
                fullStatValues.clone(),
                newFullStatValues,
                this.level,
                newLevel
        )

        this.level = report.newLevel
        this.fullStatValues = report.newStats

        this.abilityLevels += 1

        this.latestLevelUpReport = report
        this.remainingLevelUps--

        return report
    }

    fun earnEXP(EXP: Int)
    {
        // TODO add growth values for every victory, 0 when debugging
        var extFactor = 100f
        var potentiallyReachableLevel = this.level

        extFactor += hands?.addsEXP ?: 0
        extFactor += body?.addsEXP ?: 0
        extFactor += feet?.addsEXP ?: 0
        extFactor += head?.addsEXP ?: 0

        val extEXP = MathUtils.round(EXP * extFactor / 100f)

        this.exp += extEXP

        while(this.exp >= StatCalculator.calcEXPtoReachLevel(potentiallyReachableLevel))
        {
            this.exp -= StatCalculator.calcEXPtoReachLevel(potentiallyReachableLevel)
            potentiallyReachableLevel++
            remainingLevelUps++
        }

        core.setStatisticsChanged()
        core.notifyObservers()
    }

    /**
     * Resets all status values to the maximum
     */
    fun healCompletely()
    {
        hp = hpMax
        mp = mPmax
        pStr = pStrMax
        pDef = pDefMax
        mStr = mStrMax
        mDef = mDefMax
        speed = speedMax
        statusEffect = StatusEffect.HEALTHY
    }

    fun healHP(value: Int)
    {
        hp = currentStatValues.HP + value
    }

    fun decreaseHP(value: Int)
    {
        hp = currentStatValues.HP - value
    }

    fun healMP(value: Int)
    {
        mp = currentStatValues.MP + value
    }

    fun decreaseMP(value: Int)
    {
        mp = currentStatValues.MP - value
    }

    /**
     * Increases Physical Strength, by the given fraction (%)
     * @param fraction
     */
    fun modifyPStr(fraction: Int)
    {
        pStr = MathUtils.round(currentStatValues.PStr * (100 + fraction) / 100f)
    }

    /**
     * Increases Physical Defense, by the given fraction (%)
     * @param fraction
     */
    fun modifyPDef(fraction: Int)
    {
        pDef = MathUtils.round(currentStatValues.PDef * (100 + fraction) / 100f)
    }

    /**
     * Increases Magical Strength, by the given fraction (%)
     * @param fraction
     */
    fun modifyMStr(fraction: Int)
    {
        mStr = MathUtils.round(currentStatValues.MStr * (100 + fraction) / 100f)
    }

    /**
     * Increases Magical Defense, by the given fraction (%)
     * @param fraction
     */
    fun modifyMDef(fraction: Int)
    {
        mDef = MathUtils.round(currentStatValues.MDef * (100 + fraction) / 100f)
    }

    /**
     * Increases Speed, by the given fraction (%)
     * @param fraction
     */
    fun modifySpeed(fraction: Int)
    {
        speed = MathUtils.round(currentStatValues.Speed * (100 + fraction) / 100f)
    }

    fun didLevelUp(): Boolean
    {
        return remainingLevelUps > 0
    }

    fun giveEquipment(equipment: Equipment): Equipment?
    {
        var oldEquipment: Equipment? = null
        when(equipment.bodyPart)
        {
            BodyPart.HANDS -> {
                oldEquipment = hands
                hands = equipment
            }

            BodyPart.BODY -> {
                oldEquipment = body
                body = equipment
            }

            BodyPart.FEET -> {
                oldEquipment = feet
                feet = equipment
            }

            BodyPart.HEAD -> {
                oldEquipment = head
                head = equipment
            }
        }

        core.setStatisticsChanged()
        core.notifyObservers()

        return oldEquipment
    }

    /**
     * Calculates the [EquipmentPotential] of a given [Equipment] for this [Guardian]
     * @param eq
     * @return
     */
    fun getEquipmentPotential(eq: Equipment): EquipmentPotential
    {
        val pot: EquipmentPotential

        val currentEquipment: Equipment? = when(eq.bodyPart)
        {
            BodyPart.HEAD -> head
            BodyPart.BODY -> body
            BodyPart.FEET -> feet
            else -> hands
        }

        if(currentEquipment == null)
        {
            pot = EquipmentPotential(
                    eq.addsHP,
                    eq.addsMP,
                    eq.addsSpeed,
                    eq.addsEXP,
                    eq.addsPStr,
                    eq.addsPDef,
                    eq.addsMStr,
                    eq.addsMDef
            )
        }
        else
        {
            // Difference between currently equipped item and the new one
            pot = EquipmentPotential(
                    eq.addsHP - currentEquipment.addsHP,
                    eq.addsMP - currentEquipment.addsMP,
                    eq.addsSpeed - currentEquipment.addsSpeed,
                    eq.addsEXP - currentEquipment.addsEXP,
                    eq.addsPStr - currentEquipment.addsPStr,
                    eq.addsPDef - currentEquipment.addsPDef,
                    eq.addsMStr - currentEquipment.addsMStr,
                    eq.addsMDef - currentEquipment.addsMDef
            )
        }

        return pot
    }

    fun hasAbilityPoints(): Boolean = (abilityLevels > 0)

    fun hasHandsEquipped(): Boolean = (hands != null)

    fun hasHeadEquipped(): Boolean = (head != null)

    fun hasBodyEquipped(): Boolean = (body != null)

    fun hasFeetEquipped(): Boolean = (feet != null)

    /**
     * Resets boostable Stats: PStr, PDef, MStr, MDef, Speed
     */
    fun resetModifiedStats()
    {
        pStr = pStrMax
        pDef = pDefMax
        mStr = mStrMax
        mDef = mDefMax
        speed = speedMax
        statusEffect = StatusEffect.HEALTHY
    }

    /**
     * Reduces the number of available ability levels by 1,
     * usually, when a field in the ability board is activated
     */
    fun consumeAbilityLevel()
    {
        this.abilityLevels--
        core.setStatisticsChanged()
        core.notifyObservers()
    }

    // ............................................................................................. DELEGATIONS

    override fun toString(): String
    {
        return  "\n| HP: ${currentStatValues.HP} / ${fullStatValues.HP}" +
                "\n| MP: ${currentStatValues.MP} / ${fullStatValues.MP}" +
                "\n| PStr: ${currentStatValues.PStr} / ${fullStatValues.PStr}" +
                "\n| PDef: ${currentStatValues.PDef} / ${fullStatValues.PDef}" +
                "\n| MStr: ${currentStatValues.MStr} / ${fullStatValues.MStr}" +
                "\n| MDef: ${currentStatValues.MDef} / ${fullStatValues.MDef}" +
                "\n| Speed: ${currentStatValues.Speed} / ${fullStatValues.Speed}"
    }

    companion object
    {
        val characterGrowthRates = arrayOf(
                          /* PStr           PDef            MStr            MDef            Speed           HP              MP          */
                floatArrayOf(Growth.MED,    Growth.MED,     Growth.MED,     Growth.MED,     Growth.MED,     Growth.MED,     Growth.MED), // BALANCED
                floatArrayOf(Growth.FAST,   Growth.SLOW,    Growth.MED,     Growth.SLOW,    Growth.FAST,    Growth.SLOW,    Growth.MED), // VIVACIOUS
                floatArrayOf(Growth.MED,    Growth.FAST,    Growth.SLOW,    Growth.FAST,    Growth.SLOW,    Growth.FAST,    Growth.FAST)   // PRUDENT
        )
    }


    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ Inner Classes
    object Character
    {
        const val BALANCED = 0
        const val VIVACIOUS = 1
        const val PRUDENT = 2
    }

    enum class StatusEffect
    {
        HEALTHY, SLEEPING, PETRIFIED, BLIND, LUNATIC, POISONED
    }

    /**
     * The triple stands for rolling a dive parameters (Rolls, Sides, Base-Value)
     * 1D6 means one roll of a 6-sided dice
     *
     * Use Common for PStr, PDef, MStr, MDef, Speed
     *
     * Stat     SLOW        MED         FAST
     * Common   1D2         1D3         3D2
     * HP       4D30+100    4D40+100    4D50+100
     * MP       2D20+30     3D30+40     4D30+50
     */
    object Growth
    {
        const val SLOW = 0.9f
        const val MED = 1.0f
        const val FAST = 1.1f
    }

    object StatType
    {
        const val PSTR = 0
        const val PDEF = 1
        const val MSTR = 2
        const val MDEF = 3
        const val SPEED = 4
        const val HP = 5
        const val MP = 6
    }
}
