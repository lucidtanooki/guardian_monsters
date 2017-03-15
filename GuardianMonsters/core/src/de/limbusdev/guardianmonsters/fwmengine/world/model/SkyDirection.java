package de.limbusdev.guardianmonsters.fwmengine.world.model;

import de.limbusdev.guardianmonsters.fwmengine.world.ecs.components.PathComponent;

/**
 * SkyDirection can be used to create pathes in {@link PathComponent}
 * @author Georg Eckert 2016
 */
public enum SkyDirection {
    N, NE, NW, NSTOP,
    W, WSTOP,
    E, ESTOP,
    S, SW, SE, SSTOP,
}
