/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.model.mob;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public abstract class UpdateBlock {

    /**
     * The flags for all the updates in this update block.
     */
    private int flags;

    /**
     * Sets a flag active for this update block.
     *
     * @param flag The flag to set as active.
     */
    public void setActive( int flag )
    {
        flags |= flag;
    }

    /**
     * Gets the flags for this update block.
     *
     * @return The flags.
     */
    public int getFlags( )
    {
        return flags;
    }

    /**
     * Resets this update block.
     */
    public void reset( )
    {
        flags = 0;
    }
}
