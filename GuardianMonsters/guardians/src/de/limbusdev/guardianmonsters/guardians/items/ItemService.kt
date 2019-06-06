package de.limbusdev.guardianmonsters.guardians.items

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue

import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment

/**
 * ItemDB provides information about all items. Items are created from data/items.xml
 *
 * @author Georg Eckert 2017
 */

/**
 * Takes several JsonStrings and parses items from them:
 * itemsEquipment.json
 * itemsKey.json
 * itemsMedicine.json
 *
 * @param jsonItemStrings
 */
class ItemService private constructor(jsonItemStrings: ArrayMap<String, String>) : IItemService
{
    // .................................................................................. Properties

    // ................................................................................ Constructors
    init
    {
        items = ArrayMap()

        val jsonReader = JsonReader()
        var jsonValue: JsonValue

        for (jsonString in jsonItemStrings.values())
        {
            jsonValue = jsonReader.parse(jsonString)
            jsonValue = jsonValue.get("items")
            for (value in jsonValue)
            {
                val item = JSONItemParser.parseJsonItem(value)
                if (!items.containsKey(item.name))
                {
                    items.put(item.name, item)
                }
            }
        }
    }

    // ..................................................................................... Methods
    override fun getItem(name: String): Item = items[name]

    override fun getEquipment(name: String): Equipment
    {
        if(getItem(name) is Equipment)
        {
            return (getItem(name) as Equipment)
        }
        else
        {
            throw IllegalArgumentException("Given name is not an Equipment Item.")
        }
    }

    override fun destroy() { instance = null }


    // ................................................................................... Companion
    companion object
    {
        private var instance: ItemService? = null
        private var items   : ArrayMap<String, Item> = ArrayMap()

        fun getInstance(jsonItemStrings: ArrayMap<String, String>): ItemService
        {
            if (instance == null) { instance = ItemService(jsonItemStrings) }
            return instance!!
        }

        fun getInstanceFromFiles(jsonItemPaths: ArrayMap<String, String>): ItemService
        {
            val jsonItemStrings = ArrayMap<String, String>()
            for (category in jsonItemPaths.keys())
            {
                val json = Gdx.files.internal(jsonItemPaths.get(category)).readString()
                jsonItemStrings.put(category, json)
            }

            return getInstance(jsonItemStrings)
        }
    }
}
