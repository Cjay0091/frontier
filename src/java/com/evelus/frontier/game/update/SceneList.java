/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.update;

import com.evelus.frontier.game.World;
import com.evelus.frontier.game.model.Mob;
import com.evelus.frontier.game.model.Player;
import com.evelus.frontier.game.model.Position;
import com.evelus.frontier.game.regions.Region;
import com.evelus.frontier.game.regions.RegionHandler;
import com.evelus.frontier.game.regions.Sector;
import com.evelus.frontier.util.LinkedArrayList;
import java.util.Stack;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class SceneList {

    /**
     * The newly added state.
     */
    public static final int NEWLY_ADDED = 0;

    /**
     * The active state.
     */
    public static final int ACTIVE = 1;

    /**
     * The queue remove state.
     */
    public static final int QUEUE_REMOVE = 2;

    /**
     * The players entity type.
     */
    public static final int PLAYERS_TYPE = 0;

    /**
     * Constructs a new {@link SceneList};
     */
    public SceneList ( int entityType , int size , int amountEntities )
    {
        this.entityType = entityType;
        activeEntities = new byte[ amountEntities >> 3 ];
        maximumSize = size;
        initialize( );
    }

    /**
     * The node for this scene list.
     */
    private static class Node
    {
        /**
         * The id of the entity.
         */
        int id;

        /**
         * The index of this node.
         */
        int index;

        /**
         * The current state of the entity.
         */
        byte state;
        
    }

    /**
     * The list of active nodes in this scene list.
     */
    private LinkedArrayList<Node> activeNodes;

    /**
     * The stack of unused nodes in this scene list.
     */
    private Stack<Node> unusedNodes;

    /**
     * The active entities in this scene list.
     */
    private byte[] activeEntities;

    /**
     * The type of entity to capture in this scene list.
     */
    private int entityType;

    /**
     * The maximum size of this scene list.
     */
    private int maximumSize;

    /**
     * Initializes this scene list.
     */
    private void initialize( )
    {
        unusedNodes = new Stack<Node>( );
        for( int i = 0 ; i < maximumSize ; i++ )
            unusedNodes.push(new Node());
        activeNodes = new LinkedArrayList<Node>( maximumSize );
    }

    /**
     * Updates this scene list.
     *
     * @param positionX The position x to update this scene list at.
     * @param positionY The position y to update this scene list at.
     * @param height The height to update this scene list at.
     */
    public void update( int positionX , int positionY , int height )
    {
        if(activeNodes.getSize() > 0) {
            for( Node node : activeNodes ) {
                Mob mob = null;
                if( entityType == PLAYERS_TYPE) {
                    mob = World.getInstance().getPlayer( node.id );
                }
                if( node.state == QUEUE_REMOVE ) {
                    remove( node );
                    continue;
                }
                if( mob != null && withinView( mob.getPosition() , positionX , positionY , height ) )
                    continue;
                node.state = QUEUE_REMOVE;
            }
        }
        for( int sPositionX = (positionX - 16) >> 3 ; sPositionX <= (sPositionX + 16) >> 3 ; sPositionX++ ) {
            for( int sPositionY = (positionY - 16) >> 3 ; sPositionY <= (sPositionY + 16) >> 3 ; sPositionY++ ) {
                if( activeNodes.getSize() >= maximumSize )
                    return;
                int rPositionX = sPositionX >> 3;
                int rPositionY = sPositionY >> 3;
                Region region = RegionHandler.getRegion( rPositionX , rPositionY );
                boolean hasEntities = false;
                if(region != null) {
                    if(entityType == PLAYERS_TYPE) {
                        hasEntities = region.hasPlayers();
                    }
                }
                if(region != null && hasEntities) {
                    int sectorX = sPositionX - (rPositionX << 3);
                    int sectorY = sPositionY - (rPositionY << 3);
                    Sector sector = region.getSector( sectorX, sectorY );
                    if(sector != null) {
                        if(entityType == PLAYERS_TYPE) {
                            hasEntities = sector.hasPlayers();
                        }
                    }
                    if(region == null || !hasEntities)
                        continue;                    
                    if(entityType == PLAYERS_TYPE) {
                        for(Player player : sector.getPlayers()) {
                            if( withinView( player.getPosition() , positionX , positionY , height ) )
                                add( player.getId() );
                        }
                    }
                } else {
                    sPositionY += 7 - (sPositionY - (rPositionY << 3));
                }
            }
        }
    }

    /**
     * Gets if a position is within the view.
     *
     * @param position The positon to check if its within view.
     * @param compareX The comparison x coordinate.
     * @param compareY The comparison y coordinate.
     * @param compareHeight The comparison height.
     * @return If the position is within view.
     */
    private boolean withinView( Position position , int compareX , int compareY , int compareHeight )
    {
        int deltaX = position.getPositionX() - compareX;
        int deltaY = position.getPositionY() - compareY;
        return position.getHeight() == compareHeight && deltaX <= 16 && deltaX > -16 && deltaY <= 16 && deltaY > -16;
    }

    /**
     * Adds a mob to this scene list.
     *
     * @param id The id of the mob to add.
     */
    private void add( int id )
    {
        if((activeEntities[id >> 3] & (1 << id & 7)) != 0)
            return;
        Node node = unusedNodes.pop();
        node.id = id;
        node.state = NEWLY_ADDED;
        node.index = activeNodes.addElement( node );
        activeEntities[ id >> 3 ] |= 1 << id & 7;
    }

    /**
     * Removes a node from this scene list.
     *
     * @param node The node to remove.
     */
    private void remove( Node node )
    {
        int id = node.id;
        activeNodes.removeElement( node.index );
        unusedNodes.push( node );
        activeEntities[ id >> 3 ] &= ~(1 << id & 7);
    }
}
