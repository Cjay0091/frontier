/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */
package com.evelus.frontier.net.game;

import com.evelus.frontier.util.ISAACCipher;
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
    public Session(Channel channel) {
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
     * The incoming isaac cipher for this session.
     */
    private ISAACCipher incomingIsaac;

    /**
     * The outgoing isaac cipher for this session.
     */
    private ISAACCipher outgoingIsaac;
    
    /**
     * The id of this session.
     */
    private int id;

    /**
     * Gets the channel for this session.
     *
     * @return The channel.
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Sets the handler of this session.
     *
     * @param handler The session handler.
     */
    public void setHandler(SessionHandler handler) {
        this.handler = handler;
    }

    /**
     * Sets the id of this session.
     *
     * @param id The id value.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the id of this session.
     *
     * @return The id.
     */
    public int getId() {
        return id;
    }

    /**
     * Initializes the incoming ISAAC.
     * 
     * @param seeds The seeds for the ISAAC.
     */
    public void initIncomingIsaac(int[] seeds) {
        incomingIsaac = new ISAACCipher(seeds);
    }

    /**
     * Gets the incoming ISAAC.
     *
     * @return The incoming ISAAC.
     */
    public ISAACCipher getIncomingIsaac() {
        return incomingIsaac;
    }

    /**
     * Initializes the outgoing ISAAC.
     * 
     * @param seeds The seeds for the ISAAC.
     */
    public void initOutgoingIsaac(int[] seeds) {
        outgoingIsaac = new ISAACCipher(seeds);
    }

    /**
     * Gets the outgoing ISAAC.
     * 
     * @return The outgoing ISAAC.
     */
    public ISAACCipher getOutgoingIsaac() {
        return outgoingIsaac;
    }

    /**
     * Decodes an incoming frame.
     *
     * @param frame The incoming frame.
     */
    public void decodeFrame(IncomingFrame frame) {
        if (handler != null) {
            handler.decode(frame);
        }
    }

    /**
     * Destroys this session.
     */
    public void destroy() {
        if (handler != null) {
            handler.destroy();
        }
        channel.close();
    }
}
