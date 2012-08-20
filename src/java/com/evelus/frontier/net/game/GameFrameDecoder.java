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
public final class GameFrameDecoder implements FrameDecoder {

    /**
     * Constructs a new {@link GameFrameDecoder};
     * 
     * @param handler The session handler for this frame decoder.
     */
    public GameFrameDecoder ( GameSessionHandler handler ) 
    {
        this.handler = handler;
    }
    
    /**
     * The handler for this frame decoder.
     */
    private GameSessionHandler handler;
    
    @Override
    public void decode( Session session , IncomingFrame incomingFrame ) 
    {
        int id = incomingFrame.getId();
        Buffer buffer = new Buffer( incomingFrame.getPayload() );
        switch( id ) {
            case 29:
                handler.handleEquip( buffer.getDwordA() , buffer.getUword() , buffer.getUword128() );
                return;
            case 54:
                handler.handleClickButton( buffer.getDword() );
                return;
            case 70:
                handler.handleCloseWidgets( );
                return;
            case 174:
                handler.handleCommand( buffer.getJstr() );
                return;
        }
    }
}
