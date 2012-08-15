/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.regions;

import com.evelus.frontier.game.model.Position;
import com.evelus.frontier.io.Buffer;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class RegionHandler {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(RegionHandler.class.getSimpleName());

    /**
     * Prevent construction;
     */
    private RegionHandler ( ) { }

    /**
     * The regions array.
     */
    private static Region[][] regions;

    /**
     * Loads the file configuration for this region handler.
     *
     * @param filePath The file path to the region configuration file.
     */
    public static void loadConfig( String filePath )
    {
        try {
            DataInputStream dis = new DataInputStream( new FileInputStream(filePath) );
            Buffer buffer = new Buffer( dis.available() );
            dis.readFully( buffer.getPayload() );
            while( true ) {
                int opcode = buffer.getUbyte();
                if( opcode == 0 )
                    break;
                if( opcode == 1 ) {
                    int rPositionX = buffer.getUbyte();
                    int rPositionY = buffer.getUbyte();
                    Region region = regions[ rPositionX ][ rPositionY ] = new Region();
                    region.load(buffer);
                }
            }
            logger.log( Level.INFO , "Successfully loaded the region configuration" );
        } catch( Throwable t ) {
            logger.log(Level.INFO, "Failed to load the region configuration");
        }
    }

    /**
     * Gets a region.
     *
     * @param rPositionX The region x position coordinate.
     * @param rPositionY The region y position coordinate.
     * @return The region.
     */
    public static Region getRegion( int rPositionX , int rPositionY )
    {
        return regions[ rPositionX ][ rPositionY ];
    }

    static {
        regions = new Region[ Position.MAXIMUM_X >> 6 ][ Position.MAXIMUM_Y >> 6 ];
    }
}