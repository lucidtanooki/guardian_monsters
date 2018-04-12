package de.limbusdev.guardianmonsters.guardians.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

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

    /**
     * Takes several JsonStrings and parses items from them:
     *      itemsEquipment.json
     *      itemsKey.json
     *      itemsMedicine.json
     *
     * @param jsonItemStrings
     */
    private ItemService(ArrayMap<String,String> jsonItemStrings)
    {
        items = new ArrayMap<>();

        JsonReader jsonReader = new JsonReader();
        JsonValue jsonValue;

        for(String jsonString : jsonItemStrings.values())
        {
            jsonValue = jsonReader.parse(jsonString);
            jsonValue = jsonValue.get("items");
            for(JsonValue value : jsonValue)
            {
                Item item = JSONItemParser.parseJsonItem(value);
                if(!items.containsKey(item.getName())) {
                    items.put(item.getName(), item);
                }
            }
        }
    }


    // ............................................................................. GETTER & SETTER
    public static ItemService getInstance(ArrayMap<String,String> jsonItemStrings)
    {
        if(instance == null) {
            instance = new ItemService(jsonItemStrings);
        }
        return instance;
    }

    public static ItemService getInstanceFromFiles(ArrayMap<String,String> jsonItemPaths)
    {
        ArrayMap<String,String> jsonItemStrings = new ArrayMap<>();
        for(String category : jsonItemPaths.keys())
        {
            String json = Gdx.files.internal(jsonItemPaths.get(category)).readString();
            jsonItemStrings.put(category, json);
        }

        return getInstance(jsonItemStrings);
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
