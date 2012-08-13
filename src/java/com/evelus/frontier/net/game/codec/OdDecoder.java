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
    public OdDecoder ( ) { }

    /**
     * The id of the current frame being parsed.
     */
    private int frameId;

    @Override
    protected Object decode( ChannelHandlerContext chc , Channel chnl , ChannelBuffer channelBuffer ) throws Exception
    {
        if( frameId == -1 ) {
            frameId = channelBuffer.readByte() & 0xFF;
            if( frameId > 4 ) {
                logger.log(Level.INFO, "Unused frame sent from client [id=" + frameId + "]");
                return IncomingFrame.INVALID_FRAME;
            }
        }
        if( channelBuffer.readableBytes() < 4 )
            return null;
        IncomingFrame incomingFrame = new IncomingFrame( frameId , 3 );
        channelBuffer.readBytes( incomingFrame.getPayload() );
        return incomingFrame;
    }
}