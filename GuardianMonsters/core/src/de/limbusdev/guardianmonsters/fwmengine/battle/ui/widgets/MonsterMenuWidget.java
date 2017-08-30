package de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleQueue;
import de.limbusdev.guardianmonsters.fwmengine.battle.control.BattleSystem;
import de.limbusdev.guardianmonsters.fwmengine.battle.model.CombatTeam;
import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;
import de.limbusdev.guardianmonsters.services.Services;


/**
 * @author Georg Eckert 2017
 */
public class MonsterMenuWidget extends SevenButtonsWidget {

    private static int order[] = {0,1,2,3,4,5,6};

    public MonsterMenuWidget(Skin skin, Callbacks callbacks) {
        super(skin, callbacks, order);
    }

    public void init(BattleSystem battleSystem, boolean side) {

        BattleQueue queue = battleSystem.getQueue();
        Team team = side ? queue.getLeft() : queue.getRight();
        CombatTeam combatTeam = side ? queue.getCombatTeamLeft() : queue.getCombatTeamRight();

        // Set all buttons inactive
        for(Integer i : getButtons().keys()) {
            disableButton(i);
        }

        for(int key : team.keys()) {
            if(key > 6) break;
            AGuardian m = team.get(key);
            TextButton bwi = new BattleHUDTextButton(Services.getL18N().getLocalizedGuardianName(m), skin, key, Element.AIR);

            replaceButton(bwi,key);

            if(m.getIndividualStatistics().isFit() && !combatTeam.containsValue(m,false)) {
                enableButton(key);
            }
        }

    }

}
