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
public final class ItemContainer {
    
    /**
     * Constructs a new {@link ItemContainer};
     * 
     * @param size The size of the item container to create.
     */
    public ItemContainer ( int size ) 
    { 
        items = new GameItem[ size ];
        this.size = size;
    }
    
    /**
     * The items in this item container.
     */
    private GameItem[] items;
    
    /**
     * The size of this item container.
     */
    private int size;
    
    /**
     * Adds an item to this item container.
     * 
     * @param gameItem The game item to add to this container.
     * @param stack If the item should stack or not.
     * @return If the item was successfully added to this container.
     */
    public boolean addItem( GameItem gameItem , boolean stack )
    {
        int slot = -1;
        for( int i = 0 ; i < size ; i++ ) {
            GameItem item = items[ i ];
            if( stack ) {
                if( item != null ) {
                    if( item.getId() == gameItem.getId() ) {
                        item.add( gameItem.getAmount() );
                        return true;
                    }
                } else {
                    if( slot == -1 ) {
                        slot = i;
                    }
                }
            } else {
                if( item == null ) {
                    items[ i ] = gameItem;
                    return true;
                }
            }
        }
        
        if( slot != -1 ) {
            items[ slot ] = gameItem;
            return true;
        }
        
        return false;
    }
    
    /**
     * Gets an item from this item container.
     * 
     * @param slot The slot of the item to get.
     * @return The item.
     */
    public GameItem getItem( int slot )
    {
        if( slot < 0 || slot >= size ) {
            return null;
        }
        return items[ slot ];
    }
    
    /**
     * Gets the size of this item container.
     * 
     * @return The size.
     */
    public int getSize( )
    {
        return size;
    }
}