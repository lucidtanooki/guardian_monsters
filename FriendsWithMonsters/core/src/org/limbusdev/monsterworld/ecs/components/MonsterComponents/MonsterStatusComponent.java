package org.limbusdev.monsterworld.ecs.components.MonsterComponents;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Array;

import org.limbusdev.monsterworld.enums.AttackType;
import org.limbusdev.monsterworld.model.Attack;

/**
 * Created by georg on 06.12.15.
 */
public class MonsterStatusComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public int level;
    public int exp;
    public int physStrength;
    public int HP;
    public int magicStrength;
    public int MP;

    public Array<Attack> attacks;
    /* ........................................................................... CONSTRUCTOR .. */
    public MonsterStatusComponent() {
        this.level = 1;
        this.exp = 0;
        this.physStrength = 10;
        this.HP = 30;
        this.magicStrength = 5;
        this.MP = 5;

        this.attacks = new Array<Attack>();
        attacks.add(new Attack(AttackType.PHYSICAL, 5, "Kick"));
    }
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
