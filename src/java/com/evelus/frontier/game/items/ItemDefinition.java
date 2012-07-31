/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.items;

import com.evelus.frontier.io.Jagbuffer;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class ItemDefinition {
    
    /**
     * Constructs a new {@link ItemDefinition};
     */
    public ItemDefinition( ) { }

    /**
     * The name of the item.
     */
    private String name;

    /**
     * Gets the name of this item definition.
     *
     * @return The name.
     */
    public String getName( )
    {
        return name;
    }

    /**
     * Loads a data operation for this definition.
     *
     * @param opcode    The opcode of the operation to preform.
     * @param jagbuffer The jagbuffer to read the operation's data from.
     */
    public void load( int opcode , Jagbuffer jagbuffer )
    {
        if( opcode == 1 )
            name = jagbuffer.gjstr();
    }
}