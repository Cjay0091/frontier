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
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class OdDecoder extends OneToOneDecoder {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(OdDecoder.class.getSimpleName());

    /**
     * Constructs a new {@link OdDecoder};
     *
     * @param session The session for this decoder.
     */
    public OdDecoder ( Session session )
    {
        this.session = session;
    }

    /**
     * The session for this decoder.
     */
    private Session session;

    @Override
    protected Object decode( ChannelHandlerContext chc , Channel chnl , Object obj ) throws Exception
    {
        if( obj instanceof ChannelBuffer ) {
            ChannelBuffer channelBuffer = (ChannelBuffer) obj;
            while( channelBuffer.readableBytes() > 0 ) {
                int id = channelBuffer.readByte() & 0xFF;
                if( id > 4 ) {
                    logger.log(Level.INFO, "Unknown request sent from client [id=" + id + "]");
                    return false;
                }
                if( channelBuffer.readableBytes() < 3 ) {
                    logger.log(Level.INFO, "Incomplete request sent from client [id=" + id + "]");
                    return false;
                }
                IncomingFrame incomingFrame = new IncomingFrame(id, 3);
                channelBuffer.readBytes( incomingFrame.getPayload() );
                session.queueFrame( incomingFrame );
            }
            return true;
        } else
            throw new RuntimeException();
    }
}