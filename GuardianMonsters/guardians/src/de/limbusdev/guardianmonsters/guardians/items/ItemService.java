package de.limbusdev.guardianmonsters.guardians.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;

import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment;


/**
 * ItemDB provides information about all items. Items are created from data/items.xml
 *
 * @author Georg Eckert 2017
 */
public class ItemService implements IItemService
{
    private static ItemService instance;
    private static ArrayMap<String, Item> items;

    private ItemService(String xmlItems)
    {
        items = new ArrayMap<>();

        XmlReader xmlReader = new XmlReader();
        XmlReader.Element element;
        element = xmlReader.parse(xmlItems);

        for(int i=0; i<element.getChildCount(); i++) {
            Item item = XMLItemParser.parseXmlItem(element.getChild(i));
            if(!items.containsKey(item.getName())) {
                items.put(item.getName(), item);
            }
        }
    }


    // ............................................................................. GETTER & SETTER
    public static ItemService getInstance(String xmlItems)
    {
        if(instance == null) {
            instance = new ItemService(xmlItems);
        }
        return instance;
    }

    public static ItemService getInstanceFromFile(String xmlItemsFilePath)
    {
        String xml = Gdx.files.internal(xmlItemsFilePath).readString();
        return getInstance(xml);
    }

    public Item getItem(String name)
    {
        return items.get(name);
    }

    @Override
    public Equipment getEquipment(String name)
    {
        Equipment equipment;
        Item item = getItem(name);
        if(item instanceof Equipment) {
            equipment = (Equipment) item;
        } else {
            throw new IllegalArgumentException("Given name is not an Equipment Item.");
        }
        return equipment;
    }

    @Override
    public void destroy()
    {
        instance = null;
    }

}
