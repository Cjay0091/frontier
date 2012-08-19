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
     * The mask array.
     */
    private final static int[] MASKS;

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
        bitOffset = -1;
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
     * The current bit offset in the payload.
     */
    private int bitOffset;

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
     * Puts a byte type a into the payload.
     *
     * @param value The value of the byte to put.
     */
    public void putByteA( int value )
    {
        payload[ offset++ ] = (byte) ( -value );
    }

    /**
     * Puts a byte plus 128 into the payload.
     * 
     * @param value The value of the byte to put.
     */
    public void putByte128( int value )
    {
        payload[ offset++ ] = (byte) (value + 128);
    }
    
    /**
     * Puts a word into the payload.
     *
     * @param value The value of the word to put.
     */
    public void putWord( int value )
    {
        payload[ offset++ ] = (byte) (value >> 8);
        payload[ offset++ ] = (byte) value;
    }
    
    /**
     * Gets a word from the payload.
     * 
     * @return The word value.
     */
    public int getWord( )
    {
        offset += 2;
        int value = (payload[ offset - 2 ] & 0xFF) << 8 | (payload[ offset - 1] & 0xFF);
        if( value > 32767 ) {
            value -= 65536;
        }
        return value;
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
     * Puts a word plus 128 into the payload.
     *
     * @param value The value of the word to put.
     */
    public void putWord128( int value )
    {
        payload[ offset++ ] = (byte) (value >> 8);
        payload[ offset++ ] = (byte) (value + 128);
    }

    /**
     * Gets a word minus 128 from the payload.
     *
     * @return The word value.
     */
    public int getUword128() {
        offset += 2;
        return ((payload[offset - 2] & 0xFF) << 8) |
                (payload[offset - 1] - 128 & 0xff);
                
    }
    
    /**
     * Puts a little endian word into the payload.
     * 
     * @param value The world value to put.
     */
    public void putWordLe( int value )
    {
        payload[ offset++ ] = (byte) value;
        payload[ offset++ ] = (byte) ( value >> 8 );
    }
    
    /**
     * Puts a little endian word plus 128 into the payload.
     * 
     * @param value The world value to put.
     */
    public void putWordLe128( int value )
    {
        payload[ offset++ ] = (byte) ( value + 128 );
        payload[ offset++ ] = (byte) ( value >> 8 );
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
               (payload[offset - 1] & 0xFF);
    }

    /**
     * Gets a dword type a value from the payload.
     *
     * @return The dword value.
     */
    public int getDwordA( )
    {
        offset += 4;
        return (payload[offset - 4] & 0xFF) << 8  |
               (payload[offset - 3] & 0xFF)       |
               (payload[offset - 2] & 0xFF) << 24 |
               (payload[offset - 1] & 0xFF) << 16;
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
    
    /**
     * Gets if bit access is active for this buffer.
     * 
     * @return If bit access is active.
     */
    public boolean isBitAccessActive( )
    {
        return bitOffset != -1;
    }
    
    /**
     * Initializes the bit offset.
     */
    public void initializeBitOffset( ) 
    {
        bitOffset = offset * 8;
    }
    
    /**
     * Sets the current offset of the byte buffer from the bit offset.
     */
    public void resetBitOffset( ) 
    {
        offset = (bitOffset + 7) / 8;
        bitOffset = -1;
    }
    
    /**
     * Puts a value into the payload array.
     *
     * @param value The value to putBits into the payload array.
     * @param amountBits The amount of bits to write the value as.
     */
    public void putBits( int value , int amountBits ) 
    {
        int byteOffset = bitOffset >> 3;
        int off = 8 - (bitOffset & 7);
        bitOffset += amountBits;
        for (; amountBits > off; off = 8) {
            payload[byteOffset] &= ~MASKS[off];
            payload[byteOffset] |= value >> (amountBits - off) & MASKS[off];            
            amountBits -= off;
            byteOffset++;
        }
        if (amountBits == off) {
            payload[byteOffset] &= ~MASKS[off];
            payload[byteOffset] |= value & MASKS[off];
        } else {
            payload[byteOffset] &= ~MASKS[amountBits] << (off - amountBits);
            payload[byteOffset] |= (value & MASKS[amountBits]) << (off - amountBits);            
        }
    }
    
     static {
        MASKS = new int[32];
        for (int i = 0; i < 32; i++) {
            MASKS[i] = (1 << i) - 1;
        }
    } 
}