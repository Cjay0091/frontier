/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.ondemand;

import com.evelus.frontier.io.ArchiveManager;
import com.evelus.frontier.io.Buffer;
import com.evelus.frontier.net.game.Session;
import java.util.LinkedList;
import java.util.Queue;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class OndemandSession {

    /**
     * Constructs a new {@link OdSession};
     */
    public OndemandSession ( Session session )
    {
        this.session = session;
        priorityRequests = new LinkedList<Integer>();
        regularRequests = new LinkedList<Integer>();
        currentRequest = -1;
        size = -1;
    }

    /**
     * The session for this ondemand session.
     */
    private Session session;

    /**
     * The queue of priority requests.
     */
    private Queue<Integer> priorityRequests;

    /**
     * The queue of regular requests.
     */
    private Queue<Integer> regularRequests;

    /**
     * The id of this ondemand session.
     */
    private int id;

    /**
     * The current request hash.
     */
    private int currentRequest;

    /**
     * The current offset in the archive of the request being written.
     */
    private int offset;

    /**
     * The current block offset of the archive being written.
     */
    private int blockOffset;

    /**
     * The current block id of the archive being written.
     */
    private int blockId;

    /**
     * The size of the archive being written.
     */
    private int size;

    /**
     * Sets the id of this ondemand session.
     *
     * @param id The id value.
     */
    public void setId( int id )
    {
        this.id = id;
    }

    /**
     * Gets the id of this ondemand session.
     *
     * @return The id.
     */
    public int getId( )
    {
        return id;
    }

    /**
     * Queues a priority request for this session.
     *
     * @param hash The hash of the archive to request.
     */
    public void queuePriorityRequest( int hash )
    {
        priorityRequests.add( hash );
    }

    /**
     * Queues a regular request for this session.
     *
     * @param hash The hash of the archive to request.
     */
    public void queueRegularRequest( int hash )
    {
        regularRequests.add( hash );
    }

    /**
     * Updates this session.
     */
    public void update( )
    {
        if( currentRequest == -1 ) {
            if( !priorityRequests.isEmpty() ) {
                currentRequest = priorityRequests.poll();
            } else if( !regularRequests.isEmpty() ) {
                currentRequest = regularRequests.poll();
            }
            if( currentRequest == -1)
                return;
            offset = 0;
            blockId = 0;
        }
        int indexId = currentRequest >> 16;
        int archiveId = currentRequest & 0xFFFF;
        byte[] src = ArchiveManager.getArchive( indexId , archiveId );       
        int writeAmount = 512;
        if( size == -1 ) {
            Buffer buffer = new Buffer(src);
            int compressionId = buffer.getUbyte();
            size = buffer.getDword() + (compressionId == 0 ? 5 : 9);
        }
        if( offset + writeAmount > size )
            writeAmount = size - offset;
        int bufferSize = writeAmount;
        if( offset == 0 )
            bufferSize += 8;
        bufferSize += bufferSize / 512 + 1;
        ChannelBuffer channelBuffer = ChannelBuffers.buffer( bufferSize );
        if( offset == 0 ) {         
            channelBuffer.writeByte( indexId );
            channelBuffer.writeShort( archiveId );
            blockOffset = 3;
        }
        for( ; writeAmount > 0 ; ) {
            int blockBytes = 512 - blockOffset;
            if( blockBytes > writeAmount )
                blockBytes = writeAmount;
            channelBuffer.writeBytes( src , offset , blockBytes );
            writeAmount -= blockBytes;
            blockOffset += blockBytes;
            offset += blockBytes;
            if( blockOffset == 512 && offset != size ) {
                channelBuffer.writeByte( 255 );
                blockOffset = 1;
                blockId++;
            }
        }
        if( offset == size ) {
            currentRequest = -1;
            size = -1;
        }
        session.getChannel().write(channelBuffer);
    }
}