/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.items;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

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
            ByteBuffer byteBuffer = ByteBuffer.allocate(dis.available());
            dis.readFully( byteBuffer.array() );
            int amountDefs = byteBuffer.getShort();
            int maximumDef = byteBuffer.getShort();
            definitions = new ItemDefinition[ maximumDef + 1 ];
            for( int i = 0 ; i < amountDefs ; i++ ) {
                int id = byteBuffer.getShort();
                ItemDefinition definition = definitions[ id ] = new ItemDefinition( id );
                while(true) {
                    int opcode = byteBuffer.get() & 0xFF;
                    if( opcode == 0 )
                        break;
                    definition.load( opcode , byteBuffer );
                }
            }
            dis.close();
        } catch( IOException ioex ) {
            logger.log(Level.INFO, "Failed to load the item configuration");
        }
    }
}