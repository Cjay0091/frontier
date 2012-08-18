/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class Constants {

    /**
     * Prevent construction;
     */
    private Constants ( ) { }

    /**
     * The maximum amount of players allowed for the server.
     */
    public static final int AMOUNT_PLAYERS = 2047;

    /**
     * The maximum amount of allowed in the view of a players client.
     */
    public static final int ENTITIES_IN_VIEW = 256;

    /**
     * The path to the archive database.
     */
    public static final String ARCHIVE_DATABASE_PATH = "./bin/archives.db";

    /**
     * The path to the region configuration.
     */
    public static final String REGION_CONFIG_PATH = "./bin/regionconfig.dat";
    
    /**
     * The path to the item configuration.
     */
    public static final String ITEM_CONFIG_PATH = "./bin/itemconfig.dat";
    
    /**
     * The path to the widget configuration.
     */
    public static final String WIDGET_CONFIG_PATH = "./bin/widgetconfig.dat";
    
    /**
     * The path to the plugins.
     */
    public static final String PLUGIN_PATH = "./bin/plugins/";

    /**
     * The option to have ISAAC active on the server.
     */
    public static final boolean ISAAC_ACTIVE = true;

    /**
     * The option to have RSA active on the server.
     */
    public static final boolean RSA_ACTIVE = false;

}
