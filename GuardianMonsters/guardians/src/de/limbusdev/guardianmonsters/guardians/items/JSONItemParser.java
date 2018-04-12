package de.limbusdev.guardianmonsters.guardians.items;

import com.badlogic.gdx.utils.JsonValue;

import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.BodyPart;
import de.limbusdev.guardianmonsters.guardians.items.equipment.Equipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.FootEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.HandEquipment;
import de.limbusdev.guardianmonsters.guardians.items.equipment.HeadEquipment;
import de.limbusdev.guardianmonsters.guardians.items.medicine.MedicalItem;

/**
 * JSONItemParser
 *
 * @author Georg Eckert 2018
 */

public class JSONItemParser
{
    public static Item parseJsonItem(JsonValue jsonItem)
    {

        Item item;

        try {

            switch (jsonItem.getString("category")) {

                case "medicine":
                    item = parseMedicine(jsonItem);
                    break;
                case "Equipment":
                    item = parseEquipmentItem(jsonItem);
                    break;
                case "Key":
                    item = parseKeyItem(jsonItem);
                    break;
                default:
                    item = new MedicalItem("Water", 0, MedicalItem.Type.HP_CURE);
                    break;
            }
        } catch (Exception ex) {

            ex.printStackTrace();
            item = new MedicalItem("Water", 0, MedicalItem.Type.HP_CURE);
        }

        return item;
    }

    private static Item parseMedicine(JsonValue jsonItem)
    {
        switch(jsonItem.getString("type")) {
            case "HPcure":
                return parseHPCuringItem(jsonItem);
            case "MPcure":
                return parseMPCuringItem(jsonItem);
            case "revive":
                return parseRevivingItem(jsonItem);
            default:
                return new MedicalItem("Water", 0, MedicalItem.Type.HP_CURE);
        }
    }

    private static Item parseHPCuringItem(JsonValue jsonItem)
    {

        return new MedicalItem(
            jsonItem.getString("nameID"),
            jsonItem.getInt("value"),
            MedicalItem.Type.HP_CURE
        );
    }

    private static Item parseMPCuringItem(JsonValue jsonItem)
    {

        return new MedicalItem(
            jsonItem.getString("nameID"),
            jsonItem.getInt("value"),
            MedicalItem.Type.MP_CURE
        );
    }

    private static Item parseRevivingItem(JsonValue jsonItem)
    {

        return new MedicalItem(
            jsonItem.getString("nameID"),
            jsonItem.getInt("fraction"),
            MedicalItem.Type.REVIVE
        );
    }

    private static Item parseKeyItem(JsonValue jsonItem)
    {

        return new KeyItem(jsonItem.getString("nameID"));
    }

    private static Item parseEquipmentItem(JsonValue jsonItem)
    {
        BodyPart bodyPart = BodyPart.valueOf(jsonItem.getString("body-part", "hands").toUpperCase());

        String nameID = jsonItem.getString("nameID", "claws-wood");
        int pStr =      jsonItem.getInt("addsPStr",    0);
        int pDef =      jsonItem.getInt("addsPDef",    0);
        int mStr =      jsonItem.getInt("addsMStr",    0);
        int mDef =      jsonItem.getInt("addsMDef",    0);
        int speed =     jsonItem.getInt("addsSpeed",   0);
        int hp =        jsonItem.getInt("addsHP",      0);
        int mp =        jsonItem.getInt("addsMP",      0);
        int exp =       jsonItem.getInt("addsEXP",     0);
        String type =   jsonItem.getString("type").toUpperCase();

        Equipment equip;

        switch(bodyPart)
        {
            case HEAD:
                equip = new HeadEquipment(nameID, HeadEquipment.Type.valueOf(type),
                    pStr, pDef, mStr, mDef, speed, hp, mp, exp);
                break;
            case BODY:
                equip = new BodyEquipment(nameID, BodyEquipment.Type.valueOf(type),
                    pStr, pDef, mStr, mDef, speed, hp, mp, exp);
                break;
            case FEET:
                equip = new FootEquipment(nameID, FootEquipment.Type.valueOf(type),
                    pStr, pDef, mStr, mDef, speed, hp, mp, exp);
                break;
            default:
                // HandEquipment
                equip = new HandEquipment(nameID, HandEquipment.Type.valueOf(type),
                    pStr, pDef, mStr, mDef, speed, hp, mp, exp);
                break;
        }

        return equip;
    }
}
