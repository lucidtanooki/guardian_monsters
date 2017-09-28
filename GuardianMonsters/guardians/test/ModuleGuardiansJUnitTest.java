import com.badlogic.gdx.utils.ArrayMap;

import org.junit.Test;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.abilities.Ability;
import de.limbusdev.guardianmonsters.guardians.abilities.AbilityDB;

import static org.junit.Assert.assertEquals;

/**
 * GuardiansJUnitTestAbilities
 *
 * @author Georg Eckert 2017
 */

public class ModuleGuardiansJUnitTest
{
    @Test
    public void abilityParsingTest()
    {
        String testJson = "[\n" +
            "  {\n" +
            "    \"ID\": 1,\n" +
            "    \"element\": \"none\",\n" +
            "    \"name\": \"attNone1_selfdef\",\n" +
            "    \"damage\": 0,\n" +
            "    \"MPcost\": 0,\n" +
            "    \"damageType\": \"physical\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"ID\": 2,\n" +
            "    \"element\": \"earth\",\n" +
            "    \"name\": \"attNone2_kick\",\n" +
            "    \"damage\": 50,\n" +
            "    \"MPcost\": 10,\n" +
            "    \"damageType\": \"magical\"\n" +
            "  }" +
            "]";

        ArrayMap<Integer,Ability> abilities = AbilityDB.readAbilitiesFromJsonString(testJson);

        Ability ability = abilities.get(1);

        assertEquals(ability.ID, 1);
        assertEquals(ability.element, Element.NONE);
        assertEquals(ability.name, "attNone1_selfdef");
        assertEquals(ability.MPcost, 0);
        assertEquals(ability.damageType, Ability.DamageType.PHYSICAL);

        ability = abilities.get(2);

        assertEquals(ability.ID, 2);
        assertEquals(ability.element, Element.EARTH);
        assertEquals(ability.name, "attNone2_kick");
        assertEquals(ability.MPcost, 10);
        assertEquals(ability.damageType, Ability.DamageType.MAGICAL);
    }
}
