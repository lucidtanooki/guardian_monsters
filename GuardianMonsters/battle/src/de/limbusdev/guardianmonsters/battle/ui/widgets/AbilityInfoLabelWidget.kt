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
import com.badlogic.gdx.utils.Align

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.services.Services
import ktx.style.get

/**
 * AbilityInfoLabelWidget displays information about the given [Ability], like damage, MP cost,
 * a textual description and it's element.
 *
 * @author Georg Eckert 2018
 */

class AbilityInfoLabelWidget() : InfoLabelWidget()
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
        // Shorter Skin Access
        val battleSkin    = Services.getUI().battleSkin
        val inventorySkin = Services.getUI().inventorySkin

        // Background Image
        infoBGImg.drawable = battleSkin.getDrawable("label-info")

        // Element Label
        element = Label("None", inventorySkin, "elem-none")
        element.width = 72f
        element.setPosition(386f, 22f, Align.bottomRight)

        // Ability Name Label
        abilityName = Label("Unknown", battleSkin)
        abilityName.setPosition(118f, 54f, Align.topLeft)

        // Ability Description Label
        abilityDescription = Label("No description available", battleSkin)
        abilityDescription.setSize(200f, 32f)
        abilityDescription.setPosition(118f, 40f, Align.topLeft)
        abilityDescription.setWrap(true)

        // Ability Damage and MP cost Labels & Symbols
        abilityDamage = Label("0", battleSkin)
        abilityMPCost = Label("0", battleSkin)

        symbolPStr = Image(inventorySkin.getDrawable("stats-symbol-pstr"))
        symbolMStr = Image(inventorySkin.getDrawable("stats-symbol-mstr"))
        symbolMP   = Image(inventorySkin.getDrawable("stats-symbol-mp"))

        symbolPStr.setPosition(36f, 26f, Align.bottomLeft)
        symbolMStr.setPosition(36f, 26f, Align.bottomLeft)
        abilityDamage.setPosition(52f, 27f, Align.bottomLeft)
        symbolMP.setPosition(80f, 26f, Align.bottomLeft)
        abilityMPCost.setPosition(96f, 27f, Align.bottomLeft)

        // Adding actors to Widget and apply z-Order (is order of adding)
        addActor(element)
        addActor(abilityName)
        addActor(abilityDescription)
        addActor(abilityDamage)
    }


    // .............................................................................. Initialization
    /** Initializes the info label with the given [Ability]'s information. */
    fun initialize(aID: Ability.aID)
    {
        reset()

        val abilities = GuardiansServiceLocator.abilities
        val i18nElements = Services.getL18N().Elements()
        val i18nAbilities = Services.getL18N().Abilities()
        val elementName = aID.element.toString().toLowerCase()

        val ability = abilities.getAbility(aID)

        element.setText(i18nElements.get("element_$elementName"))
        element.style = Services.getUI().inventorySkin["elem-$elementName"]

        abilityName.setText(i18nAbilities.get(ability.name))
        abilityDamage.setText("${ability.damage}")
        abilityMPCost.setText("${ability.MPcost}")
        abilityDescription.setText(i18nAbilities.get("${ability.name}_desc"))

        when(ability.damageType)
        {
            Ability.DamageType.PHYSICAL ->
            {
                addActor(symbolPStr)
            }
            Ability.DamageType.MAGICAL ->
            {
                addActor(symbolMStr)
                addActor(symbolMP)
                addActor(abilityMPCost)
            }
        }
    }

    private fun reset()
    {
        abilityMPCost.remove()
        symbolMP.remove()
        symbolPStr.remove()
        symbolMStr.remove()
    }
}
