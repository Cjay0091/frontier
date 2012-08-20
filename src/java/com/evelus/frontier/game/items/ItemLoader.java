/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.items;

import com.evelus.frontier.io.Buffer;
import com.evelus.frontier.listeners.items.ItemListener;
import java.io.*;
import java.util.logging.*;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class ItemLoader implements ItemController {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(ItemLoader.class.getSimpleName());

    /**
     * The instance of this item loader.
     */
    private static ItemLoader instance;

    /**
     * Constructs a new {@link ItemLoader};
     */
    private ItemLoader ( ) { }

    /**
     * The definitions for this item loader.
     */
    private ItemDefinitionImpl[] definitions;

    /**
     * The listeners for this item loader.
     */
    private ItemListener[] listeners;

    /**
     * Loads the item definition config from file.
     *
     * @param filePath  The file path to the item configuration.
     */
    public void loadConfig( String filePath ) {
        try {
            DataInputStream dis = new DataInputStream( new FileInputStream(filePath) );
            Buffer buffer = new Buffer( dis.available() );
            dis.readFully( buffer.getPayload() );
            int amountDefs = buffer.getUword();
            int maximumDef = buffer.getUword();
            definitions = new ItemDefinitionImpl[ maximumDef + 1 ];
            int maximumListenerId = -1;
            for( int i = 0 ; i < amountDefs ; i++ ) {
                int id = buffer.getUword();
                ItemDefinitionImpl definition = definitions[ id ] = new ItemDefinitionImpl( id );
                while(true) {
                    int opcode = buffer.getUbyte();
                    if( opcode == 0 )
                        break;
                    definition.load( opcode , buffer );
                }

                if( definition.getListenerId() > maximumListenerId ) {
                    maximumListenerId = definition.getListenerId();
                }
            }
            listeners = new ItemListener[ maximumListenerId + 1 ];
            logger.log( Level.INFO , "Finished loading " + amountDefs + " item definitions" );
            dis.close();
        } catch( IOException ioex ) {
            logger.log( Level.INFO , "Failed to load the item configuration" );
        }
    }

    @Override
    public void register( ItemListener listener , int id )
    {
        listeners[ id  ] = listener;
    }

    /**
     * Gets an item definition.
     *
     * @param id    The id of the definition to get.
     * @return      The definition.
     */
    public ItemDefinitionImpl getDefinition( int id ) {
        if( id < 0 || id >= definitions.length) {
            return null;
        }
        return definitions[ id ];
    }

    /**
     * Gets an item listener from this item loader.
     *
     * @param id The id of the item listener to get.
     * @return The listener.
     */
    public ItemListener getListener( int id )
    {
        if( id < 0 || id >= listeners.length ) {
            return null;
        }
        return listeners[ id ];
    }

    /**
     * Gets the instance of this class.
     *
     * @return The instance.
     */
    public static ItemLoader getInstance( )
    {
        if( instance == null ) {
            instance = new ItemLoader( );
        }
        return instance;
    }
}