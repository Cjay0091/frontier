/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier;

import com.evelus.frontier.game.ondemand.OndemandWorker;
import com.evelus.frontier.net.game.Session;
import com.evelus.frontier.net.game.PipelineFactory;
import com.evelus.frontier.util.LinkedArrayList;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class Server {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(Server.class.getSimpleName());

    /**
     * The offline server state.
     */
    public final static int OFFLINE_STATE = 0;

    /**
     * The online live server state.
     */
    public final static int LIVE_STATE = 1;

    /**
     * The instance of this class.
     */
    private static Server instance;

    /**
     * Constructs a new {@link Server};
     */
    private Server ( )
    {
        state = OFFLINE_STATE;
        serverId = -1;
    }

    /**
     * The server bootstrap for this server.
     */
    private ServerBootstrap serverBootstrap;

    /**
     * The id of this server.
     */
    private int serverId;
    
    /**
     * The sessions currently active for this server.
     */
    private LinkedArrayList<Session> sessions;

    /**
     * The current state of this server.
     */
    private int state;

    /**
     * The thread for this server.
     */
    private Thread thread;

    /**
     * Registers a session to the server.
     *
     * @param session The session to register.
     * @return If the session was successfully registered.
     */
    public boolean registerSession( Session session )
    {
        int id = sessions.addElement( session );
        if( id == -1 )
            return false;
        session.setId( id );
        return true;
    }

    /**
     * Unregisters a session from the server.
     *
     * @param session The session to unregister.
     */
    public void unregisterSession( Session session )
    {
        sessions.removeElement( session.getId() );
    }

    /**
     * Sets the id of the server.
     *
     * @param i The id value.
     */
    public void setId( int i )
    {
        serverId = i;
    }

    /**
     * Sets the state of the server.
     *
     * @param i The state value.
     */
    public void setState( int i )
    {
        if( i != state ) {
            if( i == LIVE_STATE ) {
                if(serverId == -1) {
                    throw new IllegalStateException("server id is not set yet.");
                }
                sessions = new LinkedArrayList<Session>( 2048 );
                OndemandWorker.getInstance().start();
                bind( 40000 + serverId );
            }
            if( i == OFFLINE_STATE ) {
                removeSessions( );
                unbind( );
            }
            state = i;
            logger.log(Level.INFO, "Server state changed [state=" + getNameForState( i ) + "]");
        }
    }

    /**
     * Gets the state of the server.
     *
     * @return The state.
     */
    public int getState( )
    {
        return state;
    }

    /**
     * Binds this server to a port.
     *
     * @param port The port to bind the server to.
     */
    private void bind( int port )
    {
        serverBootstrap = new ServerBootstrap( new NioServerSocketChannelFactory(
                                                      Executors.newCachedThreadPool(),
                                                      Executors.newCachedThreadPool()) );
        serverBootstrap.setPipelineFactory( PipelineFactory.getInstance() );
        serverBootstrap.bind( new InetSocketAddress(port) );
        logger.log(Level.INFO, "Server successfully bound [port=" + port + "]");
    }

    /**
     * Removes all the sessions for the server.
     */
    private void removeSessions( )
    {
        sessions = null;
    }

    /**
     * Unbinds the server bootstrap.
     */
    private void unbind( )
    {
        serverBootstrap.releaseExternalResources();
        serverBootstrap = null;
        logger.log(Level.INFO, "Server successfully unbound");
    }

    /**
     * Gets the name for a server state.
     *
     * @param i The state value.
     * @return The name of the state.
     */
    private static String getNameForState( int i )
    {
        switch(i) {
            case OFFLINE_STATE:
                return "OFFLINE";
            case LIVE_STATE:
                return "LIVE";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * Gets the instance of this server.
     *
     * @return The instance.
     */
    public static Server getInstance( )
    {
        if( instance == null )
            instance = new Server();
        return instance;
    }
}