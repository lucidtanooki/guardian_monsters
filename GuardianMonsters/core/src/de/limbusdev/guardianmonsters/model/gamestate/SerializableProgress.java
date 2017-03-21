package de.limbusdev.guardianmonsters.model.gamestate;

/**
 * SerializableProgress
 *
 * @author Georg Eckert 2017
 */

public class SerializableProgress {

    public int maxTeamSize;

    public SerializableProgress() {}

    public SerializableProgress(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
    }
}
