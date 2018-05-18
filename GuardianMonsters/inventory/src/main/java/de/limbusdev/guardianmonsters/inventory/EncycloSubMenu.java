package main.java.de.limbusdev.guardianmonsters.inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import de.limbusdev.guardianmonsters.guardians.GuardiansServiceLocator;
import de.limbusdev.guardianmonsters.guardians.monsters.SpeciesDescription;
import de.limbusdev.guardianmonsters.media.IMediaManager;
import de.limbusdev.guardianmonsters.services.Services;
import de.limbusdev.guardianmonsters.ui.Constant;

/**
 * EncycloSubMenu
 *
 * @author Georg Eckert 2017
 */

public class EncycloSubMenu extends AInventorySubMenu
{
    private Image guardianImgMeta0, guardianImgMeta1, guardianImgMeta2, guardianImgMeta3;
    private static int leftBorder = 64 + 2;
    private static int possibleArea = (Constant.WIDTH/2-128);
    private static int row = 24;
    private static int positions[][][] = {
            /* 1 meta form  */ {{possibleArea/2, row*0}},
            /* 2 meta forms */ {{possibleArea*1, row*0}, {possibleArea*1, row*1}},
            /* 3 meta forms */ {{possibleArea*0, row*0}, {possibleArea/2, row*1}, {possibleArea*1, row*2}},
            /* 4 meta forms */ {{possibleArea*0, row*0}, {possibleArea/3, row*1}, {possibleArea/3*2, row*2}, {possibleArea*1, row*3}}
    };

    public EncycloSubMenu(Skin skin) {

        super(skin);
        layout(skin);
        init(1);
    }

    @Override
    public void refresh() {

    }

    private void init(int speciesID) {

        IMediaManager media = Services.getMedia();
        SpeciesDescription desc = GuardiansServiceLocator.getSpecies().getSpeciesDescription(speciesID);
        int metaForms = desc.getMetaForms().size;

        guardianImgMeta0.remove();
        guardianImgMeta1.remove();
        guardianImgMeta2.remove();
        guardianImgMeta3.remove();

        guardianImgMeta0.setDebug(true);
        guardianImgMeta1.setDebug(true);
        guardianImgMeta2.setDebug(true);
        guardianImgMeta3.setDebug(true);

        guardianImgMeta0.setColor(Color.BLACK);
        guardianImgMeta1.setColor(Color.BLACK);
        guardianImgMeta2.setColor(Color.BLACK);
        guardianImgMeta3.setColor(Color.BLACK);

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

    }
}
