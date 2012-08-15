/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game;

import com.evelus.frontier.io.Buffer;
import java.util.logging.Logger;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class OndemandFrameDecoder implements FrameDecoder {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(OndemandFrameDecoder.class.getSimpleName());

    /**
     * Constructs a new {@link OdFrameDecoder};
     *
     * @param handler The handler for this frame decoder.
     */
    public OndemandFrameDecoder( OndemandHandler handler )
    {
        this.handler = handler;
    }

    /**
     * The ondemand handler for this frame decoder.
     */
    private OndemandHandler handler;

    @Override
    public void decode( Session session , IncomingFrame incomingFrame )
    {
        int id = incomingFrame.getId();
        Buffer buffer = new Buffer( incomingFrame.getPayload() );
        switch( id ) {
            case 0:
            case 1:
                handler.queueRequest( buffer.getUbyte() , buffer.getUword() , id == 1 );
                return;
        }
    }
}