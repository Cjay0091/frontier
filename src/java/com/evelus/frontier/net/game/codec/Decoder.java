/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game.codec;

import com.evelus.frontier.net.game.IncomingFrame;
import com.evelus.frontier.net.game.Session;
import com.evelus.frontier.util.ISAACCipher;
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
public final class Decoder extends OneToOneDecoder {

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
    }

    /**
     * The session for this decoder.
     */
    private Session session;

    @Override
    protected Object decode(ChannelHandlerContext chc, Channel chnl, Object obj) throws Exception {
        if( obj instanceof ChannelBuffer ) {
            ChannelBuffer buffer = (ChannelBuffer) obj;
            while( buffer.readableBytes() > 0 ) {
                int id = buffer.readByte();
                if( session.getIncomingIsaac() != null ) {
                    id = id - session.getIncomingIsaac().getNextValue();
                }
                id &= 0xFF;
                int size = FRAME_SIZES[ id ];
                if( size == UNUSED) {
                    logger.log(Level.INFO, "Unused frame sent from client [id=" + id + "]");
                    return false;
                }
                if( size == BYTE_SIZE )
                    size = buffer.readByte() & 0xFF;
                else if( size == WORD_SIZE )
                    size = buffer.readShort() & 0xFFFF;
                if( buffer.readableBytes() < size ) {
                    logger.log(Level.INFO, "Incomplete frame sent from client [id=" + id + "]");
                    return false;
                }
                IncomingFrame frame = new IncomingFrame( id , size );
                buffer.readBytes( frame.getPayload() );
                session.queueFrame( frame );
            }
            return true;
        } else
            throw new RuntimeException();
    }

    static {
        FRAME_SIZES = new int[ 256 ];
        for( int i = 0 ; i < FRAME_SIZES.length ; i++ )
            FRAME_SIZES[ i ] = UNUSED;
        FRAME_SIZES[ 15 ] = 4;                           // Ondemand connect
    }
}
