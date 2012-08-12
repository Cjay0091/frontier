/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game;

import com.evelus.frontier.io.Buffer;
import com.evelus.frontier.net.game.codec.OdDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFutureListener;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public class InitialHandler implements SessionHandler {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(InitialHandler.class.getSimpleName());

    /**
     * The instance of this class.
     */
    private static InitialHandler instance;

    /**
     * Constructs a new {@link InitialHandler};
     */
    private InitialHandler ( ) { }

    @Override
    public void handleIncomingFrame( Session session , IncomingFrame incomingFrame )
    {
        int id = incomingFrame.getId();
        Buffer buffer = new Buffer( incomingFrame.getPayload() );
        switch( id ) {
            case 15:
                handleOdConnect( session , buffer );
                return;
            default:
                logger.log(Level.INFO, "Unknown initial connection frame id [id=" + id + "]");
                return;
        }
    }

    @Override
    public void update( Session session ) { }

    /**
     * Handles an incoming ondemand connection request.
     *
     * @param session The session from which the request came from.
     * @param buffer The buffer to parse the request from.
     */
    private static void handleOdConnect( Session session , Buffer buffer ) {
        Channel channel = session.getChannel();
        int revision = buffer.getDword();
        if( revision != 443 ) {
            logger.log(Level.INFO, "Unexpected client revision sent on od connect [revision=" + revision + "]");
            ChannelBuffer channelBuffer = ChannelBuffers.buffer(1);
            channelBuffer.writeByte(6);
            channel.write( channelBuffer ).addListener(ChannelFutureListener.CLOSE);
        } else {
            channel.getPipeline().replace( "decoder" , "oddecoder" , new OdDecoder(session) );
            session.setHandler( new OdHandler() );
            ChannelBuffer channelBuffer = ChannelBuffers.buffer(1);
            channelBuffer.writeByte(0);
            channel.write( channelBuffer );
        }
    }

    /**
     * Gets the instance of this class.
     *
     * @return The instance.
     */
    public static InitialHandler getInstance( )
    {
        if( instance == null )
            instance = new InitialHandler( );
        return instance;
    }
}