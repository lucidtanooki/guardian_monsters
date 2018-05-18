package main.java.de.limbusdev.guardianmonsters.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.monsters.SpeciesDescription;
import de.limbusdev.guardianmonsters.media.IMediaManager;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.Constant;
import de.limbusdev.guardianmonsters.ui.widgets.SimpleClickListener;

/**
 * EncycloSubMenu
 *
 * @author Georg Eckert 2017
 */

public class EncycloSubMenu extends AInventorySubMenu
{
    private Image guardianImgMeta0, guardianImgMeta1, guardianImgMeta2, guardianImgMeta3;
    private ButtonGroup<TextButton> metaFormButtonGroup;
    private TextButton metaFormButton0, metaFormButton1, metaFormButton2, metaFormButton3;
    private ImageButton next, previous;
    private int currentSpeciesID;

    private Label name, description;

    private static int leftBorder = 64 + 2;
    private static int possibleArea = (Constant.WIDTH/2-128);
    private static int row = 24;
    private static int positions[][][] = {
            /* 1 meta form  */ {{possibleArea/2, row*0+2}},
            /* 2 meta forms */ {{possibleArea*0, row*0+2}, {possibleArea*1, row*1+2}},
            /* 3 meta forms */ {{possibleArea*0, row*0+2}, {possibleArea/2, row*1+2}, {possibleArea*1, row*2+2}},
            /* 4 meta forms */ {{possibleArea*0, row*0+2}, {possibleArea/3, row*1+2}, {possibleArea/3*2, row*2+2}, {possibleArea*1, row*3+2}}
    };

    public EncycloSubMenu(Skin skin) {

        super(skin);
        layout(skin);
        init(1);

        metaFormButton0.addListener(new SimpleClickListener(() -> {

            if(metaFormButton0.isChecked()) {
                guardianImgMeta0.setVisible(true);
            } else {
                guardianImgMeta0.setVisible(false);
            }
        }));

        metaFormButton1.addListener(new SimpleClickListener(() -> {

            if(metaFormButton1.isChecked()) {
                guardianImgMeta1.setVisible(true);
            } else {
                guardianImgMeta1.setVisible(false);
            }
        }));

        metaFormButton2.addListener(new SimpleClickListener(() -> {

            if(metaFormButton2.isChecked()) {
                guardianImgMeta2.setVisible(true);
            } else {
                guardianImgMeta2.setVisible(false);
            }
        }));

        metaFormButton3.addListener(new SimpleClickListener(() -> {

            if(metaFormButton3.isChecked()) {
                guardianImgMeta3.setVisible(true);
            } else {
                guardianImgMeta3.setVisible(false);
            }
        }));

        next.addListener(new SimpleClickListener(() -> {

            try {
                if (currentSpeciesID == 300) {
                    currentSpeciesID = 0;
                }
                init(currentSpeciesID + 1);
            } catch(Exception e) {
                e.printStackTrace();
                init(1);
            }
        }));

        previous.addListener(new SimpleClickListener(() -> {

            try {
                if (currentSpeciesID == 1) {
                    currentSpeciesID = 301;
                }
                init(currentSpeciesID - 1);
            } catch(Exception e) {
                e.printStackTrace();
                init(1);
            }
        }));
    }

    @Override
    public void refresh() {

    }

