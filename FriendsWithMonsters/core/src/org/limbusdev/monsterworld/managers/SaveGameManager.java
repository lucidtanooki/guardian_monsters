package org.limbusdev.monsterworld.managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import org.limbusdev.monsterworld.ecs.components.ColliderComponent;
import org.limbusdev.monsterworld.ecs.components.ComponentRetriever;
import org.limbusdev.monsterworld.ecs.components.ConversationComponent;
import org.limbusdev.monsterworld.ecs.components.InputComponent;
import org.limbusdev.monsterworld.ecs.components.PositionComponent;
import org.limbusdev.monsterworld.ecs.components.SaveGameComponent;
import org.limbusdev.monsterworld.ecs.components.TitleComponent;
import org.limbusdev.monsterworld.ecs.systems.OutdoorGameArea;
import org.limbusdev.monsterworld.utils.GameState;


/**
 * Created by georg on 03.12.15.
 */
public class SaveGameManager extends EntitySystem {
    /* ............................................................................ ATTRIBUTES .. */
    private ImmutableArray<Entity> savableEntities;
    private GameState gameState;
    private OutdoorGameArea gameArea;
    /* ........................................................................... CONSTRUCTOR .. */

    public SaveGameManager(OutdoorGameArea gameArea) {
        super();
        this.gameArea = gameArea;
    }

    /* ............................................................................... METHODS .. */
    public void addedToEngine(Engine engine) {
        savableEntities = engine.getEntitiesFor(Family.all(
                SaveGameComponent.class,
                PositionComponent.class).get());
        gameState = ComponentRetriever.savCompMap.get(savableEntities.first()).gameState;
        gameState.map = this.gameArea.areaID;
    }

    public void update(float deltaTime) {
        for (Entity entity : savableEntities) {
            PositionComponent position = ComponentRetriever.getPositionComponent(entity);
            SaveGameComponent saveGame = ComponentRetriever.savCompMap.get(entity);
            saveGame.gameState.x = position.nextX;
            saveGame.gameState.y = position.nextY;
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
        Preferences prefs = Gdx.app.getPreferences("saveGame");
        prefs.putInteger("posX", gameState.x);
        prefs.putInteger("posY", gameState.y);
        prefs.putInteger("map", gameState.map);
        prefs.flush();
    }

    public static GameState loadSaveGame() {
        GameState gameState = new GameState(0,0,0);
        Preferences prefs = Gdx.app.getPreferences("saveGame");
        if(
                prefs.contains("posX")
                && prefs.contains("posY")
                && prefs.contains("map")) {
            gameState.x = prefs.getInteger("posX");
            gameState.y = prefs.getInteger("posY");
            gameState.map = prefs.getInteger("map");
        }

        return gameState;
    }

    public static boolean doesSaveGameExist() {
        Preferences prefs = Gdx.app.getPreferences("saveGame");
        if(prefs.contains("posX")
                && prefs.contains("posY")
                && prefs.contains("map"))
            return true;
        else
            return false;
    }
    /* ..................................................................... GETTERS & SETTERS .. */
}
