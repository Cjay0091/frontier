/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game;

import com.evelus.frontier.events.items.ItemEvent;
import com.evelus.frontier.events.widgets.ButtonEvent;
import com.evelus.frontier.game.GameWorld;
import com.evelus.frontier.game.items.GameItem;
import com.evelus.frontier.game.items.ItemContainer;
import com.evelus.frontier.game.items.ItemDefinition;
import com.evelus.frontier.game.items.ItemLoader;
import com.evelus.frontier.game.model.GamePlayer;
import com.evelus.frontier.game.widgets.WidgetDefinition;
import com.evelus.frontier.game.widgets.WidgetLoader;
import com.evelus.frontier.listeners.items.ItemListener;
import com.evelus.frontier.listeners.widgets.ButtonListener;
import com.evelus.frontier.net.game.codec.FrameEncoder;
import com.evelus.frontier.net.game.frames.DisplayTabOverlayFrame;
import com.evelus.frontier.net.game.frames.DisplayWidgetFrame;
import com.evelus.frontier.net.game.frames.SendItemsFrame;
import com.evelus.frontier.net.game.frames.SendMessageFrame;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public class GameSessionHandler implements SessionHandler {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger( GameSessionHandler.class.getSimpleName() );
    
    /**
     * Constructs a new {@link GameSessionHandler};
     * 
     * @param player The player for this session handler.
     */
    public GameSessionHandler ( GamePlayer player ) 
    { 
        this.player = player;
    }
    
    /**
     * The player for this session handler.
     */
    private GamePlayer player;
    
    /**
     * Handles a command sent from the client.
     * 
     * @param str The command string.
     */
    public void handleCommand( String str )
    {
        String[] args = str.split(" ");
        try {
            if( args[0].equals("item") ) {
                int id = Integer.parseInt( args[1] );
                ItemDefinition definition = ItemLoader.getInstance().getDefinition( id );
                if( definition == null ) {
                    player.sendFrame( new SendMessageFrame( "No such item exists." ) );
                    return;
                }
                int amount = 1;
                if( args.length > 2 ) {
                    amount = Integer.parseInt( args[2] );
                }
                ItemContainer container = player.getItemHandler().getContainer( 0 );
                if( definition.getStackable() ) {
                    GameItem gameItem = new GameItem( id , amount );
                    if( !container.addItem(gameItem, true) ) {
                        player.sendMessage( "Not enough room in your inventory to complete this action." );
                        return;
                    }
                } else {
                    while( amount-- > 0 ) {
                        GameItem gameItem = new GameItem( id );
                        if( !container.addItem(gameItem, false) ) {
                            player.sendMessage( "Not enough room in your inventory to complete this action." );
                            return;
                        }
                    }
                }
                player.sendFrame( new SendItemsFrame( 149 , 0 , container ));
            } else {
                player.sendMessage( "No such command." );
            }
        } catch( Throwable t ) {
            player.sendMessage( "Error in executing the command." );
        }
    }
    
    /**
     * Handles when a button is clicked.
     * 
     * @param hash The hash of the button clicked.
     */
    public void handleClickButton( int hash )
    {
        if( !player.getWidgetHandler().widgetOpen( hash >> 16 ) ) {
            return;
        }
        int id = WidgetLoader.getInstance().lookup( hash );
        if( id == -1 ) {
            return;
        }
        WidgetDefinition definition = WidgetLoader.getInstance().getDefinition( id );
        if( definition.getType() != WidgetDefinition.BUTTON_TYPE ) {
            logger.log( Level.INFO , "Specified widget is not a button [hash=" + hash + "]" );
            return;
        }
        ButtonEvent event = new ButtonEvent( player );
        ButtonListener listener = WidgetLoader.getInstance().getButtonListener( definition.getButtonId() );
        if( listener != null ) {
            listener.onActivate( event );
        }
    }

    /**
     * Handles equipping an item.
     *
     * @param hash The has of the widget where the item is located.
     * @param itemId The id of the item to equip.
     * @param slot The slot of the item to equip.
     */
    public void handleEquip( int hash , int itemId , int slot )
    {
        if( !player.getWidgetHandler().widgetOpen( hash >> 16 ) ) {
            return;
        }

        int id = WidgetLoader.getInstance().lookup( hash );
        if( id == -1 ) {
            return;
        }

        WidgetDefinition definition = WidgetLoader.getInstance().getDefinition( id );
        if( definition.getType() != WidgetDefinition.CONTAINER_TYPE ) {
            logger.log( Level.INFO , "Specified widget is not a container [hash=" + hash + "]" );
            return;
        }

        ItemContainer container = player.getItemHandler().getContainer( definition.getContainerId() );
        GameItem item = container.getItem( slot );
        if( item == null || item.getId() != itemId ) {
            return;
        }

        ItemDefinition itemDefinition = ItemLoader.getInstance().getDefinition( itemId );
        ItemListener listener = ItemLoader.getInstance().getListener( itemDefinition.getListenerId() );
        if( listener != null ) {
            ItemEvent itemEvent = new ItemEvent( player );
            listener.onEquip( itemEvent );
        }
    }
    
    /**
     * Sends all the initial login information.
     */
    public void sendLogin( )
    {
        Channel channel = player.getSession().getChannel();
        ChannelBuffer channelBuffer = ChannelBuffers.buffer( 6 );
        channelBuffer.writeByte( 2 );
        channelBuffer.writeByte( 2 );
        channelBuffer.writeByte( 0 );
        channelBuffer.writeShort( player.getId() );
        channelBuffer.writeByte( 1 );
        channel.write( channelBuffer );
        channel.getPipeline().addFirst( "frameencoder", new FrameEncoder( player.getSession() ) );
        player.rebuildMap( );
        player.getItemHandler().updateContainers( );
        player.getWidgetHandler().updateTabs( );
        player.getSkillHandler().updateSkills();
        player.sendMessage( "Welcome to Frontier" );
        player.sendFrame( new DisplayWidgetFrame( 0 ) );
    }

    @Override
    public void destroy( ) 
    {
        GameWorld.getInstance().unregisterPlayer( player );
    }
}
