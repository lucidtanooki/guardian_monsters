package de.limbusdev.guardianmonsters.guardians.abilities

import com.badlogic.ashley.signals.Signal

/**
 * Node
 *
 * A *Node* is one of the base elements of the @see AbilityGraph. It represents some sort of ability
 * a @see AGuardian can learn. It may also be in a specific state.
 *
 * @author Georg Eckert 2019
 */

class Node : Signal<Node>
{
    // ............................................................................... Inner Classes
    enum class Type  { EMPTY, ABILITY, EQUIPMENT, METAMORPHOSIS }

    enum class State { DISABLED, ENABLED, ACTIVE }

    // .................................................................................. Properties
    var x: Int = 0
    var y: Int = 0
    var ID: Int = 0
    var type:  Type
    var state: State

    // ................................................................................ Constructors
    init
    {
        type = Type.EMPTY
        state = State.DISABLED
    }

    constructor(x: Int, y: Int, ID: Int)
    {
        this.x = x
        this.y = y
        this.ID = ID
    }

    constructor(x: Int, y: Int, ID: Int, type: Type, state: State) : this(x, y, ID)
    {
        this.type = type
        this.state = state
    }

    // ........................................................................... Getters & Setters
    val isActive:   Boolean get() = state == State.ACTIVE;
    val isEnabled:  Boolean get() = state == State.ENABLED
    val isDisabled: Boolean get() = state == State.DISABLED


    fun activate()
    {
        state = State.ACTIVE
        dispatch(this)
    }

    fun enable()
    {
        if (state != State.ACTIVE) { state = State.ENABLED }
        dispatch(this)
    }

    fun disable()
    {
        if (state != State.ACTIVE && state != State.ENABLED) { state = State.DISABLED }
        dispatch(this)
    }

    override fun toString(): String = "Node $ID: $type & $state";
}
