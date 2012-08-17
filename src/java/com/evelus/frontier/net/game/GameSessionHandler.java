/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game;

import com.evelus.frontier.game.World;
import com.evelus.frontier.game.model.Player;
import com.evelus.frontier.net.game.codec.FrameEncoder;
import com.evelus.frontier.net.game.frames.LogoutFrame;
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
    }

    @Override
    public void destroy( ) 
    {
        World.getInstance().unregisterPlayer( player );
    }
}
