package de.limbusdev.guardianmonsters.battle;

import de.limbusdev.guardianmonsters.media.SFXType;

/**
 * AbilityMedia
 *
 * Holds information about which animation and sound to use for an {@link de.limbusdev.guardianmonsters.guardians.abilities.Ability}.
 *
 * @author Georg Eckert 2017
 */
public class AbilityMedia
{
    private final String name;
    private final int sfxIndex;
    private final SFXType sfxType;
    private final AnimationType animationType;

    public AbilityMedia(String name, int sfxIndex, SFXType sfxType, AnimationType animationType)
    {
        this.name = name;
        this.sfxIndex = sfxIndex;
        this.sfxType = sfxType;
        this.animationType = animationType;
    }

    public String getName()
    {
        return name;
    }

    public int getSfxIndex()
    {
        return sfxIndex;
    }

    public SFXType getSfxType()
    {
        return sfxType;
    }

    public AnimationType getAnimationType()
    {
        return animationType;
    }

    @Override
    public String toString()
    {
        return "Media for Ability " + name + ": SFX " + sfxIndex + " of type " + sfxType + ", Anim: " + animationType;
    }

}
