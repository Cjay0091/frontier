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
public final class DisplayWidgetFrame extends OutgoingFrame {

    /**
     * Constructs a new {@link DisplayWidgetFrame};
     */
    public DisplayWidgetFrame ( int parentId )
    {
        super( 160 , OutgoingFrame.STATIC_SIZE );
        this.parentId = parentId;
    }

    /**
     * The parent id of the widget to display.
     */
    private int parentId;

    @Override
    public void encode(Buffer buffer)
    {
        buffer.putWord128( parentId );
    }
}
