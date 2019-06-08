package de.limbusdev.guardianmonsters.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;

import de.limbusdev.guardianmonsters.media.SFXType;

public class AbilityMediaDB
{
    private static AbilityMediaDB instance;
    private ArrayMap<String, AbilityMedia> mediaInfos;

    private AbilityMediaDB()
    {
        FileHandle handleJson = Gdx.files.internal("data/abilitiesMultimediaInfo.json");
        String jsonString = handleJson.readString();

        mediaInfos = new ArrayMap<>();
        mediaInfos = readAbilityMediaFromJsonString(jsonString);
    }

    public static ArrayMap<String, AbilityMedia> readAbilityMediaFromJsonString(String jsonString)
    {
        ArrayMap<String, AbilityMedia> abilityMediaInfos = new ArrayMap<>();
        Json json = new Json();
        ArrayList<JsonValue> elementList = json.fromJson(ArrayList.class, jsonString);

        JsonAbilityMedia jam;
        AbilityMedia abilityMedia;
        for (JsonValue v : elementList)
        {
            jam = json.readValue(JsonAbilityMedia.class, v);

            abilityMedia = new AbilityMedia(

                jam.name,
                jam.sfxIndex,
                SFXType.valueOf(jam.sfxType.toUpperCase()),
                AnimationType.valueOf(jam.animationType.toUpperCase())
            );

            abilityMediaInfos.put(abilityMedia.getName(), abilityMedia);
        }

        return abilityMediaInfos;
    }

    public static synchronized AbilityMediaDB getInstance()
    {
        if(instance == null) instance = new AbilityMediaDB();
        return instance;
    }

    public AbilityMedia getAbilityMedia(String abilityName)
    {
        AbilityMedia am = mediaInfos.get(abilityName);
        if(am == null) am = new AbilityMedia(abilityName, 0, SFXType.NONE, AnimationType.CONTACT);
        return am;
    }

    private static class JsonAbilityMedia
    {
        public String name;
        public int sfxIndex;
        public String sfxType;
        public String animationType;
    }
}