    private void init(int speciesID) {

        StringBuilder nameBuilder = new StringBuilder();
        for(int i=0; i < GuardiansServiceLocator.getSpecies().getSpeciesDescription(speciesID).getMetaForms().size; i++) {

            if(i > 0) {nameBuilder.append(" > ");}
            nameBuilder.append(Services.getL18N().getLocalizedGuardianName(speciesID, i));
        }
        name.setText(nameBuilder);

        description.setText(Services.getL18N().getLocalizedGuardianDescription(speciesID));

        currentSpeciesID = speciesID;

        IMediaManager media = Services.getMedia();
        SpeciesDescription desc = GuardiansServiceLocator.getSpecies().getSpeciesDescription(speciesID);
        int metaForms = desc.getMetaForms().size;

        guardianImgMeta0.remove();
        guardianImgMeta1.remove();
        guardianImgMeta2.remove();
        guardianImgMeta3.remove();

        metaFormButton0.remove();
        metaFormButton1.remove();
        metaFormButton2.remove();
        metaFormButton3.remove();

        guardianImgMeta0.setDebug(true);
        guardianImgMeta1.setDebug(true);
        guardianImgMeta2.setDebug(true);
        guardianImgMeta3.setDebug(true);

        guardianImgMeta0.setColor(Color.GRAY);
        guardianImgMeta1.setColor(Color.GRAY);
        guardianImgMeta2.setColor(Color.GRAY);
        guardianImgMeta3.setColor(Color.GRAY);

        switch(metaForms) {

            case 4:
                guardianImgMeta3.setDrawable(new TextureRegionDrawable(media.getMonsterSprite(speciesID,3)));
                guardianImgMeta2.setDrawable(new TextureRegionDrawable(media.getMonsterSprite(speciesID,2)));
                guardianImgMeta1.setDrawable(new TextureRegionDrawable(media.getMonsterSprite(speciesID,1)));
                guardianImgMeta0.setDrawable(new TextureRegionDrawable(media.getMonsterSprite(speciesID,0)));
                guardianImgMeta3.setPosition(positions[3][3][0], positions[3][3][1], Align.bottom);
                guardianImgMeta2.setPosition(positions[3][2][0], positions[3][2][1], Align.bottom);
                guardianImgMeta1.setPosition(positions[3][1][0], positions[3][1][1], Align.bottom);
                guardianImgMeta0.setPosition(positions[3][0][0], positions[3][0][1], Align.bottom);
                addActor(guardianImgMeta3);
                addActor(guardianImgMeta2);
                addActor(guardianImgMeta1);
                addActor(guardianImgMeta0);
                break;
            case 3:
                guardianImgMeta2.setDrawable(new TextureRegionDrawable(media.getMonsterSprite(speciesID,2)));
                guardianImgMeta1.setDrawable(new TextureRegionDrawable(media.getMonsterSprite(speciesID,1)));
                guardianImgMeta0.setDrawable(new TextureRegionDrawable(media.getMonsterSprite(speciesID,0)));
                guardianImgMeta2.setPosition(positions[2][2][0]+leftBorder, positions[2][2][1], Align.bottom);
                guardianImgMeta1.setPosition(positions[2][1][0]+leftBorder, positions[2][1][1], Align.bottom);
                guardianImgMeta0.setPosition(positions[2][0][0]+leftBorder, positions[2][0][1], Align.bottom);
                addActor(guardianImgMeta2);
                addActor(guardianImgMeta1);
                addActor(guardianImgMeta0);
                break;
            case 2:
                guardianImgMeta1.setDrawable(new TextureRegionDrawable(media.getMonsterSprite(speciesID,1)));
                guardianImgMeta0.setDrawable(new TextureRegionDrawable(media.getMonsterSprite(speciesID,0)));
                guardianImgMeta1.setPosition(positions[1][1][0]+leftBorder, positions[1][1][1], Align.bottom);
                guardianImgMeta0.setPosition(positions[1][0][0]+leftBorder, positions[1][0][1], Align.bottom);
                addActor(guardianImgMeta1);
                addActor(guardianImgMeta0);
                break;
            default:
                guardianImgMeta0.setDrawable(new TextureRegionDrawable(media.getMonsterSprite(speciesID,0)));
                guardianImgMeta0.setPosition(positions[0][0][0]+leftBorder, positions[0][0][1], Align.bottom);
                addActor(guardianImgMeta0);
        }

        switch(metaForms) {
            case 4:
                addActor(metaFormButton3);
                metaFormButton3.setChecked(true);
                guardianImgMeta3.setVisible(true);
            case 3:
                addActor(metaFormButton2);
                metaFormButton2.setChecked(true);
                guardianImgMeta2.setVisible(true);
            case 2:
                addActor(metaFormButton1);
                addActor(metaFormButton0);
                metaFormButton1.setChecked(true);
                metaFormButton0.setChecked(true);
                guardianImgMeta1.setVisible(true);
                guardianImgMeta0.setVisible(true);
            default:;
        }

    }

    @Override
    protected void layout(Skin skin) {

        // Creates a page based layout
        Image bgImg = new Image(skin.getDrawable("label-bg-paper"));
        bgImg.setSize(Constant.WIDTH/2 - 4, Constant.HEIGHT - 4 - 35);
        bgImg.setPosition(Constant.WIDTH - 2,2, Align.bottomRight);

        addActor(bgImg);

        guardianImgMeta0 = new Image();
        guardianImgMeta0.setSize(128,128);
        guardianImgMeta0.setPosition(2,16, Align.bottomLeft);

        addActor(guardianImgMeta0);


        guardianImgMeta1 = new Image();
        guardianImgMeta1.setSize(128,128);
        guardianImgMeta1.setPosition(64 + 2,32, Align.bottomLeft);

        addActor(guardianImgMeta1);


        guardianImgMeta2 = new Image();
        guardianImgMeta2.setSize(128,128);
        guardianImgMeta2.setPosition(96 + 2,48, Align.bottomLeft);

        addActor(guardianImgMeta2);

        guardianImgMeta3 = new Image();
        guardianImgMeta3.setSize(128,128);
        guardianImgMeta3.setPosition(96 + 2,48, Align.bottomLeft);

        addActor(guardianImgMeta3);

        metaFormButtonGroup = new ButtonGroup<>();
        metaFormButton0 = new TextButton("1", skin, "default-toggleable");
        metaFormButton1 = new TextButton("2", skin, "default-toggleable");
        metaFormButton2 = new TextButton("3", skin, "default-toggleable");
        metaFormButton3 = new TextButton("4", skin, "default-toggleable");
        metaFormButton0.setSize(24,24);
        metaFormButton1.setSize(24,24);
        metaFormButton2.setSize(24,24);
        metaFormButton3.setSize(24,24);
        metaFormButton0.setPosition(Constant.WIDTH/2+8+28*0, 10, Align.bottomLeft);
        metaFormButton1.setPosition(Constant.WIDTH/2+8+28*1, 10, Align.bottomLeft);
        metaFormButton2.setPosition(Constant.WIDTH/2+8+28*2, 10, Align.bottomLeft);
        metaFormButton3.setPosition(Constant.WIDTH/2+8+28*3, 10, Align.bottomLeft);

        next = new ImageButton(skin , "button-next");
        previous = new ImageButton(skin , "button-previous");

        next.setPosition(Constant.WIDTH - 8, 10, Align.bottomRight);
        previous.setPosition(Constant.WIDTH - 8 - 18, 10, Align.bottomRight);

        addActor(next);
        addActor(previous);

        name = new Label("", skin, "default");
        name.setSize(96, 40);
        name.setAlignment(Align.left, Align.left);
        name.setPosition(Constant.WIDTH/2 + 8, Constant.HEIGHT - 72);
        addActor(name);

        description = new Label("", skin, "default");
        description.setWidth(Constant.WIDTH/2-16);
        description.setAlignment(Align.topLeft, Align.topLeft);
        description.setPosition(Constant.WIDTH/2 + 8, Constant.HEIGHT - 64);
        description.setWrap(true);
        addActor(description);
    }
}
