import com.badlogic.gdx.utils.ArrayMap

import org.junit.Test

import de.limbusdev.guardianmonsters.battle.AbilityMedia
import de.limbusdev.guardianmonsters.battle.AbilityMediaDB
import de.limbusdev.guardianmonsters.battle.AnimationType
import de.limbusdev.guardianmonsters.media.SFXType

import org.junit.Assert.assertEquals

/**
 * ModuleBattleJUnitTest
 *
 * @author Georg Eckert 2017
 */

class ModuleBattleJUnitTest {
    @Test
    fun abilityMediaParsingTest() {
        val testString = "[" +
                "{\"name\":\"attNone1_selfdef\",\"sfxType\":\"none\",\"sfxIndex\":0,\"animationType\":\"none\"}," +
                "{\"name\":\"attNone2_kick\",\"sfxType\":\"hit\",\"sfxIndex\":0,\"animationType\":\"contact\"}" +
                "]"

        val abilityMediaInfos = AbilityMediaDB.readAbilityMediaFromJsonString(testString)

        var found = abilityMediaInfos.get("attNone1_selfdef")
        assertEquals("attNone1_selfdef", found.name)
        assertEquals(0, found.sfxIndex.toLong())
        assertEquals(SFXType.NONE, found.sfxType)
        assertEquals(AnimationType.NONE, found.animationType)

        found = abilityMediaInfos.get("attNone2_kick")
        assertEquals("attNone2_kick", found.name)
        assertEquals(0, found.sfxIndex.toLong())
        assertEquals(SFXType.HIT, found.sfxType)
        assertEquals(AnimationType.CONTACT, found.animationType)
    }
}
