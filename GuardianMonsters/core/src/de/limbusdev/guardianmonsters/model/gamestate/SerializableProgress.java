package de.limbusdev.guardianmonsters.model.gamestate;

/**
 * SerializableProgress
 *
 * @author Georg Eckert 2017
 */

public class SerializableProgress {

    public int maxBattleTeamSize;

    public SerializableProgress() {}

    public SerializableProgress(int maxBattleTeamSize) {
        this.maxBattleTeamSize = maxBattleTeamSize;
    }
}
