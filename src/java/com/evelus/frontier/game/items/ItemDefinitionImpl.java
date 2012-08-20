/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.items;

import com.evelus.frontier.io.Buffer;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class ItemDefinitionImpl {
    
    /**
     * Constructs a new {@link ItemDefinition};
     *
     * @param id The id of the item.
     */
    public ItemDefinitionImpl( int id )
    {
        this.id = id;
        name = "";
        equipmentSlot = -1;
        listenerId = -1;
    }

    /**
     * The id of the item.
     */
    private int id;

    /**
     * The name of the item.
     */
    private String name;
    
    /**
     * Flag for if the item is stackable.
     */
    private boolean stackable;

    /**
     * The equipment slot of the item.
     */
    private int equipmentSlot;

    /**
     * The listener id of the item.
     */
    private int listenerId;

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
     * Gets if the item is stackable.
     * 
     * @return If the item is stackable.
     */
    public boolean getStackable( )
    {
        return stackable;
    }

    /**
     * Gets the item equipment slot.
     *
     * @return The equipment slot.
     */
    public int getEquipmentSlot( )
    {
        return equipmentSlot;
    }

    /**
     * Gets the listener id for this item.
     *
     * @return The listener id.
     */
    public int getListenerId( )
    {
        return listenerId;
    }

    /**
     * Loads a data operation for this definition.
     *
     * @param opcode The opcode of the operation to preform.
     * @param buffer The buffer to read the operation's data from.
     */
    public void load( int opcode , Buffer buffer )
    {
        if( opcode == 1 ) {
            name = buffer.getJstr();
        }
        if( opcode == 2 ) {
            stackable = true;
        }
        if( opcode == 3 ) {
            equipmentSlot = buffer.getUbyte();
        }
        if( opcode == 4 ) {
            listenerId = buffer.getUword();
        }
    }
}