/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.model.player;

import com.evelus.frontier.game.model.GamePlayer;
import com.evelus.frontier.net.game.frames.UpdateSkillFrame;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class SkillHandler {

    /**
     * The default dynamic level for each skill.
     */
    private static final int[] DEFAULT_DYNAMIC_LEVELS;

    /**
     * The default amount of experience for each skill.
     */
    private static final int[] DEFAULT_EXPERIENCE;

    /**
     * Constructs a new {@link SkillHandler};
     * 
     * @param gamePlayer The game player for this skill handler.
     */
    public SkillHandler ( GamePlayer gamePlayer )
    {
        dynamicLevels = new int[ 23 ];
        experience = new int[ 23 ];
        this.gamePlayer = gamePlayer;
        initialize( );
    }

    /**
     * The game player for this skill handler.
     */
    private GamePlayer gamePlayer;

    /**
     * The dynamic level for each skill.
     */
    private int[] dynamicLevels;

    /**
     * The amount of experience for each skill.
     */
    private int[] experience;

    /**
     * Initializes this skill handler.
     */
    private void initialize( )
    {
        for( int i = 0 ; i < 23 ; i++ ) {
            dynamicLevels[ i ] = DEFAULT_DYNAMIC_LEVELS[ i ];
            experience[ i ] = DEFAULT_EXPERIENCE[ i ];
        }
    }

    /**
     * Updates all the skills
     */
    public void updateSkills( )
    {
        for( int i = 0 ; i < 23 ; i++ ) {
            updateSkill( i );
        }
    }

    /**
     * Updates a skill.
     *
     * @param id The id of the skill to update.
     */
    public void updateSkill( int id )
    {
        gamePlayer.sendFrame( new UpdateSkillFrame( id , dynamicLevels[ id ] , experience[ id ] ) );
    }

    static {
        DEFAULT_DYNAMIC_LEVELS = new int[ 23 ];
        DEFAULT_EXPERIENCE = new int[ 23 ];
        for( int i = 0 ; i < 23 ; i++ ) {
            DEFAULT_DYNAMIC_LEVELS[ i ] = 1;
        }
        DEFAULT_DYNAMIC_LEVELS[ 3 ] = 10;
        DEFAULT_EXPERIENCE[ 3 ] = 1154;
    }
}