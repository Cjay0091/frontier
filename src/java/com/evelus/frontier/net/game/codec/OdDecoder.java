/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game.codec;

import com.evelus.frontier.net.game.IncomingFrame;
import com.evelus.frontier.net.game.Session;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class OdDecoder extends FrameDecoder {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(OdDecoder.class.getSimpleName());

    /**
     * The instance of this class.
     */
    private static OdDecoder instance;

    /**
     * Constructs a new {@link OdDecoder};
     */
    private OdDecoder ( ) { }

    @Override
    protected Object decode( ChannelHandlerContext chc , Channel chnl , ChannelBuffer channelBuffer ) throws Exception
    {
        boolean isIncomplete = channelBuffer.readableBytes() < 3;
        int id = channelBuffer.readByte() & 0xFF;
        if( id > 4 || isIncomplete ) {
            if( isIncomplete )
                logger.log(Level.INFO, "Incomplete request sent from client [id=" + id + "]");
            else
                logger.log(Level.INFO, "Unknown request sent from client [id=" + id + "]");
            return IncomingFrame.INVALID_FRAME;
        }
        IncomingFrame incomingFrame = new IncomingFrame(id, 3);
        channelBuffer.readBytes( incomingFrame.getPayload() );
        return incomingFrame;
    }

    /**
     * Gets the instance of this class.
     *
     * @return The instance.
     */
    public static OdDecoder getInstance( )
    {
        if( instance == null )
            instance = new OdDecoder();
        return instance;
    }
}