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
public interface SessionHandler {

    /**
     * Handles an incoming frame.
     *
     * @param incomingFrame The incoming frame to handle.
     */
    public void handleIncomingFrame( IncomingFrame incomingFrame );

    /**
     * Handles an update.
     */
    public void update( );

}
