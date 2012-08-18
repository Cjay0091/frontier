/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.items;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class GameItem {
    
    /**
     * Constructs a new {@link GameItem};
     * 
     * @param id The id of the item.
     */
    public GameItem( int id )
    {
        this( id , 1 );
    }
    
    /**
     * Constructs a new {@link GameItem};
     * 
     * @param id The id of the item.
     * @param amount The amount of the item.
     */
    public GameItem ( int id , int amount ) 
    {
        this.id = id;
        this.amount = amount;
    }
    
    /**
     * The id of the game item.
     */
    private int id;
    
    /**
     * The amount of the game item.
     */
    private int amount;
    
    /**
     * Adds to the amount of this item.
     * 
     * @param i The amount to add to this item.
     */
    public void add( int i )
    {
        amount += i;
    }
    
    /**
     * Gets the id of the item.
     * 
     * @return The id.
     */
    public int getId( )
    {
        return id;
    }
    
    /**
     * Gets the amount of the game item.
     * 
     * @return The amount.
     */
    public int getAmount( )
    {
        return amount;
    }
}
