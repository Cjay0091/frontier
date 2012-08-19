/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.model.player;

import com.evelus.frontier.util.LinkedArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class ServerBindingsImpl implements ServerBindings {

    /**
     * Constructs a new {@link ServerBindingsImpl};
     *
     * @param amountStates The amount of states in this bindings.
     */
    public ServerBindingsImpl ( int amountStates )
    {
        states = new LinkedArrayList<State> ( amountStates );
        stateMap = new HashMap<String, Integer>( );
    }
    
    /**
     * A state in this bindings.
     */
    private static class State {
        
        /**
         * The current value of the state.
         */
        int value;
    }

    /**
     * The states for this binding.
     */
    private LinkedArrayList<State> states;

    /**
     * The map used to lookup states from its name.
     */
    private Map<String, Integer> stateMap;

    /**
     * Looks up the value of a state. The state is created if it does not exist.
     *
     * @param name The name of the state.
     * @return The value of the state.
     */
    public int lookup( String name )
    {
        State state = null;
        if( !stateMap.containsKey( name ) ) {
            state = createState( name );
        } else {
            state = states.getElement( stateMap.get( name ) );
        }
        return state.value;
    }

    /**
     * Sets the value of a state.
     *
     * @param name The name of the state to set its value for.
     * @param i The value.
     */
    public void set( String name , int i )
    {
        State state = null;
        if( !stateMap.containsKey( name ) ) {
            state = createState( name );
        } else {
            state = states.getElement( stateMap.get( name ) );
        }
        state.value = i;
    }

    /**
     * Creates a new state.
     *
     * @param name The name of the state to create.
     * @return The created state.
     */
    private State createState( String name )
    {
        State state = new State( );
        int id = states.addElement( state );
        if( id == -1 ) {
            throw new RuntimeException( "failed to add state to states list" );
        }
        stateMap.put( name , id );
        return state;
    }
}