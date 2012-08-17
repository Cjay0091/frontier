/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.model.mob;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class WalkingQueue {

    /**
     * The directions for the delta x and y values.
     */
    private static final int[][] DIRECTIONS = new int[ 3 ][ 3 ];

    /**
     * Constructs a new {@link WalkingQueue};
     */
    public WalkingQueue ( int size )
    {
        steps = new Step[ size ];
        this.maximumSize = size;
        initialize( );
    }

    /**
     * The steps in this walking queue.
     */
    private Step[] steps;

    /**
     * The maximum size of this walking queue.
     */
    private int maximumSize;

    /**
     * The current size of this walking queue.
     */
    private int size;

    /**
     * The current offset in the queue.
     */
    private int offset;

    /**
     * Initializes this walking queue.
     */
    private void initialize( )
    {
        for( int i = 0 ; i < maximumSize ; i++ ) {
            steps[ i ] = new Step( );
        }
    }

    /**
     * Queues a new walking step.
     *
     * @param deltaX The delta x of the step.
     * @param deltaY The delta y of the step.
     * @return If the step was successfully queued.
     */
    public boolean queue( int deltaX , int deltaY )
    {
        if( deltaX < -1 || deltaX > 1 || deltaY < -1 || deltaY > 1) {
            throw new RuntimeException();
        }
        if(size < maximumSize) {
            Step step = steps[ size++ ];
            step.deltaX = deltaX;
            step.deltaY = deltaY;
            return true;
        } else
            return false;
    }

    /**
     * Polls a step from this walking queue.
     *
     * @return The polled step.
     */
    public Step poll( )
    {
        Step step = null;
        if( offset >= size ) {
            reset();
        } else if( size > 0 ) {
            step = steps[ offset++ ];
        }
        return step;
    }

    /**
     * Resets this walking queue.
     */
    public void reset( )
    {
        size = offset = 0;
    }

    /**
     * The step class.
     */
    public static class Step
    {

        /**
         * The delta x of the step.
         */
        private int deltaX;
        
        /**
         * The delta y of the step.
         */
        private int deltaY;

        /**
         * Gets the delta x of the step.
         *
         * @return The delta x.
         */
        public int getDeltaX( )
        {
            return deltaX;
        }

        /**
         * Gets the delta y of the step.
         *
         * @return The delta y.
         */
        public int getDeltaY( )
        {
            return deltaY;
        }

        /**
         * Gets the direction of the step.
         *
         * @return The direction.
         */
        public int getDirection( )
        {
            return DIRECTIONS[deltaX + 1][deltaY + 1];
        }

        /**
         * Constructs a new {@link Step};
         */
        private Step ( )
        {
            deltaX = -1;
            deltaY = -1;
        }
    }

    static {
        DIRECTIONS[ 0 ][ 2 ] = 0;
        DIRECTIONS[ 1 ][ 2 ] = 1;
        DIRECTIONS[ 2 ][ 2 ] = 2;
        DIRECTIONS[ 0 ][ 1 ] = 3;
        DIRECTIONS[ 2 ][ 1 ] = 4;
        DIRECTIONS[ 0 ][ 0 ] = 5;
        DIRECTIONS[ 1 ][ 0 ] = 6;
        DIRECTIONS[ 2 ][ 0 ] = 7;
    }
}