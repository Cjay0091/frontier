/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.model.player;

import com.evelus.frontier.game.items.ItemContainer;
import com.evelus.frontier.game.widgets.WidgetDefinition;
import com.evelus.frontier.game.widgets.WidgetLoader;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class ItemHandler {
    
    /**
     * Constructs a new {@link ItemHandler};
     */
    public ItemHandler ( ) 
    { 
        containers = new ItemContainer[ WidgetLoader.getMaximumContainer() + 1 ];
        for( WidgetDefinition definition : WidgetLoader.getContainers( ) ) {
            containers[ definition.getContainerId() ] = new ItemContainer( definition.getSize() );
        }
    }
    
    /**
     * The item containers for this item handler.
     */
    private ItemContainer[] containers;
    
    /**
     * Gets an item container from this item handler.
     * 
     * @param i The id of the container to get.
     * @return The item container.
     */
    public ItemContainer getContainer( int i )
    {
        if( i < 0 || i >= containers.length) {
            return null;
        }
        return containers[ i ];
    }
}
