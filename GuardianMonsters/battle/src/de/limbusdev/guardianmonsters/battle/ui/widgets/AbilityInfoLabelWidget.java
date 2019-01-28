/*
 * *************************************************************************************************
 * Copyright (c) 2018. limbusdev (Georg Eckert) - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Georg Eckert <georg.eckert@limbusdev.de>
 * *************************************************************************************************
 */

package de.limbusdev.guardianmonsters.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.abilities.IAbilityService;
import de.limbusdev.guardianmonsters.services.Services;

/**
 * AbilityInfoLabelWidget
 *
 * @author Georg Eckert 2018
 */

public class AbilityInfoLabelWidget extends InfoLabelWidget
{
    private Label element;
    private Label abilityName;
    private Label abilityDescription;
    private Label abilityDamage;
    private Label abilityMPcost;
    private Image SymbolPStr, SymbolMStr;
    private Image SymbolMP;

    private Skin inventorySkin;

    public AbilityInfoLabelWidget(Skin skin, Skin inventorySkin)
    {
        super(skin);
        this.inventorySkin = inventorySkin;
        infoBGImg.setDrawable(skin.getDrawable("label-info"));
        element = new Label("None", inventorySkin, "elem-none");
        abilityName = new Label("Unknown", skin);
        abilityDescription = new Label("No description available", skin);
        abilityDamage = new Label("0", skin);
        abilityMPcost = new Label("0", skin);
        SymbolPStr = new Image(inventorySkin.getDrawable("stats-symbol-pstr"));
        SymbolMStr = new Image(inventorySkin.getDrawable("stats-symbol-mstr"));
        SymbolMP = new Image(inventorySkin.getDrawable("stats-symbol-mp"));

        element.setWidth(72);
        element.setPosition(386,22, Align.bottomRight);
        abilityName.setPosition(118,54,Align.topLeft);
        abilityDescription.setSize(200,32);
        abilityDescription.setPosition(118,40,Align.topLeft);
        abilityDescription.setWrap(true);
        SymbolPStr.setPosition(36, 26, Align.bottomLeft);
        SymbolMStr.setPosition(36, 26, Align.bottomLeft);
        abilityDamage.setPosition(52, 27, Align.bottomLeft);
        SymbolMP.setPosition(80, 26, Align.bottomLeft);
        abilityMPcost.setPosition(96, 27, Align.bottomLeft);

        addActor(element);
        addActor(abilityName);
        addActor(abilityDescription);
        addActor(abilityDamage);
    }

    public void init(Ability.aID aID)
    {
        IAbilityService abilities = GuardiansServiceLocator.INSTANCE.getAbilities();
        I18NBundle i18nElements = Services.getL18N().Elements();
        I18NBundle i18nAbilities = Services.getL18N().Abilities();

        Ability ability = abilities.getAbility(aID);

        element.setText(i18nElements.get("element_" + aID.element.toString().toLowerCase()));
        element.setStyle(inventorySkin.get("elem-" + aID.element.toString().toLowerCase(), Label.LabelStyle.class));

        abilityName.setText(i18nAbilities.get(ability.name));
        abilityDamage.setText(Integer.toString(ability.damage));
        abilityMPcost.setText(Integer.toString(ability.MPcost));
        abilityDescription.setText(i18nAbilities.get(ability.name + "_desc"));

        abilityMPcost.remove();
        SymbolMP.remove();
        SymbolPStr.remove();
        SymbolMStr.remove();

        if(ability.damageType == Ability.DamageType.PHYSICAL) {
            addActor(SymbolPStr);
        } else {
            addActor(SymbolMStr);
            addActor(SymbolMP);
            addActor(abilityMPcost);
        }
    }
}
