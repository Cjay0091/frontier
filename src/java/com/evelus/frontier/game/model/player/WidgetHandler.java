/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.model.player;

import com.evelus.frontier.game.model.GamePlayer;
import com.evelus.frontier.net.game.frames.DisplayOverlayFrame;
import com.evelus.frontier.net.game.frames.DisplayWindowFrame;
import com.evelus.frontier.net.game.frames.CloseWidgetsFrame;
import com.evelus.frontier.net.game.frames.SetTabWidgetFrame;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class WidgetHandler {
    
    /**
     * The default tab widgets.
     */
    private static final int[] DEFAULT_TAB_WIDGETS;
    
    /**
     * Constructs a new {@link WidgetHandler};
     * 
     * @param player The player for this widget handler.
     */
    public WidgetHandler ( GamePlayer player ) 
    { 
        this.player = player;
        windowId = -1;
        overlayId = -1;
        initialize( );
    }
    
    /**
     * The player for this widget handler.
     */
    private GamePlayer player;

    /**
     * The currently active widget window parent id.
     */
    private int windowId;

    /**
     * The currently active widget overlay parent id.
     */
    private int overlayId;
    
    /**
     * The widget ids for all the tabs.
     */
    private int[] tabWidgetIds;
    
    /**
     * Initializes this widget handler.
     */
    private void initialize( )
    {
        tabWidgetIds = new int[ 15 ];
        for( int i = 0 ; i < 15 ; i++ ) {
            tabWidgetIds[ i ] = DEFAULT_TAB_WIDGETS[ i ];
        }
    }

    /**
     * Sets the currently open window.
     *
     * @param id The window widget id.
     */
    public void setWindow( int id )
    {
        if( windowId != id ) {
            windowId = id;
            player.sendFrame( new DisplayWindowFrame( id ) );
        }
    }

    /**
     * Sets the current overlay.
     *
     * @param id The overlay widget id.
     */
    public void setOverlay( int id )
    {
        if( overlayId != id ) {
            overlayId = id;
            player.sendFrame( new DisplayOverlayFrame( id ) );
        }
    }

    /**
     * Resets all the displayed widgets.
     */
    public void reset( )
    {
        windowId = -1;
        overlayId = -1;
        player.sendFrame( new CloseWidgetsFrame( ) );
    }

    /**
     * Gets if a widget is currently open.
     *
     * @param parentId The parent id of the widget to see if its open.
     * @return If the widget is currently open on the clients screen.
     */
    public boolean widgetOpen( int parentId )
    {
        if( windowId == parentId ) {
            return true;
        }
        if( overlayId == parentId ) {
            return true;
        }
        for( int i = 0 ; i < 15 ; i++ ) {
            if( tabWidgetIds[ i ] == parentId ) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Update all the tabs.
     */
    public void updateTabs( )
    {
        for( int i = 0 ; i < 15 ; i++ ) {
            updateTab( i );
        }
    }
    
    /**
     * Updates a tab.
     * 
     * @param id The id of the tab to update.
     */
    public void updateTab( int id )
    {
        player.sendFrame( new SetTabWidgetFrame( id , tabWidgetIds[ id ] ) );
    }
    
    static {
        DEFAULT_TAB_WIDGETS = new int[ 15 ];
        for( int i = 0 ; i < 15 ; i++) {
            DEFAULT_TAB_WIDGETS[ i ] = -1;
        }
        DEFAULT_TAB_WIDGETS[  1 ] = 320;
        DEFAULT_TAB_WIDGETS[  3 ] = 149;
        DEFAULT_TAB_WIDGETS[ 10 ] = 182;
    }
}