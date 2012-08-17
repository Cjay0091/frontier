/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game;

import com.evelus.frontier.io.Buffer;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public abstract class OutgoingFrame {
    
    /**
     * The opcode for a static sized frame.
     */
    public static final int STATIC_SIZE = 0;
    
    /**
     * The opcode for a variable byte sized frame.
     */
    public static final int BYTE_SIZE = 1;
    
    /**
     * The opcode for a variable word sized frame.
     */
    public static final int WORD_SIZE = 2;
        
    /**
     * Constructs a new {@link OutgoingFrame};
     * 
     * @param id The id of the frame.
     * @param size The size of the frame.
     */
    public OutgoingFrame( int id , int size )
    {
        this.id = id;
        this.size = size;
    }
    
    /**
     * The id of this frame.
     */
    private int id;
    
    /**
     * The size of this frame.
     */
    private int size;
    
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
     * Gets the size of this frame.
     * 
     * @return The size.
     */
    public int getSize( )
    {
        return size;
    }
    
    /**
     * Encodes this frame to a buffer.
     * 
     * @param buffer The buffer to encode the frame to.
     */
    public abstract void encode( Buffer buffer );
    
}