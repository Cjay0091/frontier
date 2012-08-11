/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game;

import com.evelus.frontier.game.model.Player;
import com.evelus.frontier.util.LinkedArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class World implements Runnable {

    /**
     * The logger instance for this world.
     */
    private static final Logger logger = Logger.getLogger(World.class.getSimpleName());

    /**
     * The instance of this world.
     */
    private static World instance;

    /**
     * Construct a new {@link World};
     */
    private World ( ) 
    {
        initialize( );
    }

    /**
     * The players within this world.
     */
    private LinkedArrayList<Player> players;

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
        players = new LinkedArrayList<Player>(2048);
    }

    @Override
    public void run( )
    {
        try {
            for(;;) {
                synchronized(this) {
                    if(!isRunning)
                        break;
                }
                long startTime = System.currentTimeMillis();
                for(Player player : players) {
                    player.update();
                    player.updateMovement();
                    player.updateLocation();
                }
                long takenTime = System.currentTimeMillis() - startTime;
                if(takenTime <= 600L) {
                    Thread.sleep(600L - takenTime);
                }
            }
        } catch(Throwable t) {
            logger.log(Level.INFO, "Exception thrown while handling the logic for the world");
        }
    }

    /**
     * Gets a player from this world.
     * 
     * @param id The id of the player to get.
     * @return The player.
     */
    public Player getPlayer( int id )
    {
        return players.getElement( id );
    }

    /**
     * Gets the instance of this class.
     *
     * @return The instance.
     */
    public static World getInstance( )
    {
        if( instance == null )
            instance = new World();
        return instance;
    }
}
