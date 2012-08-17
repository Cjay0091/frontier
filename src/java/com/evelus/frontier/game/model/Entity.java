/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.model;

import com.evelus.frontier.game.regions.Region;
import com.evelus.frontier.game.regions.RegionHandler;
import com.evelus.frontier.game.regions.Sector;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public class Entity {

    /**
     * The default x and y spawn coordinates of this entity.
     */
    private static final int DEFAULT_X = 3200, DEFAULT_Y = 3200;

    /**
     * Constructs a new {@link Entity};
     */
    public Entity ( )
    {
        position = new Position(DEFAULT_X, DEFAULT_Y);
        sectorHash = -1;
        regionHash = -1;
    }

    /**
     * The position of this entity.
     */
    private Position position;

    /**
     * The id of this entity.
     */
    private int id;

    /**
     * The sector hash of this entity.
     */
    private int sectorHash;

    /**
     * The last sector hash of this entity.
     */
    private int lastSectorHash;

    /**
     * The region hash of this entity.
     */
    private int regionHash;

    /**
     * The last region hash of this entity.
     */
    private int lastRegionHash;

    /**
     * The sector index where the entity was last stored.
     */
    private int sectorIndex;

    /**
     * Updates the location of this entity.
     */
    public void updateLocation( )
    {
       int regionX = position.getRegionX();
       int regionY = position.getRegionY();
       int rHash = regionX << 8 | regionY;
       boolean updateRegion = false;
       if( regionHash == -1 || rHash != regionHash ) {
           lastRegionHash = regionHash;
           regionHash = rHash;
           updateRegion = true;
       }
       int sectorX = position.getSectorX() - ( regionX << 3 );
       int sectorY = position.getSectorY() - ( regionY << 3 );
       int sHash = sectorX << 8 | sectorY;
       boolean updateSector = false;
       if( sectorHash == -1 || sHash != sectorHash ) {
           lastSectorHash = sectorHash;
           sectorHash = sHash;
           updateSector = true;
       }
       if( updateSector || updateRegion ) {
           Region region;
           if( lastRegionHash != -1 || lastSectorHash != -1) {
               region = RegionHandler.getRegion( lastRegionHash >> 8 , lastRegionHash & 0xFF );
               int sPositionX = lastSectorHash >> 8;
               int sPositionY = lastSectorHash & 0xFF;
               if( lastRegionHash != -1 ) {
                   region.removeEntity( this, sPositionX , sPositionY );
               } else {
                   Sector sector = region.getSector( sPositionX , sPositionY );
                   sector.removeEntity( this );
               }
           }
           region = RegionHandler.getRegion( regionX , regionY );
           region.addEntity( this, sectorX , sectorY );
       }
    }

    /**
     * Gets the position of this entity.
     *
     * @return The position.
     */
    public Position getPosition( )
    {
        return position;
    }
    
    /**
     * Sets the id of this entity.
     * 
     * @param id The id value.
     */
    public void setId( int id )
    {
        this.id = id;
    }

    /**
     * Gets the id of this entity.
     *
     * @return The id.
     */
    public int getId( )
    {
        return id;
    }

    /**
     * Sets the sector index of this entity.
     *
     * @param sectorIndex The index within the sector at which this is stored.
     */
    public void setSectorIndex( int sectorIndex )
    {
        this.sectorIndex = sectorIndex;
    }

    /**
     * Gets the sector index of this entity.
     *
     * @return The sector index.
     */
    public int getSectorIndex( ) {
        return sectorIndex;
    }
}