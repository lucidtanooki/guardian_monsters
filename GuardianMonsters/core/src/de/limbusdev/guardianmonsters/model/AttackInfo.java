package de.limbusdev.guardianmonsters.model;


import de.limbusdev.guardianmonsters.enums.AttackType;
import de.limbusdev.guardianmonsters.enums.SFXType;

/**
 * Created by georg on 24.01.16.
 */
public class AttackInfo {
    /* ............................................................................ ATTRIBUTES .. */
    public static Attack kick = new Attack(
            AttackType.PHYSICAL, 10, "Kick", SFXType.HIT, 0);
    public static Attack tooth = new Attack(
            AttackType.PHYSICAL, 10, "Tooth", SFXType.HIT, 0);
    public static Attack punch = new Attack(
            AttackType.PHYSICAL, 15, "Punch", SFXType.HIT, 0);
    public static Attack facefold = new Attack(
            AttackType.PHYSICAL, 20, "Facefold", SFXType.HIT, 0);
    public static Attack tripit = new Attack(
            AttackType.PHYSICAL, 15, "Tripit", SFXType.HIT, 0);
    public static Attack fire = new Attack(
            AttackType.MAGICAL, 20, "Fire", SFXType.HIT, 0);
    public static Attack fira = new Attack(
            AttackType.MAGICAL, 40, "Fira", SFXType.HIT, 0);
    public static Attack fiza = new Attack(
            AttackType.MAGICAL, 80, "Fiza", SFXType.HIT, 0);
    public static Attack ice = new Attack(
            AttackType.MAGICAL, 20, "Ice", SFXType.HIT, 0);
    public static Attack ica = new Attack(
            AttackType.MAGICAL, 40, "Ica", SFXType.HIT, 0);
    public static Attack iza = new Attack(
            AttackType.MAGICAL, 80, "Iza", SFXType.HIT, 0);
    public static Attack earth = new Attack(
            AttackType.MAGICAL, 20, "Earth", SFXType.HIT, 0);
    public static Attack eartha = new Attack(
            AttackType.MAGICAL, 40, "Eartha", SFXType.HIT, 0);
    public static Attack earza = new Attack(
            AttackType.MAGICAL, 80, "Earza", SFXType.HIT, 0);
    public static Attack water = new Attack(
            AttackType.MAGICAL, 20, "Water", SFXType.HIT, 0);
    public static Attack watera = new Attack(
            AttackType.MAGICAL, 40, "Watera", SFXType.HIT, 0);
    public static Attack wateza = new Attack(
            AttackType.MAGICAL, 80, "Wateza", SFXType.HIT, 0);
    /* ........................................................................... CONSTRUCTOR .. */
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
