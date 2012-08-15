/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.ondemand;

import com.evelus.frontier.Constants;
import com.evelus.frontier.util.LinkedArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class OndemandWorker implements Runnable {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger( OndemandWorker.class.getSimpleName() );

    /**
     * The instance of this class.
     */
    private static OndemandWorker instance;

    /**
     * Constructs a new {@link Odworker};
     */
    private OndemandWorker ( )
    {
        sessions = new LinkedArrayList<OndemandSession>( Constants.AMOUNT_PLAYERS );
    }

    /**
     * The sessions for this ondemand worker.
     */
    private LinkedArrayList<OndemandSession> sessions;

    /**
     * The thread for this worker.
     */
    private Thread thread;

    /**
     * The flag for if this ondemand worker is still running.
     */
    private boolean isRunning;

    /**
     * Registers a session to this worker.
     *
     * @param session The session to register.
     * @return If the session was successfully registered.
     */
    public boolean registerSession( OndemandSession session )
    {
        int id = sessions.addElement( session );
        if( id == -1 )
            return false;
        session.setId( id );
        return true;
    }

    /**
     * Unregisters a session from this worker.
     *
     * @param session The session to unregister.
     */
    public void unregisterSession( OndemandSession session )
    {
        sessions.removeElement( session.getId() );
    }

    @Override
    public void run()
    {
        try {
            for(;;) {
                synchronized(this) {
                    if( !isRunning )
                        break;
                }
                long startTime = System.currentTimeMillis();
                if( sessions.getSize() > 0 ) {
                     for( OndemandSession session : sessions ) {
                        session.update();
                     }
                }
                long takenTime = System.currentTimeMillis() - startTime;
                if( takenTime < 50L ) {
                    Thread.sleep( 50L - takenTime );
                }
            }
        } catch( Throwable t ) {
            logger.log(Level.INFO, "Exception thrown while handling the logic for the ondemand worker");
            t.printStackTrace();
        }
    }

    /**
     * Starts this ondemand worker.
     */
    public void start( )
    {
        if( !isRunning ) {
            isRunning = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * Gets the instance of this class.
     *
     * @return The instance.
     */
    public static OndemandWorker getInstance( )
    {
        if( instance == null )
            instance = new OndemandWorker( );
        return instance;
    }
}