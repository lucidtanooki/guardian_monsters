package de.limbusdev.guardianmonsters.guardians.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.XmlReader;
import com.github.czyzby.autumn.annotation.Component;


/**
 * ItemDB provides information about all items. Items are created from data/items.xml
 *
 * @author Georg Eckert 2017
 */
@Component
public class ItemService implements IItemService
{
    private static ItemService instance;
    private ArrayMap<String, Item> items;

    public ItemService() {}

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

}
