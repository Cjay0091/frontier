/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.widgets;

import com.evelus.frontier.game.items.ItemLoader;
import com.evelus.frontier.io.Buffer;
import com.evelus.frontier.listeners.widgets.ButtonListener;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class WidgetLoader implements WidgetController {
    
    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(ItemLoader.class.getSimpleName());
    
    /**
     * The instance of this class.
     */
    private static WidgetLoader instance;

    /**
     * Construct a new {@link WidgetLoader};
     */
    private WidgetLoader ( ) { }

    /**
     * The definitions for this widget loader.
     */
    private WidgetDefinition[] definitions;
    
    /**
     * The map to lookup the id of a widget from its hash.
     */
    private Map<Integer, Integer> lookupMap;
    
    /**
     * The id of the maximum container.
     */
    private int maximumContainerId;
    
    /**
     * The container widgets.
     */
    private WidgetDefinition[] containers;
    
    /**
     * The button listeners.
     */
    private ButtonListener[] buttonListeners;

    /**
     * Loads the widget definition config from file.
     *
     * @param filePath  The file path to the widget configuration.
     */
    public void loadConfig( String filePath ) {
        try {
            DataInputStream dis = new DataInputStream( new FileInputStream(filePath) );
            Buffer buffer = new Buffer( dis.available() );
            dis.readFully( buffer.getPayload() );
            int amountDefs = buffer.getUword();
            int maximumDef = buffer.getUword();
            definitions = new WidgetDefinition[ maximumDef + 1 ];
            lookupMap = new HashMap<Integer, Integer>();
            maximumContainerId = -1;
            int amountContainers = 0;
            int amountButtons = 0;
            for( int i = 0 ; i < amountDefs ; i++ ) {
                int id = buffer.getUword();
                WidgetDefinition definition = definitions[ id ] = new WidgetDefinition( id );
                int hash = buffer.getDword();
                lookupMap.put( hash , id );
                definition.load( buffer );
                if( definition.getType() == WidgetDefinition.CONTAINER_TYPE ) {
                    int containerId = definition.getContainerId();
                    if( containerId > maximumContainerId ) {
                        maximumContainerId = containerId;
                    }
                    amountContainers++;
                }
                if( definition.getType() == WidgetDefinition.BUTTON_TYPE ) {
                    int containerId = definition.getButtonId();
                    if( containerId > maximumContainerId ) {
                        maximumContainerId = containerId;
                    }
                    amountContainers++;
                }
            }
            containers = new WidgetDefinition[ amountContainers ];
            int counter = 0;
            for( int i = 0 ; i < definitions.length ; i++ ) {
                WidgetDefinition definition = definitions[ i ];
                if( definition == null || definition.getType() != WidgetDefinition.CONTAINER_TYPE ) {
                    continue;
                }
                containers[ counter++ ] = definition;
            }
            logger.log( Level.INFO , "Finished loading " + amountDefs + " widget definitions" );
            dis.close();
        } catch( IOException ioex ) {
            logger.log( Level.INFO , "Failed to load the widget configuration" );
        }
    }
    
    /**
     * Gets the maximum container id.
     * 
     * @return The maximum container id. 
     */
    public int getMaximumContainer() {
        return maximumContainerId;
    }
    
    /**
     * Gets the container widgets.
     * 
     * @return The container widgets.
     */
    public WidgetDefinition[] getContainers( )
    {
        return containers;
    }
    
    /**
     * Gets the instance of this widget loader.
     * 
     * @return The instance.
     */
    public static WidgetLoader getInstance( )
    {
        if( instance == null ) {
            instance = new WidgetLoader( );
        }
        return instance;
    }
}
