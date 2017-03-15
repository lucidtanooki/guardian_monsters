package de.limbusdev.guardianmonsters.model.abilities;

import com.badlogic.ashley.signals.Signal;

/**
 * Node
 *
 * @author Georg Eckert 2017
 */

public class Node extends Signal<Node> {

    public enum Type {
        EMPTY, ABILITY, EQUIPMENT, METAMORPHOSIS,
    }

    public enum State {
        DISABLED, ENABLED, ACTIVE,
    }

    public int x;
    public int y;
    public int ID;
    public Type type;
    private State state;

    /**
     * For Serialization only!
     */
    public Node(){}

    public Node(int x, int y, int ID) {
        this.y = y;
        this.x = x;
        this.ID = ID;
        this.type = Type.EMPTY;
        this.state = State.DISABLED;
    }

    public void activate() {
        state = State.ACTIVE;
        dispatch(this);
    }

    public void enable() {
        if(state != State.ACTIVE) {
            state = State.ENABLED;
        }
        dispatch(this);
    }

    public void disable() {
        if(state != State.ACTIVE && state != State.ENABLED) {
            state = State.DISABLED;
        }
        dispatch(this);
    }

    public boolean isActive() {
        return (state == State.ACTIVE);
    }

    public boolean isEnabled() {
        return (state == State.ENABLED);
    }

    public boolean isDisabled() {
        return (state == State.DISABLED);
    }

    public State getState() {
        return state;
    }
}
