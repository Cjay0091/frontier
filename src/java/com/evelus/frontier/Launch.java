/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier;

import com.evelus.frontier.game.items.ItemLoader;
import com.evelus.frontier.game.model.player.ItemHandler;
import com.evelus.frontier.game.model.player.WidgetHandler;
import com.evelus.frontier.io.ArchiveManager;
import com.evelus.frontier.game.regions.RegionHandler;
import com.evelus.frontier.game.widgets.WidgetLoader;
import com.evelus.frontier.net.game.GameSessionHandler;
import com.evelus.frontier.plugin.PluginLoader;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class Launch {

    /**
     * The main entry point for the program.
     * @param args The command line arguments.
     */
    public static void main( String[] args ) throws Throwable
    {
        Server.setId( Integer.parseInt(args[0]) );
        int state = -1;
        if( args[1].equals("live") ) {
            state = Server.LIVE_STATE;
        } else if (args[1].equals("dev")) {
            state = Server.DEV_STATE;
        } else {
            throw new RuntimeException("launch mode '" + args[1] + "' not recognized.");
        }
        ItemLoader itemLoader = new ItemLoader();
        itemLoader.loadConfig( Constants.ITEM_CONFIG_PATH );
        WidgetLoader widgetLoader = new WidgetLoader();
        widgetLoader.loadConfig( Constants.WIDGET_CONFIG_PATH );
        WidgetHandler.setWidgetLoader( widgetLoader );
        ItemHandler.setWidgetLoader( widgetLoader );
        GameSessionHandler.setWidgetLoader( widgetLoader );
        PluginLoader pluginLoader = new PluginLoader( );
        pluginLoader.setControllers(widgetLoader, itemLoader);
        pluginLoader.load( Constants.PLUGIN_PATH );
        RegionHandler.loadConfig( Constants.REGION_CONFIG_PATH );
        ArchiveManager.initialize( Constants.ARCHIVE_DATABASE_PATH );
        ArchiveManager.loadAll( );
        Server.setState( state );
    }
}
