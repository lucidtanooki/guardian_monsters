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
        String testString =
            "[" +
                "{\"name\":\"attNone1_selfdef\",\"sfxType\":\"none\",\"sfxIndex\":0,\"animationType\":\"none\"}," +
                "{\"name\":\"attNone2_kick\",\"sfxType\":\"hit\",\"sfxIndex\":0,\"animationType\":\"contact\"}" +
                "]";

        ArrayMap<String, AbilityMedia> abilityMediaInfos = AbilityMediaDB.readAbilityMediaFromJsonString(testString);

        AbilityMedia found  = abilityMediaInfos.get("attNone1_selfdef");
        assertEquals("attNone1_selfdef",    found.getName());
        assertEquals(0,                     found.getSfxIndex());
        assertEquals(SFXType.NONE,          found.getSfxType());
        assertEquals(AnimationType.NONE,    found.getAnimationType());

        found =  abilityMediaInfos.get("attNone2_kick");
        assertEquals("attNone2_kick",       found.getName());
        assertEquals(0,                     found.getSfxIndex());
        assertEquals(SFXType.HIT,           found.getSfxType());
        assertEquals(AnimationType.CONTACT, found.getAnimationType());
    }
}
