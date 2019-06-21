package de.limbusdev.guardianmonsters.battle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.ArrayMap
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonValue

import java.util.ArrayList

import de.limbusdev.guardianmonsters.media.SFXType

object AbilityMediaDB
{
    // .................................................................................. Properties
    private var mediaInfos: ArrayMap<String, AbilityMedia>


    // ................................................................................ Constructors
    init
    {
        val handleJson = Gdx.files.internal("data/abilitiesMultimediaInfo.json")
        val jsonString = handleJson.readString()

        mediaInfos = readAbilityMediaFromJsonString(jsonString)
    }


    // ..................................................................................... Methods
    operator fun get(abilityName: String): AbilityMedia
    {
        var am: AbilityMedia? = mediaInfos[abilityName]
        if (am == null) { am = AbilityMedia(abilityName, 0, SFXType.NONE, AnimationType.CONTACT) }
        return am
    }

    private class JsonAbilityMedia
    {
        var name: String? = null
        var sfxIndex: Int = 0
        var sfxType: String? = null
        var animationType: String? = null
    }

    private fun readAbilityMediaFromJsonString(jsonString: String): ArrayMap<String, AbilityMedia>
    {
        val abilityMediaInfos = ArrayMap<String, AbilityMedia>()
        val json = Json()
        val elementList = json.fromJson(ArrayList::class.java, jsonString) as ArrayList<JsonValue>

        var jam: JsonAbilityMedia
        var abilityMedia: AbilityMedia
        for (v in elementList)
        {
            jam = json.readValue(JsonAbilityMedia::class.java, v)

            abilityMedia = AbilityMedia(

                    jam.name!!,
                    jam.sfxIndex,
                    SFXType.valueOf(jam.sfxType!!.toUpperCase()),
                    AnimationType.valueOf(jam.animationType!!.toUpperCase())
            )

            abilityMediaInfos.put(abilityMedia.name, abilityMedia)
        }

        return abilityMediaInfos
    }
}
