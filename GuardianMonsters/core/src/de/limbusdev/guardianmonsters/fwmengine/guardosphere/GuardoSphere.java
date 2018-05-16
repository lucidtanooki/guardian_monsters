package de.limbusdev.guardianmonsters.fwmengine.guardosphere;

import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Guardian;

/**
 * GuardoSphere
 *
 * @author Georg Eckert 2017
 */

public class GuardoSphere extends ArrayMap<Integer,AGuardian>
{
    public GuardoSphere() {
        super(300);
    }
}
