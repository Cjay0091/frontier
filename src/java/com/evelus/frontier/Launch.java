/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class Launch {

    /**
     * The main entry point for the program.
     * @param args The command line arguments.
     */
    public static void main( String[] args )
    {
        Server.getInstance().setId( Integer.parseInt(args[0]) );
        int state = -1;
        if( args[1].equals("live") ) {
            state = Server.LIVE_STATE;
        } else
            throw new RuntimeException("launch mode '" + args[1] + "' not recognized.");
        Server.getInstance().setState( state );
    }
}