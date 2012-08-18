/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class PluginLoader implements PluginController {
    
    /**
     * The logger instance for this class.
     */
    private final static Logger logger = Logger.getLogger( PluginLoader.class.getSimpleName() );
    
    /**
     * The instance of this class.
     */
    private static PluginLoader instance;
    
    /**
     * Construct a new {@link PluginLoader};
     */
    private PluginLoader ( ) 
    { 
        classLoaders = new HashMap<String, URLClassLoader>();
        plugins = new HashMap<String, Plugin>();
    }
    
    /**
     * The url class loaders for each of the plugin files.
     */
    private Map<String, URLClassLoader> classLoaders;
    
    /**
     * The plugins loaded into memory.
     */
    private Map<String, Plugin> plugins;
    
    /**
     * Loads all the plugins within the path.
     * 
     * @param filePath The file path to load the plugins from.
     */
    public void load( String filePath )
    {
        
        File path = new File( filePath );
        int loadedPlugins = 0;
        for( File file : path.listFiles() ) {
            if( !file.getName().endsWith(".jar") ) {
                continue;
            }
            try {
                loadPlugin( file.getAbsolutePath() );
                loadedPlugins++;
            } catch( Throwable t ) {
                logger.log( Level.INFO , "Failed to load plugin [name=" + file.getName() + "]" );
            }
        }
        logger.log( Level.INFO , "Successfully loaded " + loadedPlugins + " plugins");
    }
    
    /**
     * Loads a plugin into memory.
     * 
     * @param filePath The file path of the plugin to load.
     */
    private void loadPlugin( String filePath ) throws Throwable
    {
        URLClassLoader classLoader = classLoaders.get( filePath );
        if( classLoader == null ) {
            filePath = "jar:file://" + filePath + "!/";
            URL url = new URL( filePath );
            classLoader = new URLClassLoader( new URL[] { url } );
            classLoaders.put( filePath , classLoader );
        }
        Class<Plugin> pluginClass = (Class<Plugin>) classLoader.loadClass( "plugin.PluginImpl" );
        Plugin plugin = pluginClass.newInstance();
        plugin.onLoad();
        plugins.put( plugin.getName() , plugin);
    }
    
    /**
     * Gets the instance of this class.
     * 
     * @return The instance.
     */
    public static PluginLoader getInstance( )
    {
        if( instance == null ) {
            instance = new PluginLoader( );
        }
        return instance;
    }
}