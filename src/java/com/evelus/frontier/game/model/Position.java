/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.model;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class Position {

    /**
     * The maximum x and maximum y coordinates.
     */
    public static int MAXIMUM_X = 16383, MAXIMUM_Y = 16383;

    /**
     * Constructs a new {@link Position};
     *
     * @param positionX The position x coordinate.
     * @param positionY The position y coordinate.
     */
    public Position ( int positionX, int positionY )
    {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    /**
     * Constructs a new {@link Position};
     *
     * @param positionX The position x coordinate.
     * @param positionY The position y coordinate.
     * @param heightPosition The height position.
     */
    public Position ( int positionX, int positionY , int heightPosition ) 
    {
        this.positionX = positionX;
        this.positionY = positionY;
        this.heightPosition = heightPosition;
    }

    /**
     * The position x coordinate.
     */
    private int positionX;

    /**
     * The position y coordinate.
     */
    private int positionY;

    /**
     * The height position.
     */
    private int heightPosition;

    /**
     * Adds to the coordinate positions.
     *
     * @param deltaX The delta x amount.
     * @param deltaY The delta y amount.
     */
    public void add( int deltaX , int deltaY ) {
        positionX += deltaX;
        positionY += deltaY;
    }

    /**
     * Sets the position x coordinate.
     *
     * @param positionX The x coordinate value.
     */
    public void setPositionX( int positionX )
    {
        this.positionX = positionX;
    }

    /**
     * Gets the position x coordinate.
     *
     * @return The position x.
     */
    public int getPositionX( )
    {
        return positionX;
    }

    /**
     * Sets the position y coordinate.
     *
     * @param positionY The y coordinate value.
     */
    public void setPositionY( int positionY )
    {
        this.positionY = positionY;
    }

    /**
     * Gets the position y coordinate.
     *
     * @return The position y.
     */
    public int getPositionY( )
    {
        return positionY;
    }

    /**
     * Gets the height position.
     *
     * @return The height position.
     */
    public int getHeight( )
    {
        return heightPosition;
    }

    /**
     * Gets the sector x coordinate.
     *
     * @return The sector x.
     */
    public int getSectorX( )
    {
        return positionX >> 3;
    }

    /**
     * Gets the sector y coordinate.
     *
     * @return The sector y.
     */
    public int getSectorY( )
    {
        return positionY >> 3;
    }

    /**
     * Gets the region x coordinate.
     *
     * @return The region x.
     */
    public int getRegionX( )
    {
        return positionX >> 6;
    }

    /**
     * Gets the region y coordinate.
     *
     * @return The region y.
     */
    public int getRegionY( )
    {
        return positionY >> 6;
    }
    
    /**
     * Gets the map position x coordinate.
     * 
     * @return The map x.
     */
    public int getMapPositionX( )
    {
        return positionX - ( getSectorX() - 6 << 3 );
    }
    
     /**
     * Gets the map position y coordinate.
     * 
     * @return The map y.
     */
    public int getMapPositionY( )
    {
        return positionY - ( getSectorY() - 6 << 3 );
    }
}