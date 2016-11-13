package de.limbusdev.guardianmonsters.model;


import de.limbusdev.guardianmonsters.enums.AttackType;
import de.limbusdev.guardianmonsters.enums.Element;
import de.limbusdev.guardianmonsters.enums.SFXType;

/**
 * Contains all existing attacks, sorted by element
 * Created by georg on 24.01.16.
 */
public class AttackInfo {
    /* ............................................................................ ATTRIBUTES .. */

    // Typeless
    public static Attack kick = new Attack(
            AttackType.PHYSICAL, Element.NONE, 5, "att_kick", SFXType.HIT, 0);
    public static Attack tooth = new Attack(
            AttackType.PHYSICAL, Element.NONE, 5, "att_tooth", SFXType.HIT, 0);
    public static Attack punch = new Attack(
            AttackType.PHYSICAL, Element.NONE, 15, "att_punch", SFXType.HIT, 0);
    public static Attack facefold = new Attack(
            AttackType.PHYSICAL, Element.NONE, 20, "att_slap", SFXType.HIT, 0);
    public static Attack tripit = new Attack(
            AttackType.PHYSICAL, Element.NONE, 10, "att_triphazard", SFXType.HIT, 0);

    // Fire
    public static Attack embers = new Attack(
            AttackType.MAGICAL, Element.FIRE, 5, "att_embers", SFXType.HIT, 0);
    public static Attack fire = new Attack(
            AttackType.MAGICAL, Element.FIRE, 10, "att_fire", SFXType.HIT, 0);
    public static Attack fira = new Attack(
            AttackType.MAGICAL, Element.FIRE, 20, "att_fira", SFXType.HIT, 0);
    public static Attack fiza = new Attack(
            AttackType.MAGICAL, Element.FIRE, 40, "att_fiza", SFXType.HIT, 0);

    // Frost
    public static Attack ice = new Attack(
            AttackType.MAGICAL, Element.FROST, 10, "att_ice", SFXType.HIT, 0);
    public static Attack ica = new Attack(
            AttackType.MAGICAL, Element.FROST, 20, "att_ica", SFXType.HIT, 0);
    public static Attack iza = new Attack(
            AttackType.MAGICAL, Element.FROST, 40, "att_iza", SFXType.HIT, 0);

    // Earth
    public static Attack earth = new Attack(
            AttackType.MAGICAL, Element.EARTH, 10, "att_earth", SFXType.HIT, 0);
    public static Attack eartha = new Attack(
            AttackType.MAGICAL, Element.EARTH, 20, "att_eartha", SFXType.HIT, 0);
    public static Attack earza = new Attack(
            AttackType.MAGICAL, Element.EARTH, 40, "att_earza", SFXType.HIT, 0);

    // Water
    public static Attack sprinkle = new Attack(
            AttackType.PHYSICAL, Element.WATER, 5, "att_sprinkle", SFXType.WATER, 0);
    public static Attack water = new Attack(
            AttackType.MAGICAL, Element.WATER, 10, "att_water", SFXType.WATER, 0);
    public static Attack watera = new Attack(
            AttackType.MAGICAL, Element.WATER, 20, "att_watera", SFXType.WATER, 0);
    public static Attack wateza = new Attack(
            AttackType.MAGICAL, Element.WATER, 40, "att_wateza", SFXType.WATER, 0);

    // Forest
    public static Attack leafgust = new Attack(
        AttackType.PHYSICAL, Element.FOREST, 5, "att_leafgust", SFXType.AIR, 0);

    // Demon
    public static Attack darkspunk = new Attack(
        AttackType.MAGICAL, Element.DEMON, 5, "att_darkspunk", SFXType.HIT, 0);

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
