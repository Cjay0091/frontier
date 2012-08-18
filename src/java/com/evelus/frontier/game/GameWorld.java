/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game;

import com.evelus.frontier.Constants;
import com.evelus.frontier.game.model.GamePlayer;
import com.evelus.frontier.util.LinkedArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class GameWorld implements Runnable {

    /**
     * The logger instance for this world.
     */
    private static final Logger logger = Logger.getLogger(GameWorld.class.getSimpleName());

    /**
     * The instance of this world.
     */
    private static GameWorld instance;

    /**
     * Construct a new {@link World};
     */
    private GameWorld ( ) 
    {
        initialize( );
    }

    /**
     * The players within this world.
     */
    private LinkedArrayList<GamePlayer> players;

    /**
     * The thread that this world is running on.
     */
    private Thread thread;

    /**
     * The flag to signal if the world is still currently running.
     */
    private boolean isRunning;

    /**
     * Initializes this world.
     */
    private void initialize( )
    {
        players = new LinkedArrayList<GamePlayer>( Constants.AMOUNT_PLAYERS );
    }
    
    /**
     * Registers a player to this world.
     * 
     * @param player The player to register to this world.
     * @return If the player was successfully registered.
     */
    public boolean registerPlayer( GamePlayer player )
    {
        int id = players.addElement( player );
        if( id == -1 ) {
            return false;
        }
        player.setId( id );
        return true;
    }
    
    /**
     * Unregisters a player from the world.
     * 
     * @param player The player to unregister.
     */
    public void unregisterPlayer( GamePlayer player )
    {
        players.removeElement( player.getId() );
    }

    @Override
    public void run( )
    {
        try {
            for(;;) {
                synchronized(this) {
                    if(!isRunning) {
                        break;
                    }
                }
                long startTime = System.currentTimeMillis();
                for(GamePlayer player : players) {
                    player.update();
                    player.updateMovement();
                    player.updateLocation();
                }
                for(GamePlayer player : players) {
                    player.updateLists();
                    player.writeUpdates();
                }
                long takenTime = System.currentTimeMillis() - startTime;
                if(takenTime < 600L) {
                    Thread.sleep(600L - takenTime);
                }
            }
        } catch(Throwable t) {
            logger.log(Level.INFO, "Exception thrown while handling the logic for the world");
            t.printStackTrace();
        }
    }

    /**
     * Gets a player from this world.
     * 
     * @param id The id of the player to get.
     * @return The player.
     */
    public GamePlayer getPlayer( int id )
    {
        return players.getElement( id );
    }
    
    /**
     * Starts this world.
     */
    public void start( )
    {
        if( !isRunning ) {
            isRunning = true;
            thread = new Thread( this );
            thread.start();
        }
    }

    /**
     * Gets the instance of this class.
     *
     * @return The instance.
     */
    public static GameWorld getInstance( )
    {
        if( instance == null ) {
            instance = new GameWorld();
        }
        return instance;
    }
}
