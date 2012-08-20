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
     * The window widget type.
     */
    public static final int WINDOW_TYPE = 3;
    
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
        listenerId = -1;
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
     * The window id of this widget.
     */
    private int windowId;

    /**
     * The listener id of this widget.
     */
    private int listenerId;
    
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
        if( type == WINDOW_TYPE ) {
            windowId = buffer.getUword();
            if( windowId == 65535 ) {
                windowId = -1;
            }
        }
        if( type == BUTTON_TYPE || type == WINDOW_TYPE ) {
            listenerId = buffer.getUword();
            if( listenerId == 65535 ) {
                listenerId = -1;
            }
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
     * Gets the parent id of this widget.
     *
     * @return The parent id.
     */
    public int getParentId() {
        return hash >> 16;
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

    /**
     * Gets the window id of this widget.
     *
     * @return The window id.
     */
    public int getWindowId( )
    {
        return windowId;
    }

    /**
     * Gets the listener id of this widget.
     *
     * @return The listener id.
     */
    public int getListenerId( )
    {
        return listenerId;
    }
}
