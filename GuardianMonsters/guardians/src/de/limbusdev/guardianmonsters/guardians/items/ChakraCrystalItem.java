package de.limbusdev.guardianmonsters.guardians.items;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;

public class ChakraCrystalItem extends Item
{
    private Element element;

    public ChakraCrystalItem(String name, String element)
    {
        super(name, Category.CHAKRACRYSTAL);
        try {
            this.element = Element.valueOf(element.toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
            this.element = Element.NONE;
        }
    }

    /**
     * calculates the chance to ban a guardian with this item
     * @param guardian
     * @return
     */
    public float chance(AGuardian guardian)
    {
        float chance = 0f;
        float elementFactor;

        if(guardian.getSpeciesDescription().getElements(
                guardian.getAbilityGraph().getCurrentForm()
        ).contains(element, false)) {
            elementFactor = 2f;
        } else {
            elementFactor = 1f;
        }

        chance = (1f - guardian.getIndividualStatistics().getHpFraction()/elementFactor/100f/2f - guardian.getIndividualStatistics().getLevel()/100f);

        if(chance > 1f) {
            chance = 1f;
        }

        return chance;
    }
}
