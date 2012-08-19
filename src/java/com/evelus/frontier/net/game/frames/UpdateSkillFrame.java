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
public final class UpdateSkillFrame extends OutgoingFrame {

    /**
     * Constructs a new {@link UpdateSkillFrame};
     */
    public UpdateSkillFrame ( int id , int dynamicLevel , int experience )
    {
        super( 58 , OutgoingFrame.STATIC_SIZE );
        this.id = id;
        this.dynamicLevel = dynamicLevel;
        this.experience = experience;
    }

    /**
     * The id of the skill to update.
     */
    private int id;

    /**
     * The dynamic level of the skill.
     */
    private int dynamicLevel;

    /**
     * The experience of the skill.
     */
    private int experience;

    @Override
    public void encode(Buffer buffer)
    {
        buffer.putByte( id );
        buffer.putByte128( dynamicLevel );
        buffer.putDwordB( experience );
    }
}