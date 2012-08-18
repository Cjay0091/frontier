/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game.frames;

import com.evelus.frontier.io.Buffer;
import com.evelus.frontier.net.game.OutgoingFrame;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class SendMessageFrame extends OutgoingFrame {
    
    /**
     * Constructs a new {@link SendMessageFrame};
     * 
     * @param message The message to send to the client.
     */
    public SendMessageFrame ( String message ) 
    {
        super( 157 , OutgoingFrame.BYTE_SIZE );
        this.message = message;
    }
 
    /**
     * The string message to send to the client.
     */
    private String message;

    @Override
    public void encode( Buffer buffer ) 
    {
        buffer.putJstr( message );
    }
}