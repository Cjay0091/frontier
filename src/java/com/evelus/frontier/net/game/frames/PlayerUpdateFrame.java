/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game.frames;

import com.evelus.frontier.game.World;
import com.evelus.frontier.game.model.Mob;
import com.evelus.frontier.game.model.Player;
import com.evelus.frontier.game.model.Position;
import com.evelus.frontier.game.update.SceneList;
import com.evelus.frontier.io.Buffer;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class PlayerUpdateFrame extends MobUpdateFrame {
    
    /**
     * Constructs a new {@link PlayerUpdateFrame};
     * 
     * @param localPlayer The local player for this player update frame.
     * @param sceneList The scene list to use for this mob update frame.
     */
    public PlayerUpdateFrame ( Player localPlayer , SceneList sceneList ) 
    {
        super( 29, sceneList );
        this.localPlayer = localPlayer;
    }
    
    /**
     * The local player for this player update frame.
     */
    private Player localPlayer;
    
    @Override
    public void encode( Buffer buffer ) 
    {
        buffer.initializeBitOffset();
        encodeLocalPlayerMovement( buffer );
        super.encode( buffer );
    }
    
    /**
     * Encodes the local player movement.
     * 
     * @param buffer The buffer to encode the data to.
     */
    private void encodeLocalPlayerMovement( Buffer buffer )
    {
        int movementHash = localPlayer.getMovementHash();
        boolean doMaskUpdate = localPlayer.getUpdateHash() != 0;
        if( doMaskUpdate ) {
            setWriteEnd( true );
        }
        boolean doUpdate = movementHash != 0 || doMaskUpdate;
        buffer.putBits( doUpdate ? 1 : 0 , 1 );
        if( doUpdate ) {
            int type = movementHash & 3;
            buffer.putBits( type , 2 );
            if( type == 1 || type == 2) {
                if( type == 1 ) {
                    buffer.putBits( movementHash >> 2 & 0x7 , 3 );
                } else {
                    buffer.putBits( movementHash >> 2 & 0x7 , 3 );
                    buffer.putBits( movementHash >> 5 , 3 );
                }
                buffer.putBits( doMaskUpdate ? 1 : 0 , 1 );
            } else if( type == 3 ) {
                Position position = localPlayer.getPosition();
                buffer.putBits( position.getMapPositionX() , 7 );
                buffer.putBits( doMaskUpdate ? 1 : 0 , 1 );
                buffer.putBits( position.getHeight() , 2 );
                buffer.putBits( position.getMapPositionY() , 7 );
                buffer.putBits( movementHash >> 2 , 1 );
            }
        }
    }

    @Override
    public void encodePopulateUpdate( Buffer buffer , Mob mob ) 
    {
        buffer.putBits( mob.getId() , 11 );
        boolean maskUpdate = mob.getUpdateHash() != 0;
        if( maskUpdate ) {
            setWriteEnd( true );
        }
        buffer.putBits( maskUpdate ? 1 : 0 , 1 );
        Position localPosition = localPlayer.getPosition();
        Position position = mob.getPosition();
        int deltaX = position.getPositionX() - localPosition.getPositionX();
        if( deltaX < 0 ) {
            deltaX += 32;
        }
        buffer.putBits( deltaX , 5 );
        buffer.putBits( 0 , 1 );
        buffer.putBits( 0 , 3 );
        int deltaY = position.getPositionY() - localPosition.getPositionY();
        if( deltaY < 0 ) {
            deltaY += 32;
        }
        buffer.putBits( deltaY , 5 );
    }

    @Override
    public void writePopulateEnd( Buffer buffer ) 
    {
        buffer.putBits( 2047 , 11 );
    }

    @Override
    public Mob getMob(int id) {
        if( id == localPlayer.getId() ) {
            return null;
        }
        return World.getInstance().getPlayer( id );
    }
}
