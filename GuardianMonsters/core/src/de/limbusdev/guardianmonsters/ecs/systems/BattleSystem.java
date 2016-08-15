package de.limbusdev.guardianmonsters.ecs.systems;

import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.model.MonsterInBattle;
import de.limbusdev.guardianmonsters.screens.BattleHUD;

/**
 * Created by georg on 15.08.16.
 */
public class BattleSystem {

    private BattleHUD battleUI;

    private Array<MonsterInBattle> heroTeam, oppoTeam;
    private Array<MonsterInBattle> battleQueue;

    private int chosenMember, chosenTarget;

    private boolean allHeroKO, allOppoKO;


    public BattleSystem(BattleHUD hud) {
        battleUI = hud;

    }

}
