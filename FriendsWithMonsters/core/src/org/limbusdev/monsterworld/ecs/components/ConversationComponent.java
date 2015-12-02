package org.limbusdev.monsterworld.ecs.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by georg on 02.12.15.
 */
public class ConversationComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public String text;
    /* ........................................................................... CONSTRUCTOR .. */
    public ConversationComponent(String text) {
        this.text = text;
    }
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
