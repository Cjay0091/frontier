/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.model.player;

import com.evelus.frontier.game.model.GamePlayer;
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
        initialize( );
    }
    
    /**
     * The player for this widget handler.
     */
    private GamePlayer player;
    
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
     * Gets if a widget is currently open.
     *
     * @param parentId The parent id of the widget to see if its open.
     * @return If the widget is currently open on the clients screen.
     */
    public boolean widgetOpen( int parentId )
    {
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
        DEFAULT_TAB_WIDGETS[  3 ] = 149;
        DEFAULT_TAB_WIDGETS[ 10 ] = 182;
    }
}