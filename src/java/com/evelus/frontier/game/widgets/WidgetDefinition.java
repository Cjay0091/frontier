/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.widgets;

import com.evelus.frontier.io.Buffer;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class WidgetDefinition {
    
    /**
     * The container widget type.
     */
    public static final int CONTAINER_TYPE = 0;
    
    /**
     * The button widget type.
     */
    public static final int BUTTON_TYPE = 2;
    
    /**
     * Constructs a new {@link WidgetDefinition};
     *
     * @param id The id of the widget.
     * @param hash The hash of the widget.
     */
    public WidgetDefinition( int id , int hash )
    {
        this.id = id;
        this.hash = hash;
        type = -1;
        containerId = -1;
        containerSize = -1;
        buttonId = -1;
    }
    
    /**
     * The id of this widget.
     */
    private int id;

    /**
     * The hash of this widget.
     */
    private int hash;
    
    /**
     * The type of this widget.
     */
    private int type;
    
    /**
     * The container id of this widget.
     */
    private int containerId;
    
    /**
     * The size of this widget.
     */
    private int containerSize;
    
    /**
     * The button id of this widget.
     */
    private int buttonId;
    
    /**
     * Loads the data for this definition.
     * 
     * @param buffer The buffer to load the data from.
     */
    public void load( Buffer buffer )
    {
        type = buffer.getUbyte();
        if( type == CONTAINER_TYPE ) {
            containerId = buffer.getUbyte();
            containerSize = buffer.getUword();
        }
        if( type == BUTTON_TYPE ) {
            buttonId = buffer.getUword();
        }
    }

    /**
     * Gets the hash of this widget.
     *
     * @return The hash.
     */
    public int getHash( )
    {
        return hash;
    }
    
    /**
     * Gets the type of this widget.
     * 
     * @return The type.
     */
    public int getType( )
    {
        return type;
    }
    
    /**
     * Gets the container id of this widget.
     * 
     * @return The container id.
     */
    public int getContainerId( )
    {
        return containerId;
    }
    
    /**
     * Gets the container size of this widget.
     * 
     * @return The container size.
     */
    public int getContainerSize( )
    {
        return containerSize;
    }
    
    /**
     * Gets the button id of this widget.
     * 
     * @return The button id.
     */
    public int getButtonId( )
    {
        return buttonId;
    }
}
