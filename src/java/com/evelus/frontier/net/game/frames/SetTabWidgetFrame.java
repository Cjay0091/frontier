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
public final class SetTabWidgetFrame extends OutgoingFrame {
    
    /**
     * Constructs a new {@link SetTabWidgetFrame};
     */
    public SetTabWidgetFrame ( int tabId , int parentId ) 
    { 
        super( 90 , OutgoingFrame.STATIC_SIZE );
        this.tabId = tabId;
        this.parentId = parentId;
    }
    
    /**
     * The id of the tab to set the parent widget id for.
     */
    private int tabId;
    
    /**
     * The parent widget id.
     */
    private int parentId;

    @Override
    public void encode( Buffer buffer ) 
    {
        buffer.putByte( tabId );
        buffer.putWord128( parentId );
    }
}