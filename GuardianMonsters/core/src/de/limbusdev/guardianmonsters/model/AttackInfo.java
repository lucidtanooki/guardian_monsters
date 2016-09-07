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
            AttackType.PHYSICAL, Element.NONE, 5, "Kick", SFXType.HIT, 0);
    public static Attack tooth = new Attack(
            AttackType.PHYSICAL, Element.NONE, 5, "Tooth", SFXType.HIT, 0);
    public static Attack punch = new Attack(
            AttackType.PHYSICAL, Element.NONE, 15, "Punch", SFXType.HIT, 0);
    public static Attack facefold = new Attack(
            AttackType.PHYSICAL, Element.NONE, 20, "Facefold", SFXType.HIT, 0);
    public static Attack tripit = new Attack(
            AttackType.PHYSICAL, Element.NONE, 10, "Tripit", SFXType.HIT, 0);

    // Fire
    public static Attack embers = new Attack(
            AttackType.MAGICAL, Element.FIRE, 5, "Embers", SFXType.HIT, 0);
    public static Attack fire = new Attack(
            AttackType.MAGICAL, Element.FIRE, 10, "Fire", SFXType.HIT, 0);
    public static Attack fira = new Attack(
            AttackType.MAGICAL, Element.FIRE, 20, "Fira", SFXType.HIT, 0);
    public static Attack fiza = new Attack(
            AttackType.MAGICAL, Element.FIRE, 40, "Fiza", SFXType.HIT, 0);

    // Frost
    public static Attack ice = new Attack(
            AttackType.MAGICAL, Element.FROST, 10, "Ice", SFXType.HIT, 0);
    public static Attack ica = new Attack(
            AttackType.MAGICAL, Element.FROST, 20, "Ica", SFXType.HIT, 0);
    public static Attack iza = new Attack(
            AttackType.MAGICAL, Element.FROST, 40, "Iza", SFXType.HIT, 0);

    // Earth
    public static Attack earth = new Attack(
            AttackType.MAGICAL, Element.EARTH, 10, "Earth", SFXType.HIT, 0);
    public static Attack eartha = new Attack(
            AttackType.MAGICAL, Element.EARTH, 20, "Eartha", SFXType.HIT, 0);
    public static Attack earza = new Attack(
            AttackType.MAGICAL, Element.EARTH, 40, "Earza", SFXType.HIT, 0);

    // Water
    public static Attack sprinkle = new Attack(
            AttackType.PHYSICAL, Element.WATER, 5, "Sprinkle", SFXType.WATER, 0);
    public static Attack water = new Attack(
            AttackType.MAGICAL, Element.WATER, 10, "Water", SFXType.WATER, 0);
    public static Attack watera = new Attack(
            AttackType.MAGICAL, Element.WATER, 20, "Watera", SFXType.WATER, 0);
    public static Attack wateza = new Attack(
            AttackType.MAGICAL, Element.WATER, 40, "Wateza", SFXType.WATER, 0);

    // Forest
    public static Attack leafgust = new Attack(
        AttackType.PHYSICAL, Element.FOREST, 5, "Leafgust", SFXType.AIR, 0);

    // Demon
    public static Attack darkspunk = new Attack(
        AttackType.MAGICAL, Element.DEMON, 5, "Darkspunk", SFXType.HIT, 0);

    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
