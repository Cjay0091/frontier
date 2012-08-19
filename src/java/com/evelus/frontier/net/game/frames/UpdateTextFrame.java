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
public final class UpdateTextFrame extends OutgoingFrame {

    /**
     * Constructs a new {@link UpdateTextFrame};
     *
     * @param parentId The parent id of the widget to update the text for.
     * @param childId The child id of the widget to update the text for.
     * @param text The text for the widget.
     */
    public UpdateTextFrame ( int parentId , int childId , String text )
    {
        this( parentId << 16 | childId , text );
    }

    /**
     * Constructs a new {@link UpdateTextFrame};
     *
     * @param hash The hash of the widget to set the text for.
     * @param text The text for the widget.
     */
    public UpdateTextFrame ( int hash , String text )
    {
        super( 180 , OutgoingFrame.BYTE_SIZE );
        this.hash = hash;
        this.text = text;
    }

    /**
     * The hash of the widget to update the text for.
     */
    private int hash;

    /**
     * The text of the widget to update.
     */
    private String text;

    @Override
    public void encode( Buffer buffer )
    {
        buffer.putDwordLe( hash );
        buffer.putJstr( text );
    }
}