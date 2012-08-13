/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game;

import java.util.logging.Logger;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class OdFrameDecoder implements FrameDecoder {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger(OdFrameDecoder.class.getSimpleName());

    @Override
    public void decode( Session session , IncomingFrame incomingFrame )
    {
        System.out.println( "DERP" );
    }
}