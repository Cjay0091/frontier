/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.model;

import com.evelus.frontier.Constants;
import com.evelus.frontier.game.model.mob.PlayerUpdateBlock;
import com.evelus.frontier.game.update.SceneList;
import com.evelus.frontier.net.game.Session;
import com.evelus.frontier.net.game.frames.RebuildMapFrame;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public class Player extends Mob {

    /**
     * Constructs a new {@link Player};
     */
    public Player ( Session session )
    {
        updatedSectorX = -1;
        updatedSectorY = -1;
        this.session = session;
        initialize( );
    }
    
    /**
     * The session for this player.
     */
    private Session session;

    /**
     * The update block for this player.
     */
    private PlayerUpdateBlock updateBlock;

    /**
     * The player scene list for this player.
     */
    private SceneList playerSceneList;

    /**
     * The updated map sector x coordinate.
     */
    private int updatedSectorX;

    /**
     * The updated map sector y coordinate.
     */
    private int updatedSectorY;

    /**
     * Initializes this player.
     */
    private void initialize( )
    {
        playerSceneList = new SceneList( SceneList.PLAYERS_TYPE , Constants.ENTITIES_IN_VIEW , Constants.AMOUNT_PLAYERS );
    }

    @Override
    public void updateMovement( )
    {
        Position position = getPosition( );
        if( updatedSectorX != -1 && updatedSectorY != -1 ) {
            int sectorX = position.getSectorX();
            int sectorY = position.getSectorY();
            if(sectorX > updatedSectorX + 4 || sectorX <= updatedSectorX - 4 ||
               sectorY > updatedSectorY + 4 || sectorY <= updatedSectorY - 4) {
                rebuildMap( );
            }
        } else {
            updatedSectorX = position.getSectorX();
            updatedSectorY = position.getSectorY();
        }
        super.updateMovement();
    }

    @Override
    public void update( )
    {
        setUpdateHash( updateBlock.getFlags() );
        updateBlock.reset();
    }
    
    /**
     * Calls for a map rebuild.
     */
    public void rebuildMap( )
    {
        Position position = getPosition( );
        updatedSectorX = position.getSectorX();
        updatedSectorY = position.getSectorY();
        session.getChannel().write( new RebuildMapFrame (   position.getSectorX(),
                                                            position.getSectorY(), 
                                                            position.getMapPositionX(), 
                                                            position.getMapPositionY() 
                                                        ));
    }

    /**
     * Updates all the lists for this player.
     */
    public void updateLists( )
    {
        Position position = getPosition( );
        playerSceneList.update( position.getPositionX() , position.getPositionY() , position.getHeight() );
    }
    
    /**
     * Gets the session for this player.
     * 
     * @return The session.
     */
    public Session getSession( )
    {
        return session;
    }
}