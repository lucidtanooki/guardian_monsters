package de.limbusdev.guardianmonsters.guardians.abilities


import de.limbusdev.guardianmonsters.guardians.Element
import de.limbusdev.guardianmonsters.guardians.monsters.IndividualStatistics.StatusEffect

/**
 * @author Georg Eckert 2019
 */
class Ability
{
    // ............................................................................... Inner Classes
    /**
     * An aID provides a unique key for an ability. This way there is one central point of ability
     * management and Guardians only have to keep an aID instance to fetch the ability instance from
     * there.
     */
    class aID(val ID: Int, val element: Element)
    {
        /** Two abilities are equal, if their ID and element are equal. */
        override fun equals(other: Any?): Boolean
        {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            val otherID = other as aID

            return ID == otherID.ID && element == otherID.element
        }

        override fun hashCode(): Int = 31 * ID + element.hashCode()

        override fun toString(): String = "Ability $ID: $element"
    }

    enum class DamageType { MAGICAL, PHYSICAL }

    // .................................................................................. Properties
    var ID          : Int = 0
    var name        : String = ""
    val simpleName  : String get() = name.split("_")[1]

    var canChangeStatusEffect : Boolean = false
    var statusEffect          : StatusEffect = StatusEffect.HEALTHY
    var damageType            : DamageType = DamageType.PHYSICAL
    var element               : Element = Element.NONE

    var damage      : Int = 0
    var MPcost      : Int = 0
    var addsPStr    : Int = 0
    var addsPDef    : Int = 0
    var addsMStr    : Int = 0
    var addsMDef    : Int = 0
    var addsSpeed   : Int = 0
    var curesHP     : Int = 0
    var curesMP     : Int = 0

    var areaDamage  : Boolean = false
    var changesStats: Boolean = false
    var curesStats  : Boolean = false

    var probabilityToChangeStatusEffect : Int = 0


    // ................................................................................ Constructors
    /** For Serialization only! */
    constructor() {}

    /**
     * For physical Attacks
     *
     * @param damageType
     * @param element
     * @param damage
     * @param name
     * @param areaDamage
     * @param canChangeStatusEffect
     * @param statusEffect
     * @param probabilityToChangeStatusEffect
     */
    constructor
    (
            ID: Int,
            damageType: DamageType,
            element: Element,
            damage: Int,
            name: String,
            areaDamage: Boolean,
            canChangeStatusEffect: Boolean,
            statusEffect: StatusEffect,
            probabilityToChangeStatusEffect: Int
    ) : this (
            ID,
            damageType,
            element,
            damage,
            name,
            0,
            areaDamage,
            canChangeStatusEffect,
            statusEffect,
            probabilityToChangeStatusEffect
    ) {}

    /**
     * For magical Attacks
     *
     * @param damageType
     * @param element
     * @param damage
     * @param name
     * @param MPcost
     * @param areaDamage
     * @param canChangeStatusEffect
     * @param statusEffect
     * @param probabilityToChangeStatusEffect
     */
    @JvmOverloads
    constructor
    (
            ID: Int,
            damageType: DamageType,
            element: Element,
            damage: Int,
            name: String,
            MPcost: Int = 0,
            areaDamage: Boolean = false,
            canChangeStatusEffect: Boolean = false,
            statusEffect: StatusEffect = StatusEffect.HEALTHY,
            probabilityToChangeStatusEffect: Int = 0
    ) : this (
            ID,
            damageType,
            element,
            damage,
            name,
            0,
            areaDamage,
            canChangeStatusEffect,
            statusEffect,
            probabilityToChangeStatusEffect,
            false,
            0,
            0,
            0,
            0,
            0,
            false,
            0,
            0
    ) {}

    /**
     * For stat changing or curing abilities
     * @param ID
     * @param damageType
     * @param element
     * @param damage
     * @param name
     * @param MPcost
     * @param areaDamage
     * @param canChangeStatusEffect
     * @param statusEffect
     * @param probabilityToChangeStatusEffect
     */
    constructor
    (
            ID: Int,
            damageType: DamageType,
            element: Element,
            damage: Int,
            name: String,
            MPcost: Int,
            areaDamage: Boolean,
            canChangeStatusEffect: Boolean,
            statusEffect: StatusEffect,
            probabilityToChangeStatusEffect: Int,
            changesStats: Boolean,
            addsPStr: Int,
            addsPDef: Int,
            addsMStr: Int,
            addsMDef: Int,
            addsSpeed: Int,
            curesStats: Boolean,
            curesHP: Int,
            curesMP: Int
    ) {
        this.ID = ID
        this.element = element
        this.damageType = damageType
        this.damage = damage
        this.name = name
        this.MPcost = MPcost
        this.areaDamage = areaDamage
        this.canChangeStatusEffect = canChangeStatusEffect
        this.statusEffect = statusEffect
        this.probabilityToChangeStatusEffect = probabilityToChangeStatusEffect
        this.changesStats = changesStats
        this.addsPStr = addsPStr
        this.addsPDef = addsPDef
        this.addsMStr = addsMStr
        this.addsMDef = addsMDef
        this.addsSpeed = addsSpeed
        this.curesStats = curesStats
        this.curesHP = curesHP
        this.curesMP = curesMP
    }


    // ...................................................................................... Object
    override fun toString() = "Ability $ID of Element $element: $name"

    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Ability

        return (ID == other.ID) && (element == other.element)
    }

    override fun hashCode() = 31 * ID + element.hashCode()
}
