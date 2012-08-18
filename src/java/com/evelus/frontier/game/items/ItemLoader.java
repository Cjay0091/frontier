/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.items;

import com.evelus.frontier.io.Buffer;
import java.io.*;
import java.util.logging.*;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class ItemLoader {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(ItemLoader.class.getSimpleName());

    /**
     * Prevent construction;
     */
    private ItemLoader ( ) { }

    /**
     * The definitions for this item loader.
     */
    private static ItemDefinition[] definitions;

    /**
     * Loads the item definition config from file.
     *
     * @param filePath  The file path to the item configuration.
     */
    public static void loadConfig( String filePath ) {
        try {
            DataInputStream dis = new DataInputStream( new FileInputStream(filePath) );
            Buffer buffer = new Buffer( dis.available() );
            dis.readFully( buffer.getPayload() );
            int amountDefs = buffer.getUword();
            int maximumDef = buffer.getUword();
            definitions = new ItemDefinition[ maximumDef + 1 ];
            for( int i = 0 ; i < amountDefs ; i++ ) {
                int id = buffer.getUword();
                ItemDefinition definition = definitions[ id ] = new ItemDefinition( id );
                while(true) {
                    int opcode = buffer.getUbyte();
                    if( opcode == 0 )
                        break;
                    definition.load( opcode , buffer );
                }
            }
            logger.log( Level.INFO , "Finished loading " + amountDefs + " item definitions" );
            dis.close();
        } catch( IOException ioex ) {
            logger.log( Level.INFO , "Failed to load the item configuration" );
        }
    }

    /**
     * Gets an item definition.
     *
     * @param id    The id of the definition to get.
     * @return      The definition.
     */
    public static ItemDefinition getDefinition( int id ) {
        if( id < 0 || id >= definitions.length) {
            return null;
        }
        return definitions[ id ];
    }
}