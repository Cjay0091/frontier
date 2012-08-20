/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.game.model;

import com.evelus.frontier.Constants;
import com.evelus.frontier.game.model.mob.PlayerUpdateBlock;
import com.evelus.frontier.game.model.player.ItemHandler;
import com.evelus.frontier.game.model.player.ServerBindings;
import com.evelus.frontier.game.model.player.ServerBindingsImpl;
import com.evelus.frontier.game.model.player.SkillHandler;
import com.evelus.frontier.game.model.player.WidgetHandler;
import com.evelus.frontier.game.update.SceneList;
import com.evelus.frontier.net.game.OutgoingFrame;
import com.evelus.frontier.net.game.Session;
import com.evelus.frontier.net.game.frames.CloseWidgetsFrame;
import com.evelus.frontier.net.game.frames.PlayerUpdateFrame;
import com.evelus.frontier.net.game.frames.RebuildMapFrame;
import com.evelus.frontier.net.game.frames.SendMessageFrame;
import com.evelus.frontier.net.game.frames.UpdateTextFrame;
import org.jboss.netty.channel.Channel;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public class GamePlayer extends GameMob implements Player {

    /**
     * Constructs a new {@link Player};
     */
    public GamePlayer ( Session session )
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
     * The player update frame for this player.
     */
    private PlayerUpdateFrame playerUpdateFrame;
    
    /**
     * The item handler for this player.
     */
    private ItemHandler itemHandler;
    
    /**
     * The widget handler for this player.
     */
    private WidgetHandler widgetHandler;

    /**
     * The skill handler for this player.
     */
    private SkillHandler skillHandler;

    /**
     * The server bindings for this player.
     */
    private ServerBindingsImpl serverBindings;

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
        updateBlock = new PlayerUpdateBlock( );
        playerSceneList = new SceneList( SceneList.PLAYERS_TYPE , Constants.ENTITIES_IN_VIEW , Constants.AMOUNT_PLAYERS );
        playerUpdateFrame = new PlayerUpdateFrame( this , playerSceneList );
        itemHandler = new ItemHandler( this );
        widgetHandler = new WidgetHandler( this );
        skillHandler = new SkillHandler( this );
        serverBindings = new ServerBindingsImpl( 500 );
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
        session.getChannel().write( new RebuildMapFrame (   
                                                            position.getSectorX(),
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
     * Writes all the updates for this player.
     */
    public void writeUpdates( )
    {
        Channel channel = session.getChannel();
        channel.write( playerUpdateFrame );
    }
    
    /**
     * Gets the item handler for this player.
     * 
     * @return The item handler.
     */
    public ItemHandler getItemHandler( )
    {
        return itemHandler;
    }
    
    /**
     * Gets the widget handler for this player.
     * 
     * @return The widget handler.
     */
    public WidgetHandler getWidgetHandler( )
    {
        return widgetHandler;
    }

    /**
     * Gets the skill handler for this player.
     *
     * @return The skill handler.
     */
    public SkillHandler getSkillHandler( )
    {
        return skillHandler;
    }

    @Override
    public void sendMessage( String string )
    {
        session.getChannel().write( new SendMessageFrame( string ) );
    }

    @Override
    public void setText( int parentId , int childId , String text )
    {
        session.getChannel().write( new UpdateTextFrame( parentId  , childId  , text  ) );
    }

    @Override
    public void displayWindow( int id )
    {
        widgetHandler.setWindow( id );
    }

    @Override
    public void displayOverlay( int id )
    {
        widgetHandler.setOverlay( id );
    }
    
    @Override
    public void closeOverlay()
    {
        widgetHandler.setOverlay( -1 );
    }

    @Override
    public void closeDisplayedWidgets()
    {
        sendFrame( new CloseWidgetsFrame( ) );
        widgetHandler.closeWidgets( );
    }

    @Override
    public ServerBindings getServerBindings( )
    {
        return serverBindings;
    }
    
    /**
     * Sends a frame to the client of this player.
     * 
     * @param frame The frame to send to the client.
     */
    public void sendFrame( OutgoingFrame frame )
    {
        session.getChannel().write( frame );
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