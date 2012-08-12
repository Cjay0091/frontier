/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class IncomingFrame {

    /**
     * Constructs a new {@link IncomingFrame};
     *
     * @param id The id of the frame.
     * @param size The size of the frame.
     */
    public IncomingFrame ( int id , int size )
    {
        this.id = id;
        buffer = new byte[ size ];
    }

    /**
     * The id of this frame.
     */
    private int id;

    /**
     * The buffer for this frame.
     */
    private byte[] buffer;

    /**
     * Gets the id of this frame.
     *
     * @return The id.
     */
    public int getId( )
    {
        return id;
    }

    /**
     * Gets the buffer of this frame.
     *
     * @return The buffer.
     */
    public byte[] getPayload( )
    {
        return buffer;
    }
}
