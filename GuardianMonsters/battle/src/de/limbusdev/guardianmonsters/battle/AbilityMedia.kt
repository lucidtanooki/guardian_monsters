package de.limbusdev.guardianmonsters.battle

import de.limbusdev.guardianmonsters.guardians.abilities.Ability
import de.limbusdev.guardianmonsters.media.SFXType

/**
 * AbilityMedia
 *
 * Holds information about which animation and sound to use for an [Ability].
 *
 * @author Georg Eckert 2017
 */
class AbilityMedia
(
        val name: String,
        val sfxIndex: Int,
        val sfxType: SFXType,
        val animationType: AnimationType
) {
    override fun toString(): String
    {
        return "Media for Ability $name: SFX $sfxIndex of type $sfxType, Anim: $animationType"
    }

    override fun equals(other: Any?): Boolean
    {
        if(this === other)                  return true
        if(javaClass != other?.javaClass)   return false

        other as AbilityMedia

        if(name != other.name)                      return false
        if(sfxIndex != other.sfxIndex)              return false
        if(sfxType != other.sfxType)                return false
        if(animationType != other.animationType)    return false

        return true
    }

    override fun hashCode(): Int
    {
        var result = name.hashCode()
        result = 31 * result + sfxIndex
        result = 31 * result + sfxType.hashCode()
        result = 31 * result + animationType.hashCode()
        return result
    }
}
