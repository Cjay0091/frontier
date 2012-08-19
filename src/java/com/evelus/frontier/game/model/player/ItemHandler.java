/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.model.player;

import com.evelus.frontier.game.items.ItemContainer;
import com.evelus.frontier.game.model.GamePlayer;
import com.evelus.frontier.game.widgets.WidgetDefinition;
import com.evelus.frontier.game.widgets.WidgetLoader;
import com.evelus.frontier.net.game.frames.SendItemsFrame;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class ItemHandler {
    
    /**
     * Constructs a new {@link ItemHandler};
     */
    public ItemHandler ( GamePlayer gamePlayer )
    { 
        containers = new ItemContainer[ WidgetLoader.getInstance().getMaximumContainer() + 1 ];
        for( WidgetDefinition definition : WidgetLoader.getInstance().getContainers( ) ) {
            containers[ definition.getContainerId() ] = new ItemContainer( definition.getContainerSize() );
        }
        this.gamePlayer = gamePlayer;
    }

    /**
     * The game player for this item handler.
     */
    private GamePlayer gamePlayer;
    
    /**
     * The item containers for this item handler.
     */
    private ItemContainer[] containers;

    /**
     * Updates all the containers.
     */
    public void updateContainers( )
    {
        for( int i = 0 ; i < containers.length ; i++ ) {
            updateContainer( i );
        }
    }

    /**
     * Updates a container.
     *
     * @param id The id of the container to update.
     */
    public void updateContainer( int id )
    {
        ItemContainer container = containers[ id ];
        if( container == null ) {
            return;
        }
        WidgetDefinition definition = WidgetLoader.getInstance().getContainer( id );
        gamePlayer.sendFrame( new SendItemsFrame( definition.getHash() , container ) );
    }
    
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
