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
public final class Decoder extends FrameDecoder {

    /**
     * The instance logger for this class.
     */
    private static final Logger logger = Logger.getLogger( Decoder.class.getSimpleName() );

    /**
     * The unused frame size value.
     */
    private static final int UNUSED = -3;

    /**
     * The word size value.
     */
    private static final int WORD_SIZE = -2;

    /**
     * The byte size value.
     */
    private static final int BYTE_SIZE = -1;

    /**
     * The sizes of all the frames in this decoder.
     */
    private static final int[] FRAME_SIZES;

    /**
     * Constructs a new {@link Decoder};
     */
    public Decoder ( Session session )
    {
        this.session = session;
        frameId = -1;
    }

    /**
     * The session for this decoder.
     */
    private Session session;

    /**
     * The current frame id.
     */
    private int frameId;

    /**
     * The current frame size.
     */
    private int frameSize;

    @Override
    protected Object decode(ChannelHandlerContext chc, Channel chnl, ChannelBuffer buffer) throws Exception {
        if( frameId == -1 ) {
            frameId = buffer.readByte();
            if( session.getIncomingIsaac() != null ) {
                frameId = frameId - session.getIncomingIsaac().getNextValue();
            }
            frameId &= 0xFF;
            frameSize = FRAME_SIZES[ frameId ];
            if( frameSize == UNUSED ) {
                buffer.readerIndex( buffer.capacity() );
                logger.log(Level.INFO, "Unused frame sent from client [id=" + frameId + "]");
                return IncomingFrame.INVALID_FRAME;
            }
        }
        if( frameSize == BYTE_SIZE || frameSize == WORD_SIZE ) {
            int required = 1;
            if( frameSize == WORD_SIZE ) {
                required = 2;
            }
            if( buffer.readableBytes() < required ) {
                return null;
            }
            if( frameSize == BYTE_SIZE ) {
                frameSize = buffer.readByte() & 0xFF;
            } else if( frameSize == WORD_SIZE ) {
                frameSize = buffer.readShort() & 0xFFFF;
            }
        }
        if( buffer.readableBytes() < frameSize ) {
            return null;
        }
        IncomingFrame frame = new IncomingFrame( frameId , frameSize );
        buffer.readBytes( frame.getPayload() );
        frameId = -1;
        return frame;
    }

    static {
        FRAME_SIZES = new int[ 256 ];
        for( int i = 0 ; i < FRAME_SIZES.length ; i++ ) {
            FRAME_SIZES[ i ] = UNUSED;
        }
        FRAME_SIZES[  14 ] =  1;                           // Login server select
        FRAME_SIZES[  15 ] =  4;                           // Ondemand connect
        FRAME_SIZES[  16 ] = -1;                           // Login request
        FRAME_SIZES[  21 ] =  0;                           // Dunno
        FRAME_SIZES[  54 ] =  4;                           // Widget click
        FRAME_SIZES[  86 ] =  0;                           // Dunno
        FRAME_SIZES[ 141 ] =  4;                           // Dunno
        FRAME_SIZES[ 162 ] =  4;                           // Mouse click
        FRAME_SIZES[ 174 ] = -1;                           // Command
        FRAME_SIZES[ 207 ] =  1;                           // Focus
    }
}
