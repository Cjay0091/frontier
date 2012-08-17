/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game.frames;

import com.evelus.frontier.io.Buffer;
import com.evelus.frontier.net.game.OutgoingFrame;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class RebuildMapFrame extends OutgoingFrame {

    /**
     * Constructs a new {@link RebuildMapFrame};
     */
    public RebuildMapFrame( int sPositionX , int sPositionY , int mPositionX , int mPositionY )
    {
        super( 121 , WORD_SIZE );
        this.sPositionX = sPositionX;
        this.sPositionY = sPositionY;
        this.mPositionX = mPositionX;
        this.mPositionY = mPositionY;
    }
    
    /**
     * The sector position x.
     */
    private int sPositionX;
    
    /**
     * The sector position y.
     */
    private int sPositionY;
    
    /**
     * The map position x.
     */
    private int mPositionX;
    
    /**
     * The map position y.
     */
    private int mPositionY;
    
    @Override
    public void encode(Buffer buffer) 
    {
        buffer.putWordLe( sPositionX );
        buffer.putWordLe( mPositionX );
        buffer.putWordLe128( mPositionY );
        buffer.putByte128( 0 );
        boolean isLoaded = false;
        if ((sPositionX / 8 == 48 || sPositionX / 8 == 49) && sPositionY / 8 == 48) {
            isLoaded = true;
        }
        if (sPositionX / 8 == 48 && sPositionY / 8 == 148) {
            isLoaded = true;
        }
        for (int x = (sPositionX - 6) / 8; (sPositionX + 6) / 8 >= x; x++) {
            for (int y = (sPositionY - 6) / 8; y <= (sPositionY + 6) / 8; y++) {
                if (!isLoaded || (y != 49 && y != 149 && y != 147 && x != 50 && (x != 49 || y != 47))) {
                    for( int i = 0 ; i < 4 ; i++ ) {
                        buffer.putDword(0);
                    }
                }
            }
        }
        buffer.putWord128( sPositionY);
    }
}
