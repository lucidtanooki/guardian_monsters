package de.limbusdev.guardianmonsters.fwmengine.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import de.limbusdev.guardianmonsters.ecs.components.Components;
import de.limbusdev.guardianmonsters.ecs.components.PositionComponent;
import de.limbusdev.guardianmonsters.ecs.components.SaveGameComponent;
import de.limbusdev.guardianmonsters.ecs.components.TeamComponent;
import de.limbusdev.guardianmonsters.ecs.systems.GameArea;
import de.limbusdev.guardianmonsters.model.Monster;
import de.limbusdev.guardianmonsters.utils.GameState;


/**
 * Created by georg on 03.12.15.
 */
public class SaveGameManager extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> savableEntities;
    private GameState gameState;
    private GameArea gameArea;
    /* ........................................................................... CONSTRUCTOR .. */

    public SaveGameManager(GameArea gameArea) {
        super();
        this.gameArea = gameArea;
    }

    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        savableEntities = engine.getEntitiesFor(Family.all(
                SaveGameComponent.class,
                PositionComponent.class,
                TeamComponent.class).get());
        gameState = Components.saveGame.get(savableEntities.first()).gameState;
        gameState.map = this.gameArea.areaID;
        gameState.team = Components.team.get(savableEntities.first()).monsters;
    }

    public void update(float deltaTime) {
        for (Entity entity : savableEntities) {
            PositionComponent position = Components.getPositionComponent(entity);
            SaveGameComponent saveGame = Components.saveGame.get(entity);
            saveGame.gameState.x = position.x;
            saveGame.gameState.y = position.y;
            saveGame.gameState.gridx = position.onGrid.x;
            saveGame.gameState.gridy = position.onGrid.y;
        }
    }


    public static void saveGame(GameState gameState) {
        Preferences prefs = Gdx.app.getPreferences("saveGame");
        prefs.putInteger("posX", gameState.x);
        prefs.putInteger("posY", gameState.y);
        prefs.putInteger("map", gameState.map);
        prefs.flush();
    }

    public void saveGame() {
        // Preferences
        Preferences prefs = Gdx.app.getPreferences("saveGame");
        prefs.putInteger("posX", gameState.x);
        prefs.putInteger("posY", gameState.y);
        prefs.putInteger("map", gameState.map);
        prefs.flush();

        // Save Game
        FileHandle fh = Gdx.files.local( "data/game-progress.json" );
        Json json = new Json();
        System.out.println(json.prettyPrint(gameState));
        if(gameState != null)
            json.toJson(gameState, GameState.class, fh);
    }

    public static GameState loadSaveGame() {
        GameState gameState = new GameState(0,0,0);
        Preferences prefs = Gdx.app.getPreferences("saveGame");

        Json json = new Json();
        json.addClassTag("Monster", Monster.class);
        FileHandle fh = Gdx.files.local( "data/game-progress.json" );
        if(fh.exists()) {
            GameState gs = json.fromJson(GameState.class, Array.class, fh);
            System.out.println(gs.map);

            gameState.team = gs.team;
            gameState.map = gs.map;
            gameState.x = gs.x;
            gameState.y = gs.y;
            gameState.gridx = gs.gridx;
            gameState.gridy = gs.gridy;
        }

        return gameState;
    }

    public static boolean doesGameSaveExist() {
        return Gdx.files.local( "data/game-progress.json" ).exists();
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
