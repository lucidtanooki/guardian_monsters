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
import com.badlogic.gdx.utils.I18NBundle

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator
import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityService
import de.limbusdev.guardianmonsters.services.Services

/**
 * AbilityInfoLabelWidget
 *
 * @author Georg Eckert 2018
 */

class AbilityInfoLabelWidget
(
        skin: Skin,
        private val inventorySkin: Skin
)
    : InfoLabelWidget(skin)
{
    private val element             : Label
    private val abilityName         : Label
    private val abilityDescription  : Label
    private val abilityDamage       : Label
    private val abilityMPcost       : Label
    private val SymbolPStr          : Image
    private val SymbolMStr          : Image
    private val SymbolMP            : Image

    init
    {
        infoBGImg.drawable = skin.getDrawable("label-info")
        element = Label("None", inventorySkin, "elem-none")
        abilityName = Label("Unknown", skin)
        abilityDescription = Label("No description available", skin)
        abilityDamage = Label("0", skin)
        abilityMPcost = Label("0", skin)
        SymbolPStr = Image(inventorySkin.getDrawable("stats-symbol-pstr"))
        SymbolMStr = Image(inventorySkin.getDrawable("stats-symbol-mstr"))
        SymbolMP = Image(inventorySkin.getDrawable("stats-symbol-mp"))

        element.width = 72f
        element.setPosition(386f, 22f, Align.bottomRight)
        abilityName.setPosition(118f, 54f, Align.topLeft)
        abilityDescription.setSize(200f, 32f)
        abilityDescription.setPosition(118f, 40f, Align.topLeft)
        abilityDescription.setWrap(true)
        SymbolPStr.setPosition(36f, 26f, Align.bottomLeft)
        SymbolMStr.setPosition(36f, 26f, Align.bottomLeft)
        abilityDamage.setPosition(52f, 27f, Align.bottomLeft)
        SymbolMP.setPosition(80f, 26f, Align.bottomLeft)
        abilityMPcost.setPosition(96f, 27f, Align.bottomLeft)

        addActor(element)
        addActor(abilityName)
        addActor(abilityDescription)
        addActor(abilityDamage)
    }

    fun init(aID: Ability.aID)
    {
        val abilities = GuardiansServiceLocator.abilities
        val i18nElements = Services.getL18N().Elements()
        val i18nAbilities = Services.getL18N().Abilities()

        val ability = abilities.getAbility(aID)

        element.setText(i18nElements.get("element_${aID.element.toString().toLowerCase()}"))
        element.style = inventorySkin.get("elem-${aID.element.toString().toLowerCase()}", Label.LabelStyle::class.java)

        abilityName.setText(i18nAbilities.get(ability.name))
        abilityDamage.setText("${ability.damage}")
        abilityMPcost.setText("${ability.MPcost}")
        abilityDescription.setText(i18nAbilities.get("${ability.MPcost}_desc"))

        abilityMPcost.remove()
        SymbolMP.remove()
        SymbolPStr.remove()
        SymbolMStr.remove()

        if (ability.damageType === Ability.DamageType.PHYSICAL)
        {
            addActor(SymbolPStr)
        }
        else
        {
            addActor(SymbolMStr)
            addActor(SymbolMP)
            addActor(abilityMPcost)
        }
    }
}
