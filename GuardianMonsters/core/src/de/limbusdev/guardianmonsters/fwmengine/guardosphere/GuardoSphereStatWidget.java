package de.limbusdev.guardianmonsters.fwmengine.guardosphere;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ArrayMap;

import de.limbusdev.guardianmonsters.guardians.Element;
import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.monsters.AGuardian;
import de.limbusdev.guardianmonsters.guardians.monsters.ISpeciesDescriptionService;
import de.limbusdev.guardianmonsters.services.Services;

public class GuardoSphereStatWidget extends Group {

    private ArrayMap<String,Label> valueLabels;
    private HorizontalGroup elementGroup;
    private Group equipmentGroup;
    private Skin skin;

    private static final int WIDTH = 152;
    private static final int HEIGHT= 180;

    public GuardoSphereStatWidget(Skin skin) {

        this.skin = skin;

        setSize(WIDTH, HEIGHT);
        Image background = new Image(skin.getDrawable("guardosphere-frame"));
        background.setSize(WIDTH,HEIGHT);
        background.setPosition(0,0, Align.bottomLeft);
        addActor(background);

        int offX = 16;
        int offY = 200-16;
        int gap = 18;

        valueLabels = new ArrayMap<>();
        Label value;
        String[] labels = {"hp", "mp", "exp", "pstr", "pdef", "mstr", "mdef", "speed"};
        Image key;

        for(int i=0; i<labels.length; i++) {


            key = new Image(skin.getDrawable("stats-symbol-" + labels[i]));
            key.setSize(16,16);
            key.setPosition(offX, offY-gap*(i+1), Align.topLeft);
            addActor(key);

            value = new Label("0", skin, "white");
            value.setPosition(offX+20, offY-gap*(i+1), Align.topLeft);
            addActor(value);
            valueLabels.put(labels[i], value);
        }

        for(int bg=0; bg<4; bg++) {

            Label bgl = new Label("", skin, "sphere");
            bgl.setSize(36,36);
            bgl.setPosition(100,offY-14-bg*38, Align.topLeft);
            addActor(bgl);
        }

        elementGroup = new HorizontalGroup();
        elementGroup.setSize(140,20);
        elementGroup.setPosition(6,6,Align.bottomLeft);
        addActor(elementGroup);
        equipmentGroup = new Group();
        addActor(equipmentGroup);

    }


    public void init(AGuardian m)
    {
        ISpeciesDescriptionService species = GuardiansServiceLocator.getSpecies();
        valueLabels.get("hp").setText(m.getIndividualStatistics().getHP() + "/" + m.getIndividualStatistics().getHPmax());
        valueLabels.get("mp").setText(m.getIndividualStatistics().getMP() + "/" + m.getIndividualStatistics().getMPmax());
        valueLabels.get("exp").setText(m.getIndividualStatistics().getEXP() + "/" + (m.getIndividualStatistics().getEXPtoNextLevel() + m.getIndividualStatistics().getEXP()));
        valueLabels.get("pstr").setText(Integer.toString(m.getIndividualStatistics().getPStrMax()));
        valueLabels.get("pdef").setText(Integer.toString(m.getIndividualStatistics().getPDefMax()));
        valueLabels.get("mstr").setText(Integer.toString(m.getIndividualStatistics().getMStrMax()));
        valueLabels.get("mdef").setText(Integer.toString(m.getIndividualStatistics().getMDefMax()));
        valueLabels.get("speed").setText(Integer.toString(m.getIndividualStatistics().getSpeedMax()));

        elementGroup.clear();
        for(Element e : m.getSpeciesDescription().getElements(0)) { // TODO currentForm

            String elem = e.toString().toLowerCase();
            String elemName = Services.getL18N().Elements().get("element_" + elem);
            elemName = elemName.length() < 7 ? elemName : elemName.substring(0,6);
            Label l = new Label(elemName, skin, "elem-" + elem);
            elementGroup.addActor(l);
        }

        equipmentGroup.clear();
        if(m.getIndividualStatistics().hasHeadEquipped()) {
            Image img = new Image(skin.getDrawable(m.getIndividualStatistics().getHead().getName()));
            img.setSize(32,32);
            img.setPosition(102,178-2,Align.topLeft);
            equipmentGroup.addActor(img);
        }
        if(m.getIndividualStatistics().hasHandsEquipped()) {
            Image img = new Image(skin.getDrawable(m.getIndividualStatistics().getHands().getName()));
            img.setSize(32,32);
            img.setPosition(102,178-2-38,Align.topLeft);
            equipmentGroup.addActor(img);
        }
        if(m.getIndividualStatistics().hasBodyEquipped()) {
            Image img = new Image(skin.getDrawable(m.getIndividualStatistics().getBody().getName()));
            img.setSize(32,32);
            img.setPosition(102,178-2-38*2,Align.topLeft);
            equipmentGroup.addActor(img);
        }
        if(m.getIndividualStatistics().hasFeetEquipped()) {
            Image img = new Image(skin.getDrawable(m.getIndividualStatistics().getFeet().getName()));
            img.setSize(32,32);
            img.setPosition(102,178-2-38*3,Align.topLeft);
            equipmentGroup.addActor(img);
        }
    }
}
