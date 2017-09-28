import com.badlogic.gdx.utils.ArrayMap;

import org.junit.Test;

import de.limbusdev.guardianmonsters.battle.AbilityMedia;
import de.limbusdev.guardianmonsters.battle.AbilityMediaDB;
import de.limbusdev.guardianmonsters.battle.AnimationType;
import de.limbusdev.guardianmonsters.media.SFXType;

import static org.junit.Assert.assertEquals;

/**
 * ModuleBattleJUnitTest
 *
 * @author Georg Eckert 2017
 */

public class ModuleBattleJUnitTest
{
    @Test
    public void abilityMediaParsingTest()
    {
        String testString = "[\n" +
            "  {\n" +
            "    \"name\": \"attEarth1_dirt\",\n" +
            "    \"sfxType\": \"hit\",\n" +
            "    \"sfxIndex\": 0,\n" +
            "    \"animationType\": \"moving_hor\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"name\": \"attEarth2_mud\",\n" +
            "    \"sfxType\": \"hit\",\n" +
            "    \"sfxIndex\": 0,\n" +
            "    \"animationType\": \"moving_hor\"\n" +
            "  }\n" +
            "]";

        ArrayMap<String, AbilityMedia> abilityMediaInfos = AbilityMediaDB.readAbilityMediaFromJsonString(testString);

        System.out.println(abilityMediaInfos.get(abilityMediaInfos.firstKey()));

        AbilityMedia wanted = new AbilityMedia("attEarth1_dirt", 0, SFXType.HIT, AnimationType.CONTACT);
        assertEquals(abilityMediaInfos.get("attEarth1_dirt"), wanted);
        wanted = new AbilityMedia("attEarth2_mud", 0, SFXType.HIT, AnimationType.MOVING_HOR);
        assertEquals(abilityMediaInfos.get("attEarth2_mud"), wanted);
    }
}
