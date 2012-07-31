/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.io;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class Jagbuffer {

    /**
     * Constructs a new {@link Jagbuffer};
     * 
     * @param payload   The byte array payload to decode values from and encode
     *                  values to.
     */
    public Jagbuffer(byte[] payload) {
        this.payload = payload;
    }

    /**
     * The byte array payload that is utilized to decode/encode values.
     */
    private byte[] payload;

    /**
     * The offset pointer of the payload.
     */
    private int offset;

}