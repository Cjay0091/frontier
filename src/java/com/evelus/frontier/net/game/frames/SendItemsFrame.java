/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game.frames;

import com.evelus.frontier.game.items.GameItem;
import com.evelus.frontier.game.items.ItemContainer;
import com.evelus.frontier.io.Buffer;
import com.evelus.frontier.net.game.OutgoingFrame;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class SendItemsFrame extends OutgoingFrame {
    
    /**
     * Constructs a new {@link SendItemsFrame};
     * 
     * @param parentId The parent widget id.
     * @param childId The child widget id.
     */
    public SendItemsFrame ( int parentId , int childId ,ItemContainer itemContainer ) 
    { 
        super( 228 , OutgoingFrame.WORD_SIZE );
        this.itemContainer = itemContainer;
        hash = parentId << 16 | childId;
    }
    
    /**
     * The item container to use for this frame.
     */
    private ItemContainer itemContainer;
    
    /**
     * The widget hash for this frame.
     */
    private int hash;

    @Override
    public void encode( Buffer buffer ) 
    {
        buffer.putDword( hash );
        buffer.putWord( 0 );
        int size = itemContainer.getSize();
        buffer.putWord( size );
        for( int i = 0 ; i < size ; i++ ) {
            GameItem gameItem = itemContainer.getItem( i );
            if( gameItem == null ) {
                buffer.putByteA( 0 );
                buffer.putWord( 0 );
                continue;
            }
            int amount = gameItem.getAmount();
            if( amount < 255 ) {
                buffer.putByteA( amount );
            } else {
                buffer.putByteA( 255 );
                buffer.putDword( amount );
            }
            buffer.putWord( gameItem.getId() + 1 );
        }
    }
}