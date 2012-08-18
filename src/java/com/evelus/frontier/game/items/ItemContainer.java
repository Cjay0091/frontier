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
    }
    
    /**
     * The items in this item container.
     */
    private GameItem[] items;
}