/**
 * Copyright Evelus, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Hadyn Richard (sini@evel.us), July 2012
 */

package com.evelus.frontier.net.game;

import com.evelus.frontier.Server;
import com.evelus.frontier.net.game.codec.Decoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
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
        if( !Server.registerSession( session ) ) {
            ctx.getChannel().close();
            return;
        }
        session.setHandler( new InitialSessionHandler(session) );
        logger.log(Level.INFO, "Connection accepted [address=" + ctx.getChannel().getRemoteAddress() + "]");
        ctx.getChannel().getPipeline().addFirst( "decoder" , new Decoder( session ) );
        ctx.setAttachment( session );
    }

    @Override
    public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        logger.log(Level.INFO, "Connection closed [address=" + ctx.getChannel().getRemoteAddress() + "]");
        Session session = (Session) ctx.getAttachment();
        Server.unregisterSession( session );
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if( !(e.getMessage() instanceof IncomingFrame) )
            throw new RuntimeException();
        Session session = (Session) ctx.getAttachment();
        IncomingFrame frame = (IncomingFrame) e.getMessage();
        if(frame == IncomingFrame.INVALID_FRAME) {
            session.destroy();
            return;
        }
        session.decodeFrame(frame);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        e.getCause().printStackTrace();
        Session session = (Session) ctx.getAttachment();
        session.destroy();
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
