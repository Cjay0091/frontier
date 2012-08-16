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
     * Constructs a new {@link Buffer};
     *
     * @param size The size of the buffer to create.
     */
    public Buffer( int size )
    {
        payload = new byte[ size ];
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
     * Puts a byte into the payload.
     *
     * @param value The value of the byte to put.
     */
    public void putByte( int value )
    {
        payload[ offset++ ] = (byte) value;
    }

    /**
     * Gets an unsigned byte from the payload.
     *
     * @return The byte value.
     */
    public int getUbyte( )
    {
        return payload[ offset++ ] & 0xFF;
    }

    /**
     * Gets an unsigned word from the payload.
     *
     * @return The word value.
     */
    public int getUword( )
    {
        offset += 2;
        return (payload[ offset - 2 ] & 0xFF) << 8 | (payload[ offset - 1] & 0xFF);
    }

    /**
     * Gets an unsigned tri from the payload.
     *
     * @return The tri value.
     */
    public int getUtri( )
    {
        offset += 3;
        return (payload[ offset - 3 ] & 0xFF) << 16 | 
               (payload[ offset - 2 ] & 0xFF) << 8  |
               (payload[ offset - 1] & 0xFF);
    }

    /**
     * Puts a dword value into the payload.
     *
     * @param value The value to put.
     */
    public void putDword( int value )
    {
        payload[ offset++ ] = (byte) (value >> 24);
        payload[ offset++ ] = (byte) (value >> 16);
        payload[ offset++ ] = (byte) (value >> 8);
        payload[ offset++ ] = (byte)  value;
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
     * Gets a qword value from the payload.
     *
     * @return The qword value.
     */
    public long getQword( )
    {
        offset += 8;
        return (payload[offset - 8] & 0xFFL) << 56 |
               (payload[offset - 7] & 0xFFL) << 48 |
               (payload[offset - 6] & 0xFFL) << 40 |
               (payload[offset - 5] & 0xFFL) << 32 |
               (payload[offset - 4] & 0xFFL) << 24 |
               (payload[offset - 3] & 0xFFL) << 16 |
               (payload[offset - 2] & 0xFFL) << 8  |
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