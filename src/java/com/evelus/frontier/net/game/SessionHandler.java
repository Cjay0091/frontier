/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */
package com.evelus.frontier.net.game;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public interface SessionHandler {

    /**
     * Decodes an incoming frame.
     *
     * @param incomingFrame The incoming frame to decode.
     */
    public void decode(IncomingFrame incomingFrame);

    /**
     * Destroys this session handler.
     */
    public void destroy();
}
