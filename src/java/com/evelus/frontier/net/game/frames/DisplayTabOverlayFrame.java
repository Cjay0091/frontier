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
public final class DisplayTabOverlayFrame extends OutgoingFrame {

    /**
     * Constructs a new {@link DisplayWidgetFrame};
     *
     * @param widgetParentId The parent id of the widget to display.
     * @param tabParentId The parent id of the widget for the tab to display.
     */
    public DisplayTabOverlayFrame ( int widgetParentId , int tabParentId )
    {
        super( 146 , OutgoingFrame.STATIC_SIZE );
        this.widgetParentId = widgetParentId;
        this.tabParentId = tabParentId;
    }

    /**
     * The parent id of the widget to display.
     */
    private int widgetParentId;

    /**
     * The parent id of the tab to display.
     */
    private int tabParentId;

    @Override
    public void encode(Buffer buffer)
    {
        buffer.putWordLe128( widgetParentId );
        buffer.putWord( tabParentId );
    }
}
