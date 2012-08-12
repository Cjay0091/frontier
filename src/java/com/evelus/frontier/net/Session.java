/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class Session {

    /**
     * Constructs a new {@link Session};
     */
    public Session ( )
    {
        frameQueue = new LinkedList<IncomingFrame>();
    }

    /**
     * The handler for this session.
     */
    private SessionHandler handler;

    /**
     * The queue of incoming frames.
     */
    private Queue<IncomingFrame> frameQueue;

    /**
     * The id of this session.
     */
    private int id;

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
     * Handles an incoming frame for this session.
     *
     * @return If a frame was handled.
     */
    public boolean handleFrame( )
    {
        if( handler == null || frameQueue.isEmpty() )
            return false;
        handler.handleIncomingFrame( frameQueue.poll() );
        return true;
    }

    /**
     * Updates this session.
     */
    public void update()
    {
        if( handler != null )
            handler.update();
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
}
