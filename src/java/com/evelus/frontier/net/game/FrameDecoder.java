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
public interface FrameDecoder {

    /**
     * Decodes an incoming frame.
     *
     * @param session The session from which the frame came from.
     * @param incomingFrame The incoming frame to handle.
     */
    public void decode( Session session, IncomingFrame incomingFrame );

}
