package de.limbusdev.guardianmonsters.fwmengine.battleresult;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.fwmengine.battle.model.BattleResult;
import de.limbusdev.guardianmonsters.fwmengine.battle.ui.widgets.LevelUpWidget;
import de.limbusdev.guardianmonsters.fwmengine.ui.AHUD;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.ISpeciesDescriptionService;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;
import de.limbusdev.guardianmonsters.services.Services;

import static de.limbusdev.guardianmonsters.Constant.HEIGHT;
import static de.limbusdev.guardianmonsters.Constant.WIDTH;

/**
 * BattleResultHUD
 *
 * @author Georg Eckert 2017
 */

public class BattleResultHUD extends AHUD {

    private Team team;
    private BattleResult result;
    private Table table;
    private Button apply;
    private Button next;
    private Array<AGuardian> reachedNextLevel;
    private Group group;

    public BattleResultHUD(Skin skin, Team team, BattleResult result) {
        super(skin);
        this.team = team;
        this.result = result;
        constructLayout();
        constructMonsterTable(team, result);
        Gdx.input.setInputProcessor(stage);
    }

    private void constructMonsterTable(final Team team, final BattleResult result)
    {
        ISpeciesDescriptionService species = GuardiansServiceLocator.getSpecies();
        table.clear();
        for(int key : team.keys()) {
            AGuardian guardian = team.get(key);
            Image face = Services.getMedia().getMonsterFace(guardian.getSpeciesID());
            table.add(face).left();
            Label name = new Label(Services.getL18N().Guardians().get(species.getCommonNameById(guardian.getSpeciesID())), skin, "default");
            table.add(name).left();
            Image expKey = new Image(skin.getDrawable("symbol-exp"));
            table.add(expKey).left();
            Label exp = new Label(Integer.toString(result.getGainedEXP(guardian)), skin, "default");
            table.add(exp).width(48).left();
            Image lvlUpKey = new Image(skin.getDrawable("symbol-levelup"));
            table.add(lvlUpKey).left();
            Label lvlUp = new Label(Integer.toString(guardian.getIndividualStatistics().getEXPtoNextLevel()), skin, "default");
            table.add(lvlUp).width(96).left();
            table.row().space(4);
        }
    }

    private void constructLayout() {
        Container container = new Container();
        container.setBackground(skin.getDrawable("label-bg-paper"));
        container.setSize(WIDTH-2, HEIGHT-2);
        container.setPosition(1, 1, Align.bottomLeft);
        stage.addActor(container);

        group = new Group();
        group.setSize(WIDTH-8,HEIGHT-8);
        group.setPosition(4,4,Align.bottomLeft);
        stage.addActor(group);

        Label heading = new Label(Services.getL18N().Battle().get("results"), skin, "default");
        heading.setAlignment(Align.topLeft, Align.topLeft);
        heading.setPosition(4, group.getHeight()-4, Align.topLeft);
        group.addActor(heading);

        table = new Table();
        table.align(Align.topLeft);
        table.setSize(group.getWidth()*3/4,group.getHeight()*4/5);
        table.setPosition(4,8,Align.bottomLeft);
        group.addActor(table);

        apply = new TextButton(Services.getL18N().General().get("apply"), skin, "default");
        apply.setSize(72,32);
        apply.setPosition(group.getWidth()-4,4,Align.bottomRight);
        group.addActor(apply);

        apply.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event,x,y);
                    System.out.println("BattleResultScreen: APPLY pressed");
                    reachedNextLevel = result.applyGainedEXPtoAll();
                    BattleResult result = new BattleResult(team,new Array<Item>());
                    constructMonsterTable(team,result);
                    apply.remove();
                    group.addActor(next);
                    System.out.printf(Boolean.toString(apply.isDisabled()));
                }
            }
        );

        next = new TextButton(Services.getL18N().General().get("next"), skin, "default");
        next.setSize(72,32);
        next.setPosition(group.getWidth()-4,4,Align.bottomRight);

        next.addListener(
            new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event,x,y);
                    System.out.println("BattleResultScreen: NEXT pressed");
                    if(reachedNextLevel.size > 0) {
                        LevelUpWidget lvlUpWidget = new LevelUpWidget(skin, reachedNextLevel.first());
                        stage.addActor(lvlUpWidget);
                        reachedNextLevel.removeIndex(0);
                    } else {
                        Services.getScreenManager().popScreen();
                    }
                }
            }
        );

    }

    @Override
    protected void reset() {

    }

    @Override
    public void show() {

    }
}