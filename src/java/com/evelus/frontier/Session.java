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
public final class Session {

    /**
     * Constructs a new {@link Session};
     */
    public Session ( ) { }

    /**
     * The id of this session.
     */
    private int id;

    /**
     * Sets the id of this session.
     *
     * @param id The id value.
     */
    public void setId( int id )
    {
        this.id = id;
    }

    /**
     * Gets the id of this session.
     *
     * @return The id.
     */
    public int getId( )
    {
        return id;
    }
}
