package de.limbusdev.guardianmonsters.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;

import de.limbusdev.guardianmonsters.model.items.BodyEquipment;
import de.limbusdev.guardianmonsters.model.items.BodyPart;
import de.limbusdev.guardianmonsters.model.items.Equipment;
import de.limbusdev.guardianmonsters.model.items.FootEquipment;
import de.limbusdev.guardianmonsters.model.items.HandEquipment;
import de.limbusdev.guardianmonsters.model.items.HeadEquipment;
import de.limbusdev.guardianmonsters.model.items.Item;
import de.limbusdev.guardianmonsters.model.items.KeyItem;
import de.limbusdev.guardianmonsters.model.items.MedicalItem;
import de.limbusdev.guardianmonsters.utils.XMLItemParser;

/**
 * ItemDB provides information about all items. Items are created from data/items.xml
 *
 * @author Georg Eckert 2017
 */

public class ItemDB {

    private static ItemDB instance;
    private ArrayMap<String, Item> items;

    private ItemDB() {
        items = new ArrayMap<>();

        FileHandle handle = Gdx.files.internal("data/items.xml");
        XmlReader xmlReader = new XmlReader();
        XmlReader.Element element;
        try {
            element = xmlReader.parse(handle);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        for(int i=0; i<element.getChildCount(); i++) {
            Item item = XMLItemParser.parseXmlItem(element.getChild(i));
            if(!items.containsKey(item.getName())) {
                items.put(item.getName(), item);
            }
        }
    }


    // ............................................................................. GETTER & SETTER
    public static ItemDB getInstance() {
        if(instance == null) {
            instance = new ItemDB();
        }
        return instance;
    }

    public static Item getItem(String name) {
        ItemDB db = getInstance();
        return db.items.get(name);
    }

}
