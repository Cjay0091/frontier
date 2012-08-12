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
public final class Buffer {

    /**
     * Constructs a new {@link Jagbuffer};
     *
     * @param payload   The byte array payload to decode values from and encode
     *                  values to.
     */
    public Buffer(byte[] payload) {
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

    /**
     * Gets the payload for this jagbuffer.
     *
     * @return The payload.
     */
    public byte[] getPayload( )
    {
        return payload;
    }

    /**
     * Sets the offset pointer of the payload.
     *
     * @param offset The offset value.
     */
    public void setOffset( int offset )
    {
        this.offset = offset;
    }

    /**
     * Gets the offset pointer of the payload.
     *
     * @return The offset.
     */
    public int getOffset( )
    {
        return offset;
    }

    /**
     * Gets a dword value from the payload.
     *
     * @return The dword value.
     */
    public int getDword( )
    {
        offset += 4;
        return (payload[offset - 4] & 0xFF) << 24 |
               (payload[offset - 3] & 0xFF) << 16 |
               (payload[offset - 2] & 0xFF) << 8  |
                payload[offset - 1] & 0xFF;
    }

    /**
     * Puts a jagex formatted string into the payload.
     *
     * @param str The string to encode into the payload.
     */
    public void putJstr( String str )
    {
        int length = str.length();
        System.arraycopy( str.getBytes() , 0 , payload , offset , length );
        payload[ offset + length ] = 0;
        offset += length + 1;
    }

    /**
     * Gets a jagex formatted string from the payload.
     *
     * @return The string.
     */
    public String getJstr( )
    {
        int start = offset;
        while( payload[ offset++ ] != 0 );
        return new String( payload , start , offset - start - 1 );
    }
}