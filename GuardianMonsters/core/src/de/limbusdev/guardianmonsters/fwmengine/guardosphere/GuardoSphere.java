package de.limbusdev.guardianmonsters.fwmengine.guardosphere;

import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.model.monsters.Monster;

/**
 * GuardoSphere
 *
 * @author Georg Eckert 2017
 */

public class GuardoSphere extends ArrayMap<Integer,Monster> {
    public GuardoSphere() {
        super(280);
    }
}
