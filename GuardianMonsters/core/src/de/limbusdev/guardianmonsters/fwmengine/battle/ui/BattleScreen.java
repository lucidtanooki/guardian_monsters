package de.limbusdev.guardianmonsters.fwmengine.battle.ui;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import de.limbusdev.guardianmonsters.assets.paths.AssetPath;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.fwmengine.ui.AScreen;
import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;

/**
 * @author Georg Eckert 2016
 */
public class BattleScreen extends AScreen {
    /* ............................................................................ ATTRIBUTES .. */
    private TextureRegion background;
    private boolean initialized=false;


    /* ........................................................................... CONSTRUCTOR .. */
    public BattleScreen(Inventory inventory) {
        super(new BattleHUD(inventory));
        setBackground(0);
    }


    /* ............................................................................... METHODS .. */
    /**
     * BattleScreen must get initialized before being shown
     * @param team
     * @param opponentTeam
     */
    public void init (Team team, Team opponentTeam) {
        this.initialized = true;
        getBattleHUD().init(team, opponentTeam);
    }

    @Override
    public void show() {
        super.show();
        if(!initialized) {
            throw new ExceptionInInitializerError("BattleScreen must get initialized before drawn.");
        }
        Services.getAudio().playLoopMusic(AssetPath.Audio.Music.BG_BATTLE[0]);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
    }

    @Override
    public void hide() {
        super.hide();
        initialized = false;
        Services.getAudio().stopMusic(AssetPath.Audio.Music.BG_BATTLE[0]);
    }

    private BattleHUD getBattleHUD() {
        return ((BattleHUD)super.getHud());
    }
}
