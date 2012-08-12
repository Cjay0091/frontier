/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game;

import com.evelus.frontier.util.ISAACCipher;
import java.util.LinkedList;
import java.util.Queue;
import org.jboss.netty.channel.Channel;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class Session {

    /**
     * Constructs a new {@link Session};
     *
     * @param channel The channel for this session.
     */
    public Session ( Channel channel )
    {
        frameQueue = new LinkedList<IncomingFrame>();
        this.channel = channel;
    }

    /**
     * The channel for this session.
     */
    private Channel channel;

    /**
     * The handler for this session.
     */
    private SessionHandler handler;

    /**
     * The queue of incoming frames.
     */
    private Queue<IncomingFrame> frameQueue;

    /**
     * The incoming isaac cipher for this session.
     */
    private ISAACCipher incomingIsaac;

    /**
     * The id of this session.
     */
    private int id;

    /**
     * Gets the channel for this session.
     *
     * @return The channel.
     */
    public Channel getChannel( )
    {
        return channel;
    }

    /**
     * Sets the handler for this session.
     *
     * @param handler The handler.
     */
    public void setHandler( SessionHandler handler )
    {
        this.handler = handler;
    }

    /**
     * Queues an incoming frame for this session.
     *
     * @param frame The frame to queue.
     */
    public void queueFrame( IncomingFrame frame )
    {
        frameQueue.add( frame );
    }

    /**
     * Handles an incoming frame for this session.
     *
     * @return If a frame was handled.
     */
    public boolean handleFrame( )
    {
        if( handler == null || frameQueue.isEmpty() )
            return false;
        handler.handleIncomingFrame( this , frameQueue.poll() );
        return true;
    }

    /**
     * Updates this session.
     */
    public void update()
    {
        if( handler != null )
            handler.update( this );
    }

    /**
     * Sets the id of this session.
     *
     * @param id The id value.
     */
    public void setId( int id )
    {
        this.id = id;
    }

    /**
     * Gets the id of this session.
     *
     * @return The id.
     */
    public int getId( )
    {
        return id;
    }

    /**
     * Gets the incoming ISAAC.
     *
     * @return The incoming ISAAC.
     */
    public ISAACCipher getIncomingIsaac( )
    {
        return incomingIsaac;
    }
}
