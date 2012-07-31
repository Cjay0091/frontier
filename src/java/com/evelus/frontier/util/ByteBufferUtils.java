/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.util;

import java.nio.ByteBuffer;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class ByteBufferUtils {

    /**
     * Prevent construction;
     */
    private ByteBufferUtils ( ) { }

    /**
     * Gets a custom encoded string from a bytebuffer.
     *
     * @param byteBuffer    The bytebuffer to get the string from.
     * @return              The string from the bytebuffer.
     */
    public static String gstr( ByteBuffer byteBuffer ) {
        int length = byteBuffer.get() & 0xFF;
        byte[] bytes = new byte[length];
        byteBuffer.get(bytes);
        return new String(bytes);
    }
}