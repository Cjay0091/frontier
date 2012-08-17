/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game.codec;

import com.evelus.frontier.io.Buffer;
import com.evelus.frontier.net.game.OutgoingFrame;
import com.evelus.frontier.net.game.Session;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class FrameEncoder extends OneToOneEncoder {
    
    /**
     * The size of the buffer to create to encode frames to.
     */
    private static final int BUFFER_SIZE = 5000;
    
    /**
     * Constructs a new {@link FrameEncoder};
     * 
     * @param session The session to encode frames for.
     */
    public FrameEncoder ( Session session ) 
    {
        buffer = new Buffer( new byte[ BUFFER_SIZE ] );
        this.session = session;
    }
    
    /**
     * The session to encode frames for.
     */
    private Session session;
    
    /**
     * The buffer to encode frames to.
     */
    private Buffer buffer;

    @Override
    protected Object encode(ChannelHandlerContext chc, Channel chnl, Object o) throws Exception {
        if( !(o instanceof OutgoingFrame) ) {
            throw new RuntimeException("expected outgoingframe");
        }
        OutgoingFrame frame = (OutgoingFrame) o;
        buffer.setOffset( 0 );
        int id = frame.getId();
        if( session.getOutgoingIsaac() != null ) {
            id += session.getOutgoingIsaac().getNextValue();
        }
        buffer.putByte( id );
        int size = frame.getSize();
        if( size == OutgoingFrame.BYTE_SIZE ) {
            buffer.putByte( 0 );
        } else if( size == OutgoingFrame.WORD_SIZE ) {
            buffer.putWord( 0 );
        }
        int startOffset = buffer.getOffset();
        frame.encode( buffer );
        int endOffset = buffer.getOffset();
        if( size == OutgoingFrame.BYTE_SIZE || size == OutgoingFrame.WORD_SIZE ) {
            buffer.setOffset( 1 );
            int bytesWritten = endOffset - startOffset;
            if( size == OutgoingFrame.BYTE_SIZE ) {
                buffer.putByte( bytesWritten );
            } else {
                buffer.putWord( bytesWritten );
            }
        }
        return ChannelBuffers.copiedBuffer( buffer.getPayload() , 0 , endOffset );
    }
}