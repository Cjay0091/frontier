/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.regions;

import com.evelus.frontier.game.model.Entity;
import com.evelus.frontier.game.model.GamePlayer;
import com.evelus.frontier.util.LinkedArrayList;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class Sector {

    /**
     * Viewport size:           4 sectors * 4 sectors
     * Entities per viewport:   255
     * -----------------------------------------------
     * About 16 entities per sector. Ish.
     */
    private static final int ENTITIES_PER_SECTOR = 16;

    /**
     * Constructs a new {@link Sector};
     */
    public Sector ( )
    {
        players = new LinkedArrayList<GamePlayer>(ENTITIES_PER_SECTOR);
    }

    /**
     * The players within this sector.
     */
    private LinkedArrayList<GamePlayer> players;

    /**
     * Adds an entity to this sector.
     *
     * @param entity The entity to add to this sector.
     * @return If the entity was successfully added to this sector.
     */
    public boolean addEntity( Entity entity )
    {
        int index = -1;
        if( entity instanceof GamePlayer ) {
            index = players.addElement( (GamePlayer) entity );
        } else
            throw new RuntimeException( );
        entity.setSectorIndex( index );
        return index != -1;
    }

    /**
     * Removes an entity from this sector.
     *
     * @param entity The entity to remove from this sector.
     */
    public void removeEntity( Entity entity )
    {
        LinkedArrayList backedList = null;
        if( entity instanceof GamePlayer ) {
            backedList = players;
        } else
            throw new RuntimeException( );
        backedList.removeElement( entity.getSectorIndex() );
    }

    /**
     * Gets the players in this sector.
     *
     * @return The players.
     */
    public LinkedArrayList<GamePlayer> getPlayers( )
    {
        return players;
    }

    /**
     * Gets if this sector contains players.
     *
     * @return If the sector contains players.
     */
    public boolean hasPlayers( )
    {
        return players.getSize() > 0;
    }
}