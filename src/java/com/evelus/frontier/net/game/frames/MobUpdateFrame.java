/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game.frames;

import com.evelus.frontier.game.model.Mob;
import com.evelus.frontier.game.update.SceneList;
import com.evelus.frontier.game.update.SceneList.Node;
import com.evelus.frontier.io.Buffer;
import com.evelus.frontier.net.game.OutgoingFrame;
import com.evelus.frontier.util.LinkedArrayList;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public abstract class MobUpdateFrame extends OutgoingFrame {
    
    /**
     * Constructs a new {@link MobUpdateFrame};
     * 
     * @param id The id of the frame.
     */
    public MobUpdateFrame( int id , SceneList sceneList )
    {
        super( id , OutgoingFrame.WORD_SIZE );
        this.sceneList = sceneList;
    }
    
    /**
     * The scene list for this mob update frame.
     */
    private SceneList sceneList;
    
    /**
     * The current amount of active mobs.
     */
    private int activeMobs;
    
    /**
     * The flag for if the end needs to be written.
     */
    private boolean writeEnd;
    
    @Override
    public void encode( Buffer buffer ) 
    {
        if( !buffer.isBitAccessActive() ) {
            buffer.initializeBitOffset();
        }
        writeMovementUpdate( buffer );
        writePopulateUpdate( buffer );
        if( writeEnd ) {
            writePopulateEnd( buffer );
        }
        buffer.resetBitOffset();
        writeEnd = false;
    }
    
    /**
     * Writes the movement updates for all the mobs.
     * 
     * @param buffer The buffer to write the update to.
     */
    private void writeMovementUpdate( Buffer buffer )
    {
        buffer.putBits( activeMobs , 8 );
        if( activeMobs > 0 ) {
            LinkedArrayList<Node> activeNodes = sceneList.getActiveNodes();
            for( Node node : activeNodes ) {
                if( node.getState() == SceneList.NEWLY_ADDED ) {
                    continue;
                }
                Mob mob = getMob( node.getId() );
                if( mob != null ) {
                    if( node.getState() != SceneList.QUEUE_REMOVE ) {
                        int movementHash = mob.getMovementHash();
                        boolean doMaskUpdate = mob.getUpdateHash() != 0;
                        if( doMaskUpdate ) {
                            writeEnd = true;
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
                                node.setState( SceneList.NEWLY_ADDED );
                            }
                        }
                        continue;
                    }
                } else {
                    node.setState( SceneList.QUEUE_REMOVE );
                }
                buffer.putBits( 1 , 1 );
                buffer.putBits( 3 , 2 );
                activeMobs--;
            }
        }
    }
    
    /**
     * Writes the populate update for all the mobs.
     * 
     * @param buffer The buffer to write the update to.
     */
    private void writePopulateUpdate( Buffer buffer )
    {
        LinkedArrayList<Node> activeNodes = sceneList.getActiveNodes();
        if( activeNodes.getSize() > 0 ) {
            for( Node node : activeNodes ) {
                if( node.getState() != SceneList.NEWLY_ADDED ) {
                    continue;
                }
                Mob mob = getMob( node.getId() );
                if( mob != null ) {
                    encodePopulateUpdate( buffer , mob );
                    node.setState( SceneList.ACTIVE );
                    activeMobs++;
                } else {
                    node.setState( SceneList.QUEUE_REMOVE );
                }
            }
        }
    }
    
    /**
     * Sets the flag for if the end of the populate update needs to be written.
     * 
     * @param writeEnd The option for the write end flag.
     */
    public void setWriteEnd( boolean writeEnd )
    {
        this.writeEnd = writeEnd;
    }
    
    /**
     * Encodes a populate update for a mob.
     * 
     * @param buffer The buffer to encode the populate update to.
     * @param mob The mob to populate.
     */
    public abstract void encodePopulateUpdate( Buffer buffer , Mob mob );
    
    /**
     * Writes the end of a population update.
     * @param buffer The buffer to encode the populate end to.
     */
    public abstract void writePopulateEnd( Buffer buffer ); 
    
    /**
     * Gets a mob for its id.
     * 
     * @param id The id of the mob to get.
     * @return The mob.
     */
    public abstract Mob getMob( int id );
    
}
