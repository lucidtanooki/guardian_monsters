package de.limbusdev.guardianmonsters.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import de.limbusdev.guardianmonsters.assets.paths.AssetPath;
import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.widgets.Callback;

public class AttackMenuWidget extends SevenButtonsWidget
{

    private static int[] order = {5,3,1,0,4,2,6};

    /**
     *
     * @param skin battle action UI skin
     */
    public AttackMenuWidget(Skin skin, Callback.ButtonID callbacks)
    {
        super(skin, callbacks::onClick, order);
    }

    public void toAttackInfoStyle()
    {
        for(Integer i : getButtons().keys())
        {
            getButton(i).setStyle(skin.get("b-attack-info", TextButton.TextButtonStyle.class));
        }
    }

    private void resetButton(int index)
    {
        disableButton(index);
        setButtonStyle(index, Element.NONE);
        setButtonText(index, "");
    }

    public void init(AGuardian guardian, boolean disableAbilitiesWithInsufficientMP)
    {
        // Set all buttons inactive & reset appearance
        for (Integer i : getButtons().keys())
        {
            resetButton(i);
        }

        // for every attack, activate a button
        for (int i = 0; i < 7; i++)
        {
            Ability.aID abilityID = guardian.getAbilityGraph().getActiveAbility(i);

            if (abilityID != null) {

                Ability attack = GuardiansServiceLocator.INSTANCE.getAbilities().getAbility(abilityID);
                setButtonStyle(i, skin, AssetPath.Skin.attackButtonStyle(attack.element));
                setButtonText(i, Services.getL18N().Abilities().get(attack.name));

                enableButton(i);

                // Disable Ability, when monster does not have enough MP for it
                if (disableAbilitiesWithInsufficientMP) {

                    if (attack.MPcost > guardian.getIndividualStatistics().getMp()) {

                        disableButton(i);
                    }
                }
            }
        }
    }
}
