/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game;

import com.evelus.frontier.Server;
import com.evelus.frontier.net.Session;
import com.evelus.frontier.net.game.codec.Decoder;
import com.evelus.frontier.net.impl.InitialHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * Evelus Development
 * Created by Hadyn Richard
 */
public final class ChannelHandler extends SimpleChannelHandler {

    /**
     * The logger instance for this class.
     */
    private static final Logger logger = Logger.getLogger( ChannelHandler.class.getSimpleName() );

    /**
     * The instance of this class.
     */
    private static ChannelHandler instance;

    /**
     * Construct a new {@link ChannelHandler};
     */
    private ChannelHandler ( ) { }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        Session session = new Session( ctx.getChannel() );
        if( !Server.getInstance().registerSession( session ) ) {
            ctx.getChannel().close();
            return;
        }
        session.setHandler( InitialHandler.getInstance() );
        logger.log(Level.INFO, "Connection accepted [address=" + ctx.getChannel().getRemoteAddress() + "]");
        ctx.getChannel().getPipeline().addFirst( "decoder" , new Decoder( session ) );
        ctx.setAttachment( session );
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        logger.log(Level.INFO, "Connection closed [address=" + ctx.getChannel().getRemoteAddress() + "]");
        Session session = (Session) ctx.getAttachment();
        Server.getInstance().unregisterSession( session );
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if( !(e.getMessage() instanceof Boolean) )
            throw new RuntimeException();
        boolean isValid = (Boolean) e.getMessage();
        if( isValid )
            return;
        ctx.getChannel().close();
    }

    /**
     * Gets the instance of this class.
     *
     * @return The instance.
     */
    public static ChannelHandler getInstance( )
    {
        if( instance == null )
            instance = new ChannelHandler( );
        return instance;
    }
}
