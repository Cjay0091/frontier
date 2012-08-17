/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.model;

import com.evelus.frontier.game.model.mob.WalkingQueue;
import com.evelus.frontier.game.model.mob.WalkingQueue.Step;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public abstract class Mob extends Entity {

    /**
     * Constructs a new {@link Mob};
     */
    public Mob ( ) 
    {
        initialize( );
    }

    /**
     * The walking queue for this mob.
     */
    private WalkingQueue walkingQueue;

    /**
     * The movement hash of this mob.
     */
    private int movementHash;

    /**
     * The update hash of this mob.
     */
    private int updateHash;

    /**
     * Flag for if the mob is currently running.
     */
    private boolean isRunning;

    /**
     * Flag for if the mob teleported.
     */
    private boolean teleport;

    /**
     * Initializes this mob.
     */
    private void initialize( )
    {
        walkingQueue = new WalkingQueue( 100 );
    }

    /**
     * Updates the movement.
     */
    public void updateMovement( )
    {
        if( !teleport ) {
            Step step = walkingQueue.poll( );
            if( step != null ) {
                Position position = getPosition( );
                position.add( step.getDeltaX() , step.getDeltaY() );
                movementHash = step.getDirection() << 2;
                if( isRunning && ( step = walkingQueue.poll() ) != null ) {
                    position.add( step.getDeltaX() , step.getDeltaY() );
                    movementHash |= step.getDirection() << 5 | 2;
                } else {
                    movementHash |= 1;
                }
            } else {
                movementHash = 0;
            }
        } else {
            movementHash = 3;
        }
    }

    /**
     * Updates the mob and finalizes everything for a cycle.
     */
    public abstract void update( );
    
    /**
     * Gets the walking queue for this mob.
     * 
     * @return The walking queue.
     */
    public WalkingQueue getWalkingQueue( )
    {
        return walkingQueue;
    }

    /**
     * Sets the movement hash.
     *
     * @param movementHash The movement hash value.
     */
    public void setMovementHash( int movementHash )
    {
        this.movementHash = movementHash;
    }

    /**
     * Gets the movement hash.
     *
     * @return The movement hash.
     */
    public int getMovementHash( )
    {
        return movementHash;
    }

    /**
     * Sets the update hash for this mob.
     *
     * @param updateHash The update hash value.
     */
    public void setUpdateHash( int updateHash )
    {
        this.updateHash = updateHash;
    }
    
    /**
     * Gets the update hash for this mob.
     * 
     * @return The update hash.
     */
    public int getUpdateHash( )
    {
        return updateHash;
    }

    /**
     * Sets if the mob is running.
     *
     * @param isRunning The option for if the mob is running.
     */
    public void setRunning( boolean isRunning )
    {
        this.isRunning = isRunning;
    }

    /**
     * Gets if the mob is running.
     *
     * @return If the mob is running.
     */
    public boolean isRunning( )
    {
        return isRunning;
    }

    /**
     * Sets if the mob has teleported.
     *
     * @param teleport The option for if the mob teleported.
     */
    public void setTeleport( boolean teleport )
    {
        this.teleport = teleport;
    }
}