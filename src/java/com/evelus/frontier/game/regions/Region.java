/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.regions;

import com.evelus.frontier.game.model.Entity;
import com.evelus.frontier.game.model.Player;
import com.evelus.frontier.io.Buffer;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class Region {

    /**
     * Construct a new {@link Region};
     */
    public Region ( ) 
    {
        sectors = new Sector[ 8 ][];
    }

    /**
     * The sectors for this region.
     */
    private Sector[][] sectors;

    /**
     * The count of players within this region.
     */
    private int playerCount;

    /**
     * Loads the data for this region.
     *
     * @param buffer The buffer to read the data from.
     */
    public void load( Buffer buffer )
    {
        while( true ) {
            int opcode = buffer.getUbyte();
            if( opcode == 0 )
                break;
            if( opcode == 1 ) {
                int hash = buffer.getUbyte();
                int sPositionX = hash >> 4;
                if( sectors[ sPositionX ] == null )
                    sectors[ sPositionX ] = new Sector[ 8 ];
                sectors[ hash >> 4 ][ hash & 0xF ] = new Sector();
            }
        }
    }

    /**
     * Gets a sector from this region.
     *
     * @param sPositionX The sector x position coordinate.
     * @param sPositionY The sector y position coordinate.
     * @return The sector.
     */
    public Sector getSector( int sPositionX , int sPositionY )
    {
        return sectors[ sPositionX ][ sPositionY ];
    }

    /**
     * Adds an entity to this region.
     *
     * @param entity The entity to add to this region.
     * @param lsPositionX The local sector position x coordinate.
     * @param lsPositionY The local sector position y coordinate.
     * @return If the entity was successfully added to this region.
     */
    public boolean addEntity( Entity entity , int lsPositionX, int lsPositionY )
    {
        if(sectors[ lsPositionX ] == null || sectors[ lsPositionX ][ lsPositionY ] == null)
            return false;
        Sector sector = sectors[ lsPositionX ][ lsPositionY ];
        if( !sector.addEntity(entity) )
            return false;
        if( entity instanceof Player )
            playerCount++;
        return true;
    }

    /**
     * Removes an entity from this region.
     *
     * @param entity The entity to remove from this region.
     * @param lsPositionX The local sector position x coordinate.
     * @param lsPositionY The local sector position y coordinate.
     */
    public void removeEntity( Entity entity , int lsPositionX, int lsPositionY )
    {
        if(sectors[ lsPositionX ] == null || sectors[ lsPositionX ][ lsPositionY ] == null)
            return;
        Sector sector = sectors[ lsPositionX ][ lsPositionY ];
        sector.removeEntity( entity );
        if( entity instanceof Player )
            playerCount--;
    }

    /**
     * Gets if this region contains players.
     *
     * @return If the region contains players.
     */
    public boolean hasPlayers( )
    {
        return playerCount > 0;
    }
}