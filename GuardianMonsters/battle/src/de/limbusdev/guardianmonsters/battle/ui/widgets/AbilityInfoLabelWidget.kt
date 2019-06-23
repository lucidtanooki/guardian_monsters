/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.battle.ui.widgets

import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.scene2d.*
import de.limbusdev.guardianmonsters.services.Services
import de.limbusdev.utils.extensions.toLCString
import ktx.actors.minusAssign
import ktx.actors.plusAssign
import ktx.actors.txt
import ktx.style.get

/**
 * AbilityInfoLabelWidget displays information about the given [Ability], like damage, MP cost,
 * a textual description and it's element.
 *
 * @author Georg Eckert 2018
 */

class AbilityInfoLabelWidget
(
        private val battleSkin: Skin = Services.UI().battleSkin,
        private val inventorySkin: Skin = Services.UI().inventorySkin
)
    : InfoLabelWidget()
{
    // .................................................................................. Properties
    private val element             : Label
    private val abilityName         : Label
    private val abilityDescription  : Label
    private val abilityDamage       : Label
    private val abilityMPCost       : Label
    private val symbolPStr          : Image
    private val symbolMStr          : Image
    private val symbolMP            : Image


    // ................................................................................ Constructors
    init
    {
        // Background Image
        infoBGImg.drawable = battleSkin["label-info"]

        // Element Label
        element = makeLabel(

                style = inventorySkin["elem-none"],
                text = "None",
                layout = Layout2D(72f, 24f, 386f, 22f, Align.bottomRight)
        )

        // Ability Name Label
        abilityName = makeLabel(

                skin = battleSkin,
                text = "Unknown",
                position = Position2D(118f, 54f, Align.topLeft)
        )

        // Ability Description Label
        abilityDescription = makeLabel(

                skin = battleSkin,
                text = "No description available",
                layout = LabelLayout2D(200f, 32f, 118f, 40f, Align.topLeft, Align.topLeft, true)
        )

        // Ability Damage and MP cost Labels & Symbols
        abilityDamage = Label("0", battleSkin)
        abilityMPCost = Label("0", battleSkin)

        symbolPStr = makeImage(inventorySkin["stats-symbol-pstr"])
        symbolMStr = makeImage(inventorySkin["stats-symbol-mstr"])
        symbolMP   = makeImage(inventorySkin["stats-symbol-mp"])

        symbolPStr.position     = Position2D(36f, 26f, Align.bottomLeft)
        symbolMStr.position     = Position2D(36f, 26f, Align.bottomLeft)
        abilityDamage.position  = Position2D(52f, 27f, Align.bottomLeft)
        symbolMP.position       = Position2D(80f, 26f, Align.bottomLeft)
        abilityMPCost.position  = Position2D(96f, 27f, Align.bottomLeft)

        // Adding actors to Widget and apply z-Order (is order of adding)
        this += element
        this += abilityName
        this += abilityDescription
        this += abilityDamage
    }


    // .............................................................................. Initialization
    /** Initializes the info label with the given [Ability]'s information. */
    fun initialize(aID: Ability.aID)
    {
        reset()

        val abilities = GuardiansServiceLocator.abilities
        val i18nElements = Services.I18N().Elements()
        val i18nAbilities = Services.I18N().Abilities()
        val elementName = aID.element.toLCString()

        val ability = abilities.getAbility(aID)

        element.txt = i18nElements["element_$elementName"]
        element.style = inventorySkin["elem-$elementName"]

        abilityName.txt        = i18nAbilities[ability.name]
        abilityDescription.txt = i18nAbilities["${ability.name}_desc"]

        abilityDamage.txt = "${ability.damage}"
        abilityMPCost.txt = "${ability.MPcost}"

        when(ability.damageType)
        {
            Ability.DamageType.PHYSICAL ->
            {
                this += symbolPStr
            }
            Ability.DamageType.MAGICAL ->
            {
                this += symbolMStr
                this += symbolMP
                this += abilityMPCost
            }
        }
    }

    private fun reset()
    {
        this -= abilityMPCost
        this -= symbolMP
        this -= symbolPStr
        this -= symbolMStr
    }
}
