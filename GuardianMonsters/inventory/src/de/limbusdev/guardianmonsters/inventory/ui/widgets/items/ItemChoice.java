package de.limbusdev.guardianmonsters.inventory.ui.widgets.items;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import de.limbusdev.guardianmonsters.guardians.battle.BattleSystem;
import de.limbusdev.guardianmonsters.guardians.items.ChakraCrystalItem;
import de.limbusdev.guardianmonsters.guardians.items.Inventory;
import de.limbusdev.guardianmonsters.guardians.items.Item;
import de.limbusdev.guardianmonsters.guardians.items.medicine.AMedicalItem;
import de.limbusdev.guardianmonsters.guardians.items.medicine.MedicalItem;
import de.limbusdev.guardianmonsters.guardians.monsters.Team;
import de.limbusdev.guardianmonsters.inventory.ui.widgets.team.MonsterListWidget;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.Constant;
import de.limbusdev.guardianmonsters.ui.widgets.ItemListWidget;
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener;

public class ItemChoice extends Group
{
    private Team team;
    private Inventory inventory;
    private MonsterListWidget guardianList;
    private Item chosenItem;
    private BattleSystem battleSystem;
    private ItemApplicationWidget detailViewWidget;

    public ItemChoice(Inventory inventory, Team team, BattleSystem battleSystem)
    {
        Skin skin = Services.getUI().getInventorySkin();
        this.team = team;
        this.inventory = inventory;
        this.battleSystem = battleSystem;

        setSize(Constant.WIDTH, Constant.HEIGHT);
        setPosition(0, 0, Align.bottomLeft);
        Image overlay = new Image(Services.getUI().getInventorySkin().getDrawable("black-a80"));
        overlay.setSize(Constant.WIDTH, Constant.HEIGHT);
        overlay.setPosition(0, 0, Align.bottomLeft);
        addActor(overlay);

        setUp();

        ImageButton back = new ImageButton(skin, "button-back");
        back.setPosition(Constant.WIDTH-2, 2, Align.bottomRight);
        addActor(back);
        back.addListener(new SimpleClickListener(() -> remove()));

        detailViewWidget = new ItemApplicationWidget(skin, inventory, team);
        detailViewWidget.setPosition(20,2,Align.bottomLeft);
        detailViewWidget.getDelete().setVisible(false);
    }

    private void setUp ()
    {
        final MonsterListWidget.Callbacks callbacks = i ->
        {
            if(chosenItem instanceof AMedicalItem) {
                AMedicalItem med = (AMedicalItem) chosenItem;
                inventory.takeFromInventory(chosenItem);
                med.apply(team.get(i));
                if(chosenItem instanceof MedicalItem) {
                    if(((MedicalItem)chosenItem).getType() == MedicalItem.Type.REVIVE) {
                        battleSystem.revive(team.get(i));
                    }
                }
                remove();
                battleSystem.doNothing();
            }
            return false;
        };

        ItemListWidget.ClickListener clicks = item ->
        {
            chosenItem = item;
            detailViewWidget.init(item);
            addActor(detailViewWidget);
            detailViewWidget.getUse().clearListeners();
            SimpleClickListener clickListener;

            if(item instanceof MedicalItem) {

                clickListener = new SimpleClickListener(() ->
                {
                    if (guardianList != null) {
                        guardianList.remove();
                    }
                    guardianList = new MonsterListWidget(Services.getUI().getInventorySkin(), team, callbacks, chosenItem);
                    addActor(guardianList);
                    detailViewWidget.remove();
                });

            } else if (item instanceof ChakraCrystalItem) {

                clickListener = new SimpleClickListener(() ->
                {
                    if (guardianList != null) {
                        guardianList.remove();
                    }
                    detailViewWidget.remove();
                    remove();
                    inventory.takeFromInventory(chosenItem);
                    battleSystem.banWildGuardian((ChakraCrystalItem) item);
                });

            } else {
                clickListener = new SimpleClickListener(() -> {});
            }

            detailViewWidget.getUse().addListener(clickListener);
        };

        Array<Item.Category> filters = new Array<>();
        filters.add(Item.Category.MEDICINE);
        if(battleSystem.getQueue().getCombatTeamRight().countFitMembers() == 1 && battleSystem.isWildEncounter()) {
            filters.add(Item.Category.CHAKRACRYSTAL);
        }
        ItemListWidget itemList = new ItemListWidget(Services.getUI().getInventorySkin(), inventory, clicks, filters);
        itemList.setSize(140, Constant.HEIGHT);
        itemList.setPosition(Constant.WIDTH/2-32, 0, Align.bottomLeft);
        addActor(itemList);
    }
}
