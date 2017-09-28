package de.limbusdev.guardianmonsters.guardians.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import java.io.IOException;


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
            Item item = de.limbusdev.guardianmonsters.guardians.items.XMLItemParser.parseXmlItem(element.getChild(i));
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
