/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game;

import com.evelus.frontier.game.World;
import com.evelus.frontier.game.items.GameItem;
import com.evelus.frontier.game.items.ItemContainer;
import com.evelus.frontier.game.items.ItemDefinition;
import com.evelus.frontier.game.items.ItemLoader;
import com.evelus.frontier.game.model.Player;
import com.evelus.frontier.net.game.codec.FrameEncoder;
import com.evelus.frontier.net.game.frames.SendItemsFrame;
import com.evelus.frontier.net.game.frames.SendMessageFrame;
import com.evelus.frontier.net.game.frames.SetTabWidgetFrame;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public class GameSessionHandler implements SessionHandler {
    
    /**
     * Constructs a new {@link GameSessionHandler};
     * 
     * @param player The player for this session handler.
     */
    public GameSessionHandler ( Player player ) 
    { 
        this.player = player;
    }
    
    /**
     * The player for this session handler.
     */
    private Player player;
    
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
                ItemDefinition definition = ItemLoader.getDefinition( id );
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
                        player.sendFrame( new SendMessageFrame( "Not enough room in your inventory to complete this action." ) );
                        return;
                    }
                } else {
                    while( amount-- > 0 ) {
                        GameItem gameItem = new GameItem( id );
                        if( !container.addItem(gameItem, false) ) {
                            player.sendFrame( new SendMessageFrame( "Not enough room in your inventory to complete this action." ) );
                            return;
                        }
                    }
                }
                player.sendFrame( new SendItemsFrame( 149 , 0 , container ));
            } else {
                player.sendFrame( new SendMessageFrame( "No such command." ) );
            }
        } catch( Throwable t ) {
            player.sendFrame( new SendMessageFrame( "Error in executing the command." ) );
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
        channel.write( new SetTabWidgetFrame( 3 , 149 ) );
        channel.write( new SendMessageFrame( "Welcome to Frontier" ) );
    }

    @Override
    public void destroy( ) 
    {
        World.getInstance().unregisterPlayer( player );
    }
}
