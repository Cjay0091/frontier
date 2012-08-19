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
    private WidgetLoader ( )
    {
        lookupMap = new HashMap<Integer, Integer>();
        maximumContainerId = -1;
        maximumButtonId = -1;
    }

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
     * The id of the maximum button.
     */
    private int maximumButtonId;
    
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
    public void loadConfig( String filePath ) 
    {
        try {
            DataInputStream dis = new DataInputStream( new FileInputStream(filePath) );
            Buffer buffer = new Buffer( dis.available() );
            dis.readFully( buffer.getPayload() );
            int amountDefs = buffer.getUword();
            int maximumDef = buffer.getUword();
            definitions = new WidgetDefinition[ maximumDef + 1 ];
            int amountContainers = 0;
            int amountButtons = 0;           
            for( int i = 0 ; i < amountDefs ; i++ ) {
                int id = buffer.getUword();
                int hash = buffer.getDword();
                WidgetDefinition definition = definitions[ id ] = new WidgetDefinition( id , hash );
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
                    if( containerId > maximumButtonId ) {
                        maximumButtonId = containerId;
                    }
                    amountButtons++;
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
            buttonListeners = new ButtonListener[ maximumButtonId + 1 ];
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
     * Registers a button listener to this widget loader.
     * 
     * @param listener The listener to register.
     * @param id The id of the listener to register. 
     */
    public void register( ButtonListener listener, int id ) 
    {
        buttonListeners[ id ] = listener;
    }
    
    /**
     * Gets a button listener.
     * 
     * @param id The id of the button listener to get.
     * @return The button listener.
     */
    public ButtonListener getButtonListener( int id )
    {
        return buttonListeners[ id ];
    }
    
    /**
     * Looks up a widget definition from its hash.
     * 
     * @param hash The hash of the widget definition to lookup.
     * @return The id of the definition.
     */
    public int lookup( int hash )
    {
        if( !lookupMap.containsKey(hash) ) {
            return -1;
        }
        return lookupMap.get( hash );
    }

    /**
     * Gets the definition for a widget container.
     *
     * @param id The id of the container to get the definition for.
     * @return The definition.
     */
    public WidgetDefinition getContainer( int id )
    {
        if( id < 0 || id > containers.length ) {
            return null;
        }
        return containers[ id ];
    }
    
    /**
     * Gets a widget definition.
     * 
     * @param id The id of the definition to get.
     * @return The definition.
     */
    public WidgetDefinition getDefinition( int id )
    {
        if( id < 0 || id > definitions.length ) {
            return null;
        }
        return definitions[ id ];
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
