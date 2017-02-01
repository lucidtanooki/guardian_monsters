package de.limbusdev.guardianmonsters.fwmengine.world.ecs.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by georg on 02.12.15.
 */
public class ConversationComponent implements Component {
    /* ............................................................................ ATTRIBUTES .. */
    public String text;
    public String name;
    /* ........................................................................... CONSTRUCTOR .. */
    public ConversationComponent(String text) {
        this(text, "");
    }

    public ConversationComponent(String text, String name) {
        this.text = text;
        this.name = name;
    }
    
    /* ............................................................................... METHODS .. */
    
    /* ..................................................................... GETTERS & SETTERS .. */
}
